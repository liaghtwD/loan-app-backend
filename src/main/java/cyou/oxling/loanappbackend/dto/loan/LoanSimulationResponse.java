package cyou.oxling.loanappbackend.dto.loan;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 贷款模拟响应DTO
 */
@Data
public class LoanSimulationResponse {
    /**
     * 贷款金额
     */
    private BigDecimal loanAmount;
    
    /**
     * 贷款期限（月）
     */
    private Integer loanPeriod;
    
    /**
     * 还款方式：0=一次性全款；1=分期等额本金；2=分期等额本息
     */
    private Integer repaymentMethod;
    
    /**
     * 利率
     */
    private BigDecimal interestRate;
    
    /**
     * 总还款金额
     */
    private BigDecimal totalRepayment;
    
    /**
     * 等额本金还款计划
     */
    private List<RepaymentPlan> principalRepaymentPlans;
    
    /**
     * 等额本息还款计划
     */
    private List<RepaymentPlan> annuityRepaymentPlans;
    
    /**
     * 还款计划明细
     */
    @Data
    public static class RepaymentPlan {
        /**
         * 期数
         */
        private Integer installmentNo;
        
        /**
         * 应还金额
         */
        private BigDecimal amountDue;
        
        /**
         * 本金
         */
        private BigDecimal principal;
        
        /**
         * 利息
         */
        private BigDecimal interest;
    }
} 