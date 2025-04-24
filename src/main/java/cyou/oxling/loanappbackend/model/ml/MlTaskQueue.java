package cyou.oxling.loanappbackend.model.ml;

import java.util.Date;

import lombok.Data;

/**
 * ML任务队列实体类
 */
@Data
public class MlTaskQueue {
    
    private Long id;
    private Long userId;
    private Long snapshotId;  // 关联的特征快照ID
    private Integer retries;  // 重试次数
    private Integer status;  // 状态：0 待执行 1 成功 2 失败 3 处理中
    private Date nextRunTime;  // 下次运行时间
    private Date createTime;
    
    // 状态常量
    public static final int STATUS_PENDING = 0;  // 待执行
    public static final int STATUS_SUCCESS = 1;  // 成功
    public static final int STATUS_FAILED = 2;  // 失败
    public static final int STATUS_PROCESSING = 3;  // 处理中
} 