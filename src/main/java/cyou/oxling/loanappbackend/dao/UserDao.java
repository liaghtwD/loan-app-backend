package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserDao {
    
    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    UserInfo findByPhone(@Param("phone") String phone);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    UserInfo findById(@Param("id") Long id);
    
    /**
     * 保存用户信息
     * @param userInfo 用户信息
     * @return 影响行数
     */
    int createUserInfo(UserInfo userInfo);
    
    /**
     * 更新用户信息
     * @param userInfo 用户信息
     * @return 影响行数
     */
    int updateUserInfo(UserInfo userInfo);
    
    /**
     * 更新用户信用信息
     * @param userCredit 用户信用信息
     * @return 影响行数
     */
    int updateUserCredit(UserCredit userCredit);
    
    /**
     * 保存用户信用信息
     * @param userCredit 用户信用信息
     * @return 影响行数
     */
    int createUserCredit(UserCredit userCredit);
    
    /**
     * 更新最后登录时间
     * @param userId 用户ID
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("userId") Long userId);
    
    /**
     * 根据第三方openId查询用户
     * @param openId 第三方openId
     * @param type 第三方类型
     * @return 用户信息
     */
    UserInfo findByOpenId(@Param("openId") String openId, @Param("type") String type);
    
    /**
     * 获取用户详细资料
     * @param userId 用户ID
     * @return 用户详细资料
     */
    Map<String, Object> getUserProfile(@Param("userId") Long userId);
    
    /**
     * 获取用户信用信息
     * @param userId 用户ID
     * @return 用户信用信息
     */
    UserCredit getUserCredit(@Param("userId") Long userId);
    
    /**
     * 更新用户当前贷款ID
     * @param userId 用户ID
     * @param loanId 贷款ID
     * @return 影响行数
     */
    int updateUserNowLoan(@Param("userId") Long userId, @Param("loanId") Long loanId);
    
    /**
     * 清除用户当前贷款ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int clearUserNowLoan(@Param("userId") Long userId);
} 