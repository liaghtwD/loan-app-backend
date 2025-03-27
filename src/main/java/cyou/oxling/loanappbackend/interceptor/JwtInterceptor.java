package cyou.oxling.loanappbackend.interceptor;

import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT拦截器
 * 从请求头中解析JWT获取userId
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录当前请求路径
        String requestURI = request.getRequestURI();
        logger.info("JwtInterceptor处理请求: {}", requestURI);
        
        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        logger.info("Authorization头: {}", token);
        
        // 如果请求头没有Authorization，或者不是以Bearer开头，则返回错误
        if (token == null || !token.startsWith("Bearer ")) {
            logger.error("Authorization头不存在或格式不正确: {}", token);
            throw new BusinessException(1001, "未授权，请先登录");
        }
        
        // 去掉Bearer前缀
        token = token.substring(7);
        
        if (!StringUtils.hasText(token)) {
            logger.error("Token为空");
            throw new BusinessException(1001, "未授权，请先登录");
        }
        
        try {
            // 解析token并获取Claims
            Claims claims = jwtUtil.parseToken(token);
            if (claims == null) {
                logger.error("Token解析失败，无法获取Claims");
                throw new BusinessException(1001, "Token无效或已过期");
            }
            
            // 检查token是否过期
            boolean isValid = jwtUtil.validateToken(token);
            if (!isValid) {
                logger.error("Token已过期");
                throw new BusinessException(1001, "Token已过期，请重新登录");
            }
            
            // 从claims中获取userId
            String subject = claims.getSubject();
            if (subject == null) {
                logger.error("Token中不包含subject信息");
                throw new BusinessException(1001, "Token无效，缺少用户信息");
            }
            
            Long userId = null;
            try {
                userId = Long.valueOf(subject);
            } catch (NumberFormatException e) {
                logger.error("userId格式错误: {}", subject);
                throw new BusinessException(1001, "Token中的用户ID格式错误");
            }
            
            // 将userId存入请求属性中，供后续使用
            request.setAttribute("userId", userId);
            logger.info("Token验证成功，userId: {}", userId);
            
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Token处理异常", e);
            throw new BusinessException(1001, "Token验证失败: " + e.getMessage());
        }
    }
} 