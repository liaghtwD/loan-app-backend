package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.user.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户拓展信息数据访问接口
 */
@Mapper
public interface UserProfileDao {
    
    /**
     * 根据用户ID查询用户资料
     * @param userId 用户ID
     * @return 用户资料
     */
    UserProfile findByUserId(@Param("userId") Long userId);
    
    /**
     * 保存用户资料
     * @param userProfile 用户资料
     * @return 影响行数
     */
    int createUserProfile(UserProfile userProfile);
    
    /**
     * 更新用户资料
     * @param userProfile 用户资料
     * @return 影响行数
     */
    int updateUserProfile(UserProfile userProfile);
    
    /**
     * 根据用户ID删除用户资料
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);
} 