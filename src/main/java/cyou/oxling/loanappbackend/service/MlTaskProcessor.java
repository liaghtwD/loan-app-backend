package cyou.oxling.loanappbackend.service;

/**
 * ML任务处理器服务接口
 */
public interface MlTaskProcessor {
    
    /**
     * 增加待处理任务计数
     * 当任务数量从0变为大于0时，会自动激活处理器
     */
    void incrementPendingTaskCount();

} 