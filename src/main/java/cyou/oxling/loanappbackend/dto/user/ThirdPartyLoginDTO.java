package cyou.oxling.loanappbackend.dto.user;

import lombok.Data;

/**
 * 第三方登录请求DTO
 */
@Data
public class ThirdPartyLoginDTO {
    private String openId;
    private String accessToken;
    private String type; // "weixin" or "ali"
} 