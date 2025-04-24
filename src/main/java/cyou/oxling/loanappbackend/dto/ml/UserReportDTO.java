package cyou.oxling.loanappbackend.dto.ml;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 用户自报信息DTO
 */
@Data
public class UserReportDTO {
    
    private Long userId;
    private Integer overdue30pCnt2y;  // 借款人过去2年信用档案中逾期30天以上的违约事件数
    private Integer openCreditLinesCnt;  // 借款人信用档案中未结信用额度的数量
    private Date earliestCreditOpenDate;  // 借款人最早报告的信用额度开立的时间
    private Integer derogCnt;  // 贬损公共记录的数量
    private Integer publicRecordCleanCnt;  // 公开记录清除的数量
    private String housingStatus;  // 借款人在登记时提供的房屋所有权状况
    private String potentialLoanPurpose;  // 潜在借贷预期
    private BigDecimal extEarlyAmtTotal;  // 贷款人提前还款累积金额
    private Integer extEarlyCntTotal;  // 借款人提前还款次数
    private BigDecimal extEarlyAmt3m;  // 近3个月内提前还款金额
} 