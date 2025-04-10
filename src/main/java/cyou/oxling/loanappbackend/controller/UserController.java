package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.dto.user.LoginDTO;
import cyou.oxling.loanappbackend.dto.user.RegisterDTO;
import cyou.oxling.loanappbackend.dto.user.ThirdPartyLoginDTO;
import cyou.oxling.loanappbackend.model.loan.LoanApplication;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import cyou.oxling.loanappbackend.model.user.UserProfile;
import cyou.oxling.loanappbackend.service.LoanService;
import cyou.oxling.loanappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private LoanService loanService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Long> register(@RequestBody RegisterDTO registerDTO) {
        return Result.success(userService.register(registerDTO));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    /**
     * 第三方登录
     */
    @PostMapping("/third-party-login")
    public Result<Map<String, Object>> thirdPartyLogin(@RequestBody ThirdPartyLoginDTO thirdPartyLoginDTO) {
        return Result.success(userService.thirdPartyLogin(thirdPartyLoginDTO));
    }

    /**
     * 获取用户基本信息
     */
    @GetMapping("/info")
    public Result<UserInfo> getUserInfo(@RequestAttribute("userId") Long userId) {
        return Result.success(userService.getUserById(userId));
    }

    /**
     * 更新用户基本信息
     */
    @PostMapping("/info")
    public Result<Boolean> updateUserInfo(@RequestAttribute("userId") Long userId, @RequestBody UserInfo userInfo) {
        userInfo.setId(userId); // 确保只能修改自己的信息
        return Result.success(userService.updateUserInfo(userInfo));
    }

    /**
     * 获取用户拓展资料
     */
    @GetMapping("/profile")
    public Result<UserProfile> getUserProfile(@RequestAttribute("userId") Long userId) {
        UserProfile userProfile = userService.getUserProfileByUserId(userId);
        return Result.success(userProfile);
    }

    /**
     * 保存或更新用户拓展资料
     */
    @PostMapping("/profile")
    public Result<Boolean> saveOrUpdateUserProfile(@RequestAttribute("userId") Long userId, @RequestBody UserProfile userProfile) {
        boolean success = userService.saveOrUpdateUserProfile(userId, userProfile);
        return Result.success(success);
    }

    /**
     * 获取用户信用信息
     */
    @GetMapping("/credit")
    public Result<UserCredit> getUserCredit(@RequestAttribute("userId") Long userId) {
        UserCredit userCredit = userService.getUserCreditByUserId(userId);
        return Result.success(userCredit);
    }

    /**
     * 获取用户完整信息（包括基本信息、拓展资料、信用信息和当前贷款）
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> getUserFullProfile(@RequestAttribute("userId") Long userId) {
        Map<String, Object> fullProfile = userService.getUserFullProfile(userId);
        return Result.success(fullProfile);
    }

    /**
     * 获取用户贷款历史记录
     */
    @GetMapping("/loan-history")
    public Result<List<LoanApplication>> getLoanHistory(@RequestAttribute("userId") Long userId) {
        List<LoanApplication> history = loanService.getLoanHistory(userId);
        return Result.success(history);
    }
}