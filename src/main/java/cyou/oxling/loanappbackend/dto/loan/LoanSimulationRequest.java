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
     * 是否分期（可选，默认不分期）
     */
    private Boolean installment;
} 