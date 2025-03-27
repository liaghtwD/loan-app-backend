package cyou.oxling.loanappbackend.dto.user;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 贷款历史DTO
 */
@Data
public class LoanHistoryDTO {
    /**
     * 贷款ID
     */
    private Long loanId;
    
    /**
     * 借款金额
     */
    private BigDecimal loanAmount;
    
    /**
     * 状态：0=审核中，1=已放款，2=已结清，3=已逾期，4=已拒绝
     */
    private Integer status;
    
    /**
     * 申请时间
     */
    private Date applyTime;
    
    /**
     * 审批时间
     */
    private Date approveTime;
    
    /**
     * 结清时间
     */
    private Date settleTime;
} 