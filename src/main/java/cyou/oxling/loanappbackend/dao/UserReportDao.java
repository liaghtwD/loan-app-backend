package cyou.oxling.loanappbackend.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import cyou.oxling.loanappbackend.model.user.UserReport;

/**
 * 用户自报信息数据访问接口
 */
@Mapper
public interface UserReportDao {
    
    /**
     * 保存用户自报信息
     * @param userReport 用户自报信息
     * @return 影响行数
     */
    int saveUserReport(UserReport userReport);
    
    /**
     * 根据ID查询用户自报信息
     * @param id ID
     * @return 用户自报信息
     */
    UserReport findById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询最新的用户自报信息
     * @param userId 用户ID
     * @return 用户自报信息
     */
    UserReport findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询所有用户自报信息
     * @param userId 用户ID
     * @return 用户自报信息列表
     */
    List<UserReport> findAllByUserId(@Param("userId") Long userId);
    
    /**
     * 更新用户自报信息
     * @param userReport 用户自报信息
     * @return 影响行数
     */
    int updateUserReport(UserReport userReport);
    
    /**
     * 更新用户自报信息状态
     * @param id ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
} 