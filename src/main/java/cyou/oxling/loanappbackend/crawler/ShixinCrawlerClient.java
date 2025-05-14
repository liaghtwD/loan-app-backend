package cyou.oxling.loanappbackend.crawler;

import cyou.oxling.loanappbackend.model.spider.shixin.DispDataItem;
import cyou.oxling.loanappbackend.util.JsonParserUtil;
import cyou.oxling.loanappbackend.util.JsonpUtil;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ShixinCrawlerClient {

    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * 向百度接口发起失信人查询请求
     *
     * @param name   失信人姓名
     * @param idCard 身份证号
     * @return 响应数据 Map
     */
    public Map<String, Object> queryFromCrawler(String name, String idCard) throws Exception {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("sp1.baidu.com")
                .addPathSegment("8aQDcjqpAAV3otqbppnN2DJv")
                .addPathSegment("api.php")
                .addQueryParameter("resource_id", "6899")
                .addQueryParameter("query", "失信被执行人名单")
                .addQueryParameter("cardNum", idCard)
                .addQueryParameter("iname", name)
                .addQueryParameter("areaName", "")
                .addQueryParameter("from_mid", "1")
                .addQueryParameter("ie", "utf-8")
                .addQueryParameter("oe", "utf-8")
                .addQueryParameter("format", "json")
                .addQueryParameter("t", String.valueOf(System.currentTimeMillis()))
                .addQueryParameter("cb", "jQuery110207510308569620578_" + System.currentTimeMillis())
                .addQueryParameter("_", String.valueOf(System.currentTimeMillis()))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                .build();

        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        String responseBody = response.body().string();
        String jsonContent = JsonpUtil.extractJson(responseBody);
        var parsedData = JsonParserUtil.parse(jsonContent);
        DispDataItem data = parsedData.getData().get(0);

        Map<String, Object> result = new HashMap<>();
        if (data.getDisp_data() == null || data.getDisp_data().isEmpty()) {
            result.put("status", "not_found");
            result.put("message", "未找到相关失信被执行人");
        } else {
            result.put("status", "found");
            result.put("message", "此人为失信人！");
            result.put("data", parsedData); // 包含 data.disp_data.* 的详细信息
        }

        return result;
    }
}
