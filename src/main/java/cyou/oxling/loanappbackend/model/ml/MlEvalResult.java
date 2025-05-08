package cyou.oxling.loanappbackend.model.ml;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * ML评估结果实体类
 */
@Data
public class MlEvalResult {
    
    private Long id;
    private Long userId;
    private Long snapshotId;  // 关联的特征快照ID
    private Integer creditScore;  // 信用评分
    private BigDecimal creditLimit;  // 信用额度
    private String modelVer;  // 模型版本
    private Date createTime;
    private Date expireTime;  // 创建时间+90天
} 