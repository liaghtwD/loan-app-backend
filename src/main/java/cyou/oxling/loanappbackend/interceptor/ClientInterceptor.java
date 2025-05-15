package cyou.oxling.loanappbackend.interceptor;

import cyou.oxling.loanappbackend.util.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author: Chanler
 * @date: 2025/5/15 - 16:37
 */
@Component
public class ClientInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 尝试从多个可能的请求头中获取clientId
        String clientId = request.getHeader("X-Client-Id");

        if (clientId != null && !clientId.isEmpty()) {
            // 存入UserHolder
            UserHolder.setClientId(clientId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清除ThreadLocal中的数据，防止内存泄漏
        UserHolder.clear();
    }
}