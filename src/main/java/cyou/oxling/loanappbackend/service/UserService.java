package cyou.oxling.loanappbackend.service;

import cyou.oxling.loanappbackend.dto.user.LoginDTO;
import cyou.oxling.loanappbackend.dto.user.RegisterDTO;
import cyou.oxling.loanappbackend.dto.user.ThirdPartyLoginDTO;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.model.user.UserInfo;

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
} 