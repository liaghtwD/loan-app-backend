package cyou.oxling.loanappbackend.dto.user;

import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
public class LoginDTO {
    private String phone;
    private String passwordOrSmsCode;
    private String loginType; // "password" or "sms"
} 