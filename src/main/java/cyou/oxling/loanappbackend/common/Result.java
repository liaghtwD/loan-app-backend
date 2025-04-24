package cyou.oxling.loanappbackend.common;

/**
 * 通用返回结果类
 * @param <T> 数据类型
 */
public class Result<T> {
    
    private Integer code;
    private String message;
    private T data;
    
    public Result() {
    }
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    
    /**
     * 通用响应方法，用于返回非200成功码的特殊状态响应
     * @param <T> 数据类型
     * @param code 状态码
     * @param message 消息
     * @param data 数据
     * @return 结果对象
     */
    public static <T> Result<T> response(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
} 