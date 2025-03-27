package cyou.oxling.loanappbackend.dto.credit;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户信用信息DTO
 */
@Data
public class UserCreditInfoDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 信用分
     */
    private Integer creditScore;
    
    /**
     * 总额度
     */
    private BigDecimal creditLimit;
    
    /**
     * 已用额度
     */
    private BigDecimal usedCredit;
    
    /**
     * 可用额度
     */
    private BigDecimal availableCredit;
} 