package cyou.oxling.loanappbackend.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import cyou.oxling.loanappbackend.model.ml.MlFeatureSnapshot;

/**
 * ML特征快照数据访问接口
 */
@Mapper
public interface MlFeatureSnapshotDao {
    
    /**
     * 保存ML特征快照
     * @param mlFeatureSnapshot ML特征快照
     * @return 影响行数
     */
    int save(MlFeatureSnapshot mlFeatureSnapshot);
    
    /**
     * 根据ID查询ML特征快照
     * @param id ID
     * @return ML特征快照
     */
    MlFeatureSnapshot findById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询最新的ML特征快照
     * @param userId 用户ID
     * @return ML特征快照
     */
    MlFeatureSnapshot findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询所有ML特征快照
     * @param userId 用户ID
     * @return ML特征快照列表
     */
    List<MlFeatureSnapshot> findAllByUserId(@Param("userId") Long userId);
} 