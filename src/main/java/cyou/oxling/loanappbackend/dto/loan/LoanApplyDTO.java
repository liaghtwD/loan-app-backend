package cyou.oxling.loanappbackend.dto.loan;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 贷款申请DTO
 */
@Data
public class LoanApplyDTO {
    /**
     * 借款金额
     */
    private BigDecimal loanAmount;
    
    /**
     * 借款期限（月）
     */
    private Integer loanPeriod;
    
    /**
     * 还款方式：1=等额本息，2=等额本金，3=先息后本，4=一次性还本付息
     */
    private Integer repaymentMethod;
} 