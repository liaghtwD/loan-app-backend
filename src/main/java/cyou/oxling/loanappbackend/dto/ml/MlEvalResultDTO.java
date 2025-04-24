package cyou.oxling.loanappbackend.dto.ml;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * ML评估结果DTO
 */
@Data
public class MlEvalResultDTO {
    
    private Long userId;
    private Long snapshotId;
    private Integer creditScore;
    private BigDecimal creditLimit;
    private String modelVer;
    private Date createTime;
    private Date expireTime;
    private boolean expired;  // 是否过期
} 