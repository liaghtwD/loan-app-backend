package cyou.oxling.loanappbackend.dto.user;

import lombok.Data;

/**
 * 用户注册请求DTO
 */
@Data
public class RegisterDTO {
    private String phone;
    private String password;
    private String captcha;
    private String smsCaptcha;
} 