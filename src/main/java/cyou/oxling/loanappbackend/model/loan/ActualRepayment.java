package cyou.oxling.loanappbackend.model.loan;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实际还款记录实体类
 */
@Data
public class ActualRepayment {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 贷款ID
     */
    private Long loanId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 期数
     */
    private Integer installmentNo;
    
    /**
     * 实际还款本金
     */
    private BigDecimal principal;
    
    /**
     * 实际还款利息
     */
    private BigDecimal interest;
    
    /**
     * 实际还款总金额
     */
    private BigDecimal repaymentAmount;
    
    /**
     * 计划还款时间
     */
    private Date repaymentTime;
    
    /**
     * 状态：0=已还；1=提前还款
     */
    private Integer status;
    
    /**
     * 实际还款时间
     */
    private Date actualRepaymentTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 