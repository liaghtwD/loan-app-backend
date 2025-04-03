package cyou.oxling.loanappbackend.dto.loan;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 还款请求DTO
 */
@Data
public class RepaymentRequest {
    /**
     * 贷款ID
     */
    private Long loanId;
    
    /**
     * 还款金额
     */
    private BigDecimal amount;
    
    /**
     * 是否提前还款
     */
    private Boolean prepayment;
    
    /**
     * 期数（可选，默认当前期）
     */
    private Integer installmentNo;
} 