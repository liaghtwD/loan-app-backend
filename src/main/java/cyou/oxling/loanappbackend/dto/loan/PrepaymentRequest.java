package cyou.oxling.loanappbackend.dto.loan;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 提前还款请求DTO
 */
@Data
public class PrepaymentRequest {
    /**
     * 贷款ID
     */
    private Long loanId;
    
    /**
     * 还款金额
     */
    private BigDecimal amount;
} 