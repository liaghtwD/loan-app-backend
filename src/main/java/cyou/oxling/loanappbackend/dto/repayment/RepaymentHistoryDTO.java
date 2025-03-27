package cyou.oxling.loanappbackend.dto.repayment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 还款历史DTO
 */
@Data
public class RepaymentHistoryDTO {
    /**
     * 贷款ID
     */
    private Long loanId;
    
    /**
     * 期数
     */
    private Integer installmentNo;
    
    /**
     * 应还金额
     */
    private BigDecimal amountDue;
    
    /**
     * 状态：0=未还，1=已还，2=逾期
     */
    private Integer status;
    
    /**
     * 还款时间
     */
    private Date paidTime;
} 