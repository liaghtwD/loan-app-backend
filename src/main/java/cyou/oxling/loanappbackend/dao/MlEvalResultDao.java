package cyou.oxling.loanappbackend.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import cyou.oxling.loanappbackend.model.ml.MlEvalResult;

/**
 * ML评估结果数据访问接口
 */
@Mapper
public interface MlEvalResultDao {
    
    /**
     * 保存ML评估结果
     * @param mlEvalResult ML评估结果
     * @return 影响行数
     */
    int save(MlEvalResult mlEvalResult);
    
    /**
     * 根据ID查询ML评估结果
     * @param id ID
     * @return ML评估结果
     */
    MlEvalResult findById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询最新的ML评估结果
     * @param userId 用户ID
     * @return ML评估结果
     */
    MlEvalResult findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询所有ML评估结果
     * @param userId 用户ID
     * @return ML评估结果列表
     */
    List<MlEvalResult> findAllByUserId(@Param("userId") Long userId);
    
    /**
     * 根据特征快照ID查询ML评估结果
     * @param snapshotId 特征快照ID
     * @return ML评估结果
     */
    MlEvalResult findBySnapshotId(@Param("snapshotId") Long snapshotId);
} 