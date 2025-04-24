package cyou.oxling.loanappbackend.dto.credit;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 信用状态DTO
 */
@Data
public class CreditStatusDTO {
    private Integer creditScore;  // 信用分
    private BigDecimal creditLimit;  // 信用额度
    private BigDecimal usedCredit;  // 已用额度
    private BigDecimal availableCredit;  // 可用额度
    private Integer evaluating;  // 评估状态: 0稳定状态，1正在评估，2等待评估
    private Date createTime;  // 创建时间
    private Date expireTime;  // 过期时间
    private Boolean expired;  // 是否过期
} 