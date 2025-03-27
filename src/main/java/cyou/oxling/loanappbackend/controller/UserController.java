package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.dto.user.LoginDTO;
import cyou.oxling.loanappbackend.dto.user.RegisterDTO;
import cyou.oxling.loanappbackend.dto.user.ThirdPartyLoginDTO;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import cyou.oxling.loanappbackend.service.UserService;
import cyou.oxling.loanappbackend.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户认证与登录控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Long> register(@RequestBody RegisterDTO registerDTO) {
        return Result.success(userService.register(registerDTO));
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    /**
     * 第三方登录
     * @param thirdPartyLoginDTO 第三方登录信息
     * @return 登录结果
     */
    @PostMapping("/third-party-login")
    public Result<Map<String, Object>> thirdPartyLogin(@RequestBody ThirdPartyLoginDTO thirdPartyLoginDTO) {
        return Result.success(userService.thirdPartyLogin(thirdPartyLoginDTO));
    }

    @PutMapping("/info")
    public Result<Boolean> updateUserInfo(@RequestBody UserInfo userInfo) {
        // 从JWT中获取用户ID
        Long userId = RequestUtil.getCurrentUserId();
        userInfo.setId(userId); // 确保只能修改自己的信息
        
        return Result.success(userService.updateUserInfo(userInfo));
    }

    @PutMapping("/credit")
    public Result<Boolean> updateUserCredit(@RequestBody UserCredit userCredit) {
        // 从JWT中获取用户ID
        Long userId = RequestUtil.getCurrentUserId();
        userCredit.setUserId(userId); // 确保只能修改自己的信用信息
        
        return Result.success(userService.updateUserCredit(userCredit));
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> getUserProfile() {
        // 从JWT中获取用户ID
        Long userId = RequestUtil.getCurrentUserId();
        
        return Result.success(userService.getUserProfile(userId));
    }

    @GetMapping("/info")
    public Result<UserInfo> getUserInfo() {
        // 从JWT中获取用户ID
        Long userId = RequestUtil.getCurrentUserId();
        
        return Result.success(userService.getUserById(userId));
    }
}