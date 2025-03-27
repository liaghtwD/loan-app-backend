package cyou.oxling.loanappbackend.model.user;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户信用实体类
 */
@Data
public class UserCredit {
    
    private Long id;
    private Long userId;
    private Integer creditScore;
    private BigDecimal creditLimit;
    private BigDecimal usedCredit;
    private String remark;
    private Date createTime;
    private Date updateTime;
    
    /**
     * 获取可用额度
     * 这个计算不存储在数据库中，而是在查询时计算
     * @return 可用额度
     */
    public BigDecimal getAvailableCredit() {
        if (creditLimit != null && usedCredit != null) {
            return creditLimit.subtract(usedCredit);
        }
        return BigDecimal.ZERO;
    }
} 