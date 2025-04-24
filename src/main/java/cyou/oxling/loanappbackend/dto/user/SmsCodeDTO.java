package cyou.oxling.loanappbackend.dto.user;

import lombok.Data;

/**
 * 短信验证码请求DTO
 */
@Data
public class SmsCodeDTO {
    private String phone;
} 