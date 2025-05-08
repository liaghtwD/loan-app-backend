package cyou.oxling.loanappbackend.model.ml;

import java.util.Date;

import lombok.Data;

/**
 * ML特征快照实体类
 */
@Data
public class MlFeatureSnapshot {
    
    private Long id;
    private Long userId;
    private String featureJson;  // 特征JSON字符串
    private String source;  // 来源：self_refresh / loan_apply / nightly_job
    private Date createTime;
    
    // 来源常量
    public static final String SOURCE_SELF_REFRESH = "self_refresh";  // 用户自主刷新
    public static final String SOURCE_LOAN_APPLY = "loan_apply";  // 贷款申请
    public static final String SOURCE_NIGHTLY_JOB = "nightly_job";  // 夜间任务
} 