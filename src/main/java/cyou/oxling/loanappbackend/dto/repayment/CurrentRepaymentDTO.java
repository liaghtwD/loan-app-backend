package cyou.oxling.loanappbackend.dto.repayment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 当前还款DTO
 */
@Data
public class CurrentRepaymentDTO {
    /**
     * 贷款ID
     */
    private Long loanId;
    
    /**
     * 应还金额
     */
    private BigDecimal amountDue;
    
    /**
     * 到期日
     */
    private Date dueDate;
    
    /**
     * 期数
     */
    private Integer installmentNo;
} 