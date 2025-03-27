package cyou.oxling.loanappbackend.dto.loan;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 贷款列表DTO
 */
@Data
public class LoanListDTO {
    /**
     * 进行中的贷款列表
     */
    private List<ActiveLoanDTO> activeLoans;
    
    /**
     * 已完成的贷款列表
     */
    private List<CompletedLoanDTO> completedLoans;
    
    @Data
    public static class ActiveLoanDTO {
        private Long loanId;
        private BigDecimal loanAmount;
        private Date loanTime;
        private Integer status;
        private BigDecimal repaidAmount;
        private BigDecimal remainingAmount;
        private Date nextDueDate;
        private BigDecimal nextDueAmount;
        private Integer overdueStatus;
        private BigDecimal overdueAmount;
    }
    
    @Data
    public static class CompletedLoanDTO {
        private Long loanId;
        private BigDecimal loanAmount;
        private Date loanTime;
        private Integer status;
        private BigDecimal totalRepaymentAmount;
        private Integer totalInstallments;
        private Date completionTime;
    }
} 