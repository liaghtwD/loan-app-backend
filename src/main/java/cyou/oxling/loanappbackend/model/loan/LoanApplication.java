package cyou.oxling.loanappbackend.model.loan;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 贷款申请实体类
 */
@Data
public class LoanApplication {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 贷款金额
     */
    private BigDecimal loanAmount;
    
    /**
     * 贷款期限（月）
     */
    private Integer loanPeriod;
    
    /**
     * 还款方式：0=一次性全款；1=分期
     */
    private Integer repaymentMethod;
    
    /**
     * 利率
     */
    private BigDecimal interestRate;
    
    /**
     * 贷款用途
     */
    private String loanPurpose;
    
    /**
     * 实际放款金额
     */
    private BigDecimal actualLoanAmount;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 状态：0=审核中；1=已放款；2=已还清；3=逾期；4=拒绝；5=取消
     */
    private Integer status;
    
    /**
     * 申请时间
     */
    private Date applyTime;
    
    /**
     * 审批时间
     */
    private Date approveTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 审核人ID
     */
    private Long reviewerId;
} 