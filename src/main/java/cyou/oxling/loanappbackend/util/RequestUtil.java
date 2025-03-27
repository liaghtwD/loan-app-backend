package cyou.oxling.loanappbackend.util;

import cyou.oxling.loanappbackend.exception.BusinessException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 请求工具类
 */
public class RequestUtil {

    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new BusinessException(1001, "未授权，请先登录");
        }
        
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Object userId = request.getAttribute("userId");
        
        if (userId == null) {
            throw new BusinessException(1001, "未授权，请先登录");
        }
        
        return (Long) userId;
    }
} 