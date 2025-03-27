package cyou.oxling.loanappbackend.model.loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 贷款申请表
 * 
 * 字段说明：
 * id - 主键ID
 * userId - 用户ID，关联user_info表
 * loanAmount - 贷款金额
 * loanPeriod - 贷款期限，单位：月
 * repaymentMethod - 还款方式：0=一次性全款；1=分期
 * interestRate - 利率
 * loanPurpose - 贷款用途
 * actualLoanAmount - 实际放款金额
 * status - 状态：0=审核中；1=已放款；2=已还清；3=逾期；4=拒绝
 * applyTime - 申请时间
 * approveTime - 审批时间，可为空
 * updateTime - 更新时间
 * reviewerId - 审核人ID，若需人工审核，关联admin_user.id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {
    private Long id;
    private Long userId;
    private BigDecimal loanAmount;
    private Integer loanPeriod;
    private Integer repaymentMethod;
    private BigDecimal interestRate;
    private String loanPurpose;
    private BigDecimal actualLoanAmount;
    private Integer status;
    private Date applyTime;
    private Date approveTime;
    private Date updateTime;
    private Long reviewerId;
} 