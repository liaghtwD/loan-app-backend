package cyou.oxling.loanappbackend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.dto.credit.CreditStatusDTO;
import cyou.oxling.loanappbackend.dto.ml.UserReportRequestDTO;
import cyou.oxling.loanappbackend.dto.ml.UserReportResponseDTO;
import cyou.oxling.loanappbackend.dto.user.LoginDTO;
import cyou.oxling.loanappbackend.dto.user.RegisterDTO;
import cyou.oxling.loanappbackend.dto.user.SmsCodeDTO;
import cyou.oxling.loanappbackend.dto.user.ThirdPartyLoginDTO;
import cyou.oxling.loanappbackend.model.loan.LoanApplication;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import cyou.oxling.loanappbackend.model.user.UserProfile;
import cyou.oxling.loanappbackend.service.LoanService;
import cyou.oxling.loanappbackend.service.UserService;

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
    public Result<CreditStatusDTO> getUserCredit(@RequestAttribute("userId") Long userId) {
        CreditStatusDTO creditStatus = userService.getUserCreditStatus(userId);
        
        // 根据评估状态处理返回信息
        if (creditStatus.getEvaluating() != null) {
            if (creditStatus.getEvaluating() == UserCredit.EVAL_STATUS_EVALUATING) {
                return Result.response(1, "信用评估中，请稍后查询", creditStatus);
            } else if (creditStatus.getEvaluating() == UserCredit.EVAL_STATUS_WAITING) {
                return Result.response(2, "信用信息已过期，请提交新的信用报告", creditStatus);
            }
        }
        
        // 信用评分系统优化提示
        String message = "信用评估完成";
        if (creditStatus.getCreditScore() != null) {
            if (creditStatus.getCreditScore() >= 80 && creditStatus.getCreditLimit().doubleValue() < 10000) {
                message = "您的信用优良，可享受10万以下额度秒批";
            } else if (creditStatus.getCreditScore() >= 50 && creditStatus.getCreditLimit().doubleValue() < 10000) {
                message = "您的信用良好，可享受10万以下额度秒批";
            }
        }
        
        return Result.success(message, creditStatus);
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

    /**
     * 发送验证码
     */
    @PostMapping("/code")
    public Result<String> sendSmsCode(@RequestBody SmsCodeDTO smsCodeDTO) {
        String code = userService.sendSmsCode(smsCodeDTO.getPhone());
        return Result.success("验证码发送成功：" + code);
    }

    /**
     * 提交用户自报信息
     */
    @PostMapping("/report")
    public Result<UserReportResponseDTO> submitUserReport(@RequestAttribute("userId") Long userId, @RequestBody UserReportRequestDTO reportRequestDTO) {
        UserReportResponseDTO responseDTO = userService.submitUserReport(userId, reportRequestDTO);
        return Result.success("异步评估中，请稍后查询结果", responseDTO);
    }
}