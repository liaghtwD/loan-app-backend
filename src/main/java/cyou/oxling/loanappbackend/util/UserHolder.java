package cyou.oxling.loanappbackend.util;

/**
 * 线程存储
 * @author: Chanler
 * @date: 2025/5/15 - 16:10
 */
public class UserHolder {
    // 存储设备ID的ThreadLocal
    private static final ThreadLocal<String> clientIdThreadLocal = new ThreadLocal<>();

    /**
     * 设置设备ID
     */
    public static void setClientId(String clientId) {
        clientIdThreadLocal.set(clientId);
    }

    /**
     * 获取设备ID
     */
    public static String getClientId() {
        return clientIdThreadLocal.get();
    }

    /**
     * 清除当前线程存储的用户信息
     */
    public static void clear() {
        clientIdThreadLocal.remove();
    }
}
