package cyou.oxling.loanappbackend.model.loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实际还款记录表
 * 
 * 字段说明：
 * id - 主键ID
 * loanId - 贷款ID，关联loan_application.id
 * userId - 用户ID，关联user_info.id
 * installmentNo - 第几期
 * repaymentAmount - 实际还款金额
 * repaymentDate - 还款时间
 * status - 状态：0=已还；1=提前还款
 * payTime - 实际还款时间，可选
 * updateTime - 更新时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualRepayment {
    private Long id;
    private Long loanId;
    private Long userId;
    private Integer installmentNo;
    private BigDecimal repaymentAmount;
    private Date repaymentDate;
    private Integer status;
    private Date payTime;
    private Date updateTime;
} 