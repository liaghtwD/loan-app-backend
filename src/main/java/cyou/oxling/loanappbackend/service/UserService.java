package cyou.oxling.loanappbackend.service;

import cyou.oxling.loanappbackend.dto.user.LoginDTO;
import cyou.oxling.loanappbackend.dto.user.RegisterDTO;
import cyou.oxling.loanappbackend.dto.user.ThirdPartyLoginDTO;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import cyou.oxling.loanappbackend.model.user.UserProfile;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 用户ID
     */
    Long register(RegisterDTO registerDTO);
    
    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录结果（包含token和userId）
     */
    Map<String, Object> login(LoginDTO loginDTO);
    
    /**
     * 第三方登录
     * @param thirdPartyLoginDTO 第三方登录信息
     * @return 登录结果（包含token和userId）
     */
    Map<String, Object> thirdPartyLogin(ThirdPartyLoginDTO thirdPartyLoginDTO);

    /**
     * 更新用户信息
     * @param userInfo 用户信息
     * @return 是否更新成功
     */
    boolean updateUserInfo(UserInfo userInfo);

    /**
     * 更新用户信用信息
     * @param userCredit 用户信用信息
     * @return 是否更新成功
     */
    boolean updateUserCredit(UserCredit userCredit);

    /**
     * 获取用户详细资料
     * @param userId 用户ID
     * @return 用户详细资料
     */
    Map<String, Object> getUserProfile(Long userId);

    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfo getUserById(Long userId);
    
    /**
     * 获取用户完整资料（包括UserInfo, UserProfile和当前贷款）
     * @param userId 用户ID
     * @return 用户完整资料
     */
    Map<String, Object> getUserFullProfile(Long userId);
    
    /**
     * 保存或更新用户拓展资料
     * @param userId 用户ID
     * @param userProfile 用户拓展资料
     * @return 是否保存或更新成功
     */
    boolean saveOrUpdateUserProfile(Long userId, UserProfile userProfile);
    
    /**
     * 获取用户拓展资料
     * @param userId 用户ID
     * @return 用户拓展资料
     */
    UserProfile getUserProfileByUserId(Long userId);
    
    /**
     * 获取用户信用信息
     * @param userId 用户ID
     * @return 用户信用信息
     */
    UserCredit getUserCreditByUserId(Long userId);
    
    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return 是否发送成功
     */
    String sendSmsCode(String phone);
    
    /**
     * 验证短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifySmsCode(String phone, String code);
} 