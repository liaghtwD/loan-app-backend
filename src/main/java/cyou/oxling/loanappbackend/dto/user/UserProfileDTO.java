package cyou.oxling.loanappbackend.dto.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户个人信息DTO
 */
@Data
public class UserProfileDTO {
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 信用分
     */
    private Integer creditScore;
    
    /**
     * 信用额度
     */
    private BigDecimal creditLimit;
    
    /**
     * 已用额度
     */
    private BigDecimal usedCredit;
} 