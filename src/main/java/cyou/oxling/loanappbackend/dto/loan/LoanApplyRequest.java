package cyou.oxling.loanappbackend.dto.loan;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 贷款申请请求DTO
 */
@Data
public class LoanApplyRequest {
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
     * 贷款用途
     */
    private String loanPurpose;
} 