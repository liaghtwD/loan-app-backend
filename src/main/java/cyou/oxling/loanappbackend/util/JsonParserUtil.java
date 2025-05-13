package cyou.oxling.loanappbackend.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.oxling.loanappbackend.model.spider.shixin.ResponseData;


public class JsonParserUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    // 静态初始化块，配置 ObjectMapper 行为
    static {
        // 关闭“遇到未知字段就报错”的行为
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ResponseData parse(String json) throws Exception {
        return mapper.readValue(json, ResponseData.class);
    }
}
