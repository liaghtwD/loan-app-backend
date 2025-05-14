package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.spider.shixin.ShixinRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 失信记录数据访问接口
 */
@Mapper
public interface ShixinRecordDao {

    /**
     * 保存失信记录
     * @param record 待保存的记录
     * @return 影响行数
     */
    int saveShixinRecord(ShixinRecord record);

    /**
     * 更新失信记录
     * @param record 待更新的记录
     * @return 影响行数
     */
    int updateShixinRecord(ShixinRecord record);

    /**
     * 根据身份证号删除失信记录
     * @param idCard 身份证号
     * @return 影响行数
     */
    int deleteByIdCard(@Param("idCard") String idCard);

    /**
     * 根据身份证号查询失信记录
     * @param idCard 身份证号
     * @return 失信记录
     */
    ShixinRecord findByIdCard(@Param("idCard") String idCard);

    /**
     * 查询所有失信记录
     * @return 所有记录列表
     */
    List<ShixinRecord> findAll();

    /**
     * 根据是否为失信人筛选记录
     * @param isShixin 是否为失信人
     * @return 记录列表
     */
    List<ShixinRecord> findByShixinStatus(@Param("isShixin") Integer isShixin);
}
