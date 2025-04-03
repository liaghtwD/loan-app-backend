package cyou.oxling.loanappbackend.model.loan;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 还款计划实体类
 */
@Data
public class RepaymentSchedule {
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
     * 应还金额
     */
    private BigDecimal amountDue;
    
    /**
     * 到期日
     */
    private Date dueDate;
    
    /**
     * 状态：0=未还；1=预期；2=逾期
     */
    private Integer status;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 