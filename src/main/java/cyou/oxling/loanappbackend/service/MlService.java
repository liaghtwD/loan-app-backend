package cyou.oxling.loanappbackend.service;

import cyou.oxling.loanappbackend.dto.ml.MlEvalResultDTO;
import cyou.oxling.loanappbackend.dto.ml.UserReportDTO;
import cyou.oxling.loanappbackend.model.ml.MlEvalResult;
import cyou.oxling.loanappbackend.model.ml.MlFeatureSnapshot;

/**
 * ML服务接口
 */
public interface MlService {
    
    /**
     * 提交用户自报信息
     * @param userReportDTO 用户自报信息
     * @return 特征快照ID
     */
    Long submitUserReport(UserReportDTO userReportDTO);
    
    /**
     * 获取用户信用评估结果
     * @param userId 用户ID
     * @return 用户信用评估结果
     */
    MlEvalResultDTO getUserCredit(Long userId);
    
    /**
     * 创建特征快照
     * @param userId 用户ID
     * @param source 来源
     * @return 特征快照
     */
    MlFeatureSnapshot createFeatureSnapshot(Long userId, String source);
    
    /**
     * 创建评估任务
     * @param userId 用户ID
     * @param snapshotId 特征快照ID
     * @return 任务ID
     */
    Long createEvalTask(Long userId, Long snapshotId);
    
    /**
     * 处理评估任务
     * @param taskId 任务ID
     * @return 是否处理成功
     */
    boolean processEvalTask(Long taskId);
    
    /**
     * 更新用户信用信息
     * @param evalResult 评估结果
     * @return 是否更新成功
     */
    boolean updateUserCredit(MlEvalResult evalResult);
    
    /**
     * 获取最新的评估结果
     * @param userId 用户ID
     * @return 最新的评估结果
     */
    MlEvalResult getLatestEvalResult(Long userId);
} 