package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.spider.lpr.LprRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * LPR 记录数据访问接口
 */
@Mapper
public interface LprRecordDao {

    /**
     * 查询所有 LPR 记录
     * @return LPR记录列表
     */
    List<LprRecord> findAll();

    /**
     * 根据发布日期查询 LPR 记录
     * @param publishDate 发布日期
     * @return LPR记录
     */
    LprRecord findByDate(@Param("publishDate") LocalDate publishDate);

    /**
     * 保存 LPR记录
     * @param record LPR记录
     * @return 影响行数
     */
    int save(LprRecord record);
}
