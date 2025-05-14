package cyou.oxling.loanappbackend.util;

public class JsonpUtil {

    /**
     * 提取 JSONP 响应中的 JSON 字符串
     */
    public static String extractJson(String jsonpData) {
        int startIndex = jsonpData.indexOf('{');
        int endIndex = jsonpData.lastIndexOf('}');
        if (startIndex != -1 && endIndex != -1) {
            return jsonpData.substring(startIndex, endIndex + 1);
        }
        throw new IllegalArgumentException("Invalid JSONP format: " + jsonpData);
    }
}
