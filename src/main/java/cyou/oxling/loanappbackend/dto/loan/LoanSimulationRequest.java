package cyou.oxling.loanappbackend.dto.loan;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 贷款模拟请求DTO
 */
@Data
public class LoanSimulationRequest {
    /**
     * 贷款金额
     */
    private BigDecimal loanAmount;
    
    /**
     * 贷款期限（月）（可选，默认6个月）
     */
    private Integer loanPeriod;
    
    /**
     * 还款方式：0=一次性全款；1=分期等额本金；2=分期等额本息（可选，默认0）
     */
    private Integer repaymentMethod;
} 