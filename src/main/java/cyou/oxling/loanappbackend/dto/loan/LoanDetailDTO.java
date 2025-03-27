package cyou.oxling.loanappbackend.dto.loan;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 贷款详情DTO
 */
@Data
public class LoanDetailDTO {
    /**
     * 贷款ID
     */
    private Long loanId;
    
    /**
     * 借款金额
     */
    private BigDecimal loanAmount;
    
    /**
     * 借款期限（月）
     */
    private Integer loanPeriod;
    
    /**
     * 贷款状态：0=审核中，1=已放款，2=已结清，3=已逾期，4=已拒绝
     */
    private Integer status;
    
    /**
     * 还款方式：1=等额本息，2=等额本金，3=先息后本，4=一次性还本付息
     */
    private Integer repaymentMethod;
    
    /**
     * 还款计划列表
     */
    private List<RepaymentScheduleDTO> repaymentSchedules;
    
    @Data
    public static class RepaymentScheduleDTO {
        /**
         * 期数
         */
        private Integer installmentNo;
        
        /**
         * 应还金额
         */
        private BigDecimal amountDue;
        
        /**
         * 到期日
         */
        private Date dueDate;
        
        /**
         * 状态：0=未还，1=已还，2=逾期
         */
        private Integer status;
    }
} 