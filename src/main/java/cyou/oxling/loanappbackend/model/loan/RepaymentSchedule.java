package cyou.oxling.loanappbackend.model.loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 还款计划表
 * 
 * 字段说明：
 * id - 主键ID
 * loanId - 贷款ID，关联loan_application.id
 * userId - 用户ID，关联user_info.id
 * installmentNo - 第几期
 * amountDue - 当期应还
 * dueDate - 当期到期日
 * status - 状态：0=未还；1=预期；2=逾期
 * updateTime - 更新时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentSchedule {
    private Long id;
    private Long loanId;
    private Long userId;
    private Integer installmentNo;
    private BigDecimal amountDue;
    private Date dueDate;
    private Integer status;
    private Date updateTime;
} 