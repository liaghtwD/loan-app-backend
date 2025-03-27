package cyou.oxling.loanappbackend.dto.repayment;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 还款请求DTO
 */
@Data
public class RepaymentRequestDTO {
    /**
     * 贷款ID
     */
    private Long loanId;
    
    /**
     * 期数
     */
    private Integer installmentNo;
    
    /**
     * 还款金额
     */
    private BigDecimal payAmount;
} 