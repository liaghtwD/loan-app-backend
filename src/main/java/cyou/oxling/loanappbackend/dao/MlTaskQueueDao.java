package cyou.oxling.loanappbackend.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import cyou.oxling.loanappbackend.model.ml.MlTaskQueue;

/**
 * ML任务队列数据访问接口
 */
@Mapper
public interface MlTaskQueueDao {
    
    /**
     * 保存ML任务队列
     * @param mlTaskQueue ML任务队列
     * @return 影响行数
     */
    int save(MlTaskQueue mlTaskQueue);
    
    /**
     * 根据ID查询ML任务队列
     * @param id ID
     * @return ML任务队列
     */
    MlTaskQueue findById(@Param("id") Long id);
    
    /**
     * 根据用户ID和特征快照ID查询ML任务队列
     * @param userId 用户ID
     * @param snapshotId 特征快照ID
     * @return ML任务队列
     */
    MlTaskQueue findByUserIdAndSnapshotId(@Param("userId") Long userId, @Param("snapshotId") Long snapshotId);
    
    /**
     * 查询待执行的任务
     * @param status 状态
     * @param currentTime 当前时间
     * @param limit 限制数量
     * @return ML任务队列列表
     */
    List<MlTaskQueue> findPendingTasks(@Param("status") Integer status, @Param("currentTime") Date currentTime, @Param("limit") Integer limit);
    
    /**
     * 计算待处理任务数量
     * @param status 状态
     * @param currentTime 当前时间
     * @return 待处理任务数量
     */
    Long countPendingTasks(@Param("status") Integer status, @Param("currentTime") Date currentTime);
    
    /**
     * 更新任务状态
     * @param id ID
     * @param status 状态
     * @param retries 重试次数
     * @param nextRunTime 下次运行时间
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("retries") Integer retries, @Param("nextRunTime") Date nextRunTime);
    
    /**
     * 更新任务状态，仅当当前状态匹配时才更新（乐观锁）
     * @param id 任务ID
     * @param expectedStatus 期望的当前状态
     * @param newStatus 新状态
     * @param retries 当前重试次数
     * @return 影响的行数
     */
    int updateStatusIfMatch(@Param("id") Long id, 
                           @Param("expectedStatus") Integer expectedStatus, 
                           @Param("newStatus") Integer newStatus, 
                           @Param("retries") Integer retries);
} 