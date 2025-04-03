package cyou.oxling.loanappbackend.dto.loan;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 贷款更新请求DTO
 */
@Data
public class LoanUpdateRequest {
    /**
     * 贷款ID
     */
    private Long loanId;
    
    /**
     * 贷款金额（可选）
     */
    private BigDecimal loanAmount;
    
    /**
     * 贷款期限（月）（可选）
     */
    private Integer loanPeriod;
    
    /**
     * 还款方式：0=一次性全款；1=分期（可选）
     */
    private Integer repaymentMethod;
    
    /**
     * 贷款用途（可选）
     */
    private String loanPurpose;
    
    /**
     * 状态：0=审核中；1=已放款；2=已还清；3=逾期；4=拒绝；5=取消（可选）
     */
    private Integer status;
} 