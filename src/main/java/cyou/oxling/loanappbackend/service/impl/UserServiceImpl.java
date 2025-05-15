package cyou.oxling.loanappbackend.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.getui.push.v2.sdk.api.PushApi;
import cyou.oxling.loanappbackend.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import cyou.oxling.loanappbackend.dao.UserDao;
import cyou.oxling.loanappbackend.dao.UserProfileDao;
import cyou.oxling.loanappbackend.dto.credit.CreditStatusDTO;
import cyou.oxling.loanappbackend.dto.ml.UserReportDTO;
import cyou.oxling.loanappbackend.dto.ml.UserReportRequestDTO;
import cyou.oxling.loanappbackend.dto.ml.UserReportResponseDTO;
import cyou.oxling.loanappbackend.dto.user.LoginDTO;
import cyou.oxling.loanappbackend.dto.user.RegisterDTO;
import cyou.oxling.loanappbackend.dto.user.ThirdPartyLoginDTO;
import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.model.ml.MlEvalResult;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import cyou.oxling.loanappbackend.model.user.UserProfile;
import cyou.oxling.loanappbackend.service.LoanService;
import cyou.oxling.loanappbackend.service.MlService;
import cyou.oxling.loanappbackend.service.UserService;
import cyou.oxling.loanappbackend.util.CodeGeneratorUtil;
import cyou.oxling.loanappbackend.util.JwtUtil;
import cyou.oxling.loanappbackend.util.RedisUtil;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private UserProfileDao userProfileDao;
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private MlService mlService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CodeGeneratorUtil codeGeneratorUtil;

    @Value("${sms.code.expire-seconds:300}")
    private int smsCodeExpireSeconds;

    // Redis中验证码的key前缀
    private static final String SMS_CODE_PREFIX = "sms:code:";
    @Autowired
    private PushApi pushApi;
    @Autowired
    private PushserviceImpl pushserviceImpl;

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterDTO registerDTO) {
        // 1. 验证验证码
        validateCaptcha(registerDTO.getCaptcha());
        
        // 2. 验证短信验证码
        validateSmsCaptcha(registerDTO.getPhone(), registerDTO.getSmsCaptcha());
        
        // 3. 检查手机号是否已注册
        UserInfo existUser = userDao.findByPhone(registerDTO.getPhone());
        if (existUser != null) {
            throw new RuntimeException("该手机号已注册");
        }
        
        // 4. 创建用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhone(registerDTO.getPhone());
        // 密码加密存储
        userInfo.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes()));
        userInfo.setStatus(1); // 正常状态
        userInfo.setDeleted(0); // 未删除
        userInfo.setCreateTime(new Date());
        userInfo.setUpdateTime(new Date());
        
        // 5. 保存用户信息
        userDao.createUserInfo(userInfo);
        
        // 6. 初始化用户信用信息
        UserCredit userCredit = new UserCredit();
        userCredit.setUserId(userInfo.getId());
        userCredit.setCreditScore(50);
        userCredit.setCreditLimit(new BigDecimal("5000.00"));
        userCredit.setUsedCredit(new BigDecimal("0.00"));
        userCredit.setRemark("新用户初始化");
        userCredit.setCreateTime(new Date());
        userCredit.setUpdateTime(new Date());
        userDao.createUserCredit(userCredit);

        Long userId = userInfo.getId();
        pushserviceImpl.bind(UserHolder.getClientId(), userId);
        
        return userId;
    }

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录结果（包含token和userId）
     */
    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        // 1. 根据手机号查询用户
        UserInfo userInfo = userDao.findByPhone(loginDTO.getPhone());
        if (userInfo == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 验证用户状态
        if (userInfo.getStatus() != 1) {
            throw new RuntimeException("账号状态异常");
        }
        
        // 3. 根据登录类型验证密码或短信验证码
        if ("password".equals(loginDTO.getLoginType())) {
            // 密码登录
            String encryptedPassword = DigestUtils.md5DigestAsHex(loginDTO.getPasswordOrSmsCode().getBytes());
            if (!encryptedPassword.equals(userInfo.getPassword())) {
                throw new RuntimeException("密码错误");
            }
        } else if ("sms".equals(loginDTO.getLoginType())) {
            // 短信验证码登录
            validateSmsCaptcha(loginDTO.getPhone(), loginDTO.getPasswordOrSmsCode());
        } else {
            throw new RuntimeException("不支持的登录类型");
        }

        Long userId = userInfo.getId();
        
        // 4. 更新最后登录时间
        userDao.updateLastLoginTime(userId);
        
        // 5. 生成JWT token
        String token = jwtUtil.generateToken(userId);
        
        // 6. 返回登录结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", userId);

        pushserviceImpl.bind(UserHolder.getClientId(), userId);

        return result;
    }

    /**
     * 第三方登录
     * @param thirdPartyLoginDTO 第三方登录信息
     * @return 登录结果（包含token和userId）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> thirdPartyLogin(ThirdPartyLoginDTO thirdPartyLoginDTO) {
        // 1. 验证第三方token有效性（实际项目中需调用第三方API验证）
        validateThirdPartyToken(thirdPartyLoginDTO.getOpenId(), thirdPartyLoginDTO.getAccessToken(), thirdPartyLoginDTO.getType());
        
        // 2. 查询是否已有绑定用户
        UserInfo userInfo = userDao.findByOpenId(thirdPartyLoginDTO.getOpenId(), thirdPartyLoginDTO.getType());
        
        // 3. 如果用户不存在，需要创建用户
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setPhone(null); // 第三方登录初始可能没有手机号
            userInfo.setPassword(DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes())); // 随机密码
            userInfo.setStatus(1); // 正常状态
            userInfo.setDeleted(0); // 未删除
            userInfo.setCreateTime(new Date());
            userInfo.setUpdateTime(new Date());
            userInfo.setLastLoginTime(new Date());
            
            // 保存用户信息
            userDao.createUserInfo(userInfo);
            
            // 创建用户信用信息
            UserCredit userCredit = new UserCredit();
            userCredit.setUserId(userInfo.getId());
            userCredit.setCreditScore(50); // 初始信用分
            userCredit.setCreditLimit(new BigDecimal("500")); // 初始额度
            userCredit.setUsedCredit(BigDecimal.ZERO); // 已用额度为0
            userCredit.setRemark("第三方登录新用户");
            userCredit.setCreateTime(new Date());
            userCredit.setUpdateTime(new Date());
            userDao.createUserCredit(userCredit);
        }
        
        // 4. 更新最后登录时间
        userDao.updateLastLoginTime(userInfo.getId());
        
        // 5. 生成JWT token
        String token = jwtUtil.generateToken(userInfo.getId());
        
        // 6. 返回登录结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", userInfo.getId());
        
        return result;
    }
    
    /**
     * 验证图形验证码
     * @param captcha 验证码
     */
    private void validateCaptcha(String captcha) {
        // 实际项目中需要从缓存中获取验证码并比对
        // 此处简化处理
        if (!"test".equals(captcha)) {
            throw new RuntimeException("验证码错误");
        }
    }
    
    /**
     * 验证短信验证码
     * @param phone 手机号
     * @param smsCaptcha 短信验证码
     */
    private void validateSmsCaptcha(String phone, String smsCaptcha) {
        String key = SMS_CODE_PREFIX + phone;
        String code = redisUtil.getString(key);
        
        if (code == null) {
            throw new RuntimeException("验证码已过期或不存在");
        }
        
        if (!code.equals(smsCaptcha)) {
            throw new RuntimeException("短信验证码错误");
        }
        
        // 验证成功后删除验证码
        redisUtil.delete(key);
    }
    
    /**
     * 验证第三方token有效性
     * @param openId 第三方openId
     * @param accessToken 第三方accessToken
     * @param type 第三方类型
     */
    private void validateThirdPartyToken(String openId, String accessToken, String type) {
        // 实际项目中需要调用第三方API验证token有效性
        // 此处简化处理，假设token有效
    }

    @Override
    public boolean updateUserInfo(UserInfo userInfo) {
        if (userInfo == null || userInfo.getId() == null) {
            throw new BusinessException("用户信息不能为空");
        }
        
        // 验证用户是否存在
        UserInfo existingUser = userDao.findById(userInfo.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 如果更新手机号，需要验证新手机号是否已被使用
        if (userInfo.getPhone() != null && !userInfo.getPhone().equals(existingUser.getPhone())) {
            UserInfo phoneUser = userDao.findByPhone(userInfo.getPhone());
            if (phoneUser != null) {
                throw new BusinessException("手机号已被使用");
            }
        }
        
        // 如果更新密码，需要对密码进行加密
        if (userInfo.getPassword() != null && userInfo.getPassword().length() > 0) {
            userInfo.setPassword(DigestUtils.md5DigestAsHex(userInfo.getPassword().getBytes()));
        }
        
        return userDao.updateUserInfo(userInfo) > 0;
    }

    @Override
    public boolean updateUserCredit(UserCredit userCredit) {
        if (userCredit == null || userCredit.getUserId() == null) {
            throw new BusinessException("用户信用信息不能为空");
        }
        
        // 验证用户是否存在
        UserInfo existingUser = userDao.findById(userCredit.getUserId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证信用额度和已用额度的合理性
        if (userCredit.getUsedCredit() != null && userCredit.getCreditLimit() != null) {
            if (userCredit.getUsedCredit().compareTo(userCredit.getCreditLimit()) > 0) {
                throw new BusinessException("已用额度不能大于信用额度");
            }
        }
        
        return userDao.updateUserCredit(userCredit) > 0;
    }

    @Override
    public Map<String, Object> getUserProfile(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        Map<String, Object> userProfile = userDao.getUserProfile(userId);
        if (userProfile == null || userProfile.isEmpty()) {
            throw new BusinessException("用户不存在");
        }
        
        return userProfile;
    }

    @Override
    public UserInfo getUserById(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        
        return userInfo;
    }

    @Override
    public Map<String, Object> getUserFullProfile(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 获取用户基本信息
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 敏感信息脱敏处理 - UserInfo
        userInfo.setPassword(null); // 不传递密码信息
        result.put("userInfo", userInfo);
        
        // 获取用户拓展资料
        UserProfile userProfile = userProfileDao.findByUserId(userId);
        if (userProfile != null) {
            // 敏感信息脱敏处理 - UserProfile
            if (userProfile.getIdCardNo() != null && userProfile.getIdCardNo().length() > 8) {
                // 身份证号码脱敏处理，保留前4位和后4位
                String idCardNo = userProfile.getIdCardNo();
                String maskedIdCardNo = idCardNo.substring(0, 4) + "********" + idCardNo.substring(idCardNo.length() - 4);
                userProfile.setIdCardNo(maskedIdCardNo);
            }
            
            if (userProfile.getBankCardNo() != null && userProfile.getBankCardNo().length() > 8) {
                // 银行卡号脱敏处理，只保留后4位
                String bankCardNo = userProfile.getBankCardNo();
                String maskedBankCardNo = "************" + bankCardNo.substring(bankCardNo.length() - 4);
                userProfile.setBankCardNo(maskedBankCardNo);
            }
            
            result.put("userProfile", userProfile);
        }
        
        // 获取用户当前贷款信息
        if (userInfo.getNowLoan() != null) {
            Map<String, Object> currentLoan = loanService.getLoanDetailFull(userInfo.getNowLoan());
            if (currentLoan != null) {
                result.put("currentLoan", currentLoan);
            }
        }
        
        // // 获取用户信用信息
        // UserCredit userCredit = userDao.getUserCredit(userId);
        // if (userCredit != null) {
        //     result.put("userCredit", userCredit);
        // }

        // 获取信用评估状态
        CreditStatusDTO creditStatus = getUserCreditStatus(userId);

        // 根据评估状态添加提示信息
        String creditMessage = null;
        if (creditStatus.getEvaluating() != null) {
            if (creditStatus.getEvaluating() == UserCredit.EVAL_STATUS_EVALUATING) {
                creditMessage = "信用评估中，请稍后查询";
            } else if (creditStatus.getEvaluating() == UserCredit.EVAL_STATUS_WAITING) {
                creditMessage = "信用信息已过期，请提交新的信用报告";
            } else if (creditStatus.getCreditScore() != null) {
                if (creditStatus.getCreditScore() >= 80) {
                    creditMessage = "您的信用优良，可享受20万以下申请秒批";
                } else if (creditStatus.getCreditScore() >= 50) {
                    creditMessage = "您的信用良好，可享受10万以下申请秒批";
                } else {
                    creditMessage = "信用评估完成";
                }
            }
        }
        
        // 添加信用评估状态
        Map<String, Object> creditStatusMap = new HashMap<>();
        creditStatusMap.put("status", creditStatus);
        if (creditMessage != null) {
            creditStatusMap.put("message", creditMessage);
        }

        result.put("creditStatus", creditStatusMap);
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateUserProfile(Long userId, UserProfile userProfile) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 设置用户ID到userProfile
        userProfile.setUserId(userId);
        
        // 查询是否已有userProfile
        UserProfile existingProfile = userProfileDao.findByUserId(userId);
        
        if (existingProfile == null) {
            // 不存在则创建新的
            userProfile.setCreateTime(new Date());
            userProfile.setUpdateTime(new Date());
            return userProfileDao.createUserProfile(userProfile) > 0;
        } else {
            // 存在则更新
            userProfile.setId(existingProfile.getId());
            userProfile.setUpdateTime(new Date());
            return userProfileDao.updateUserProfile(userProfile) > 0;
        }
    }
    
    @Override
    public UserProfile getUserProfileByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        UserProfile userProfile = userProfileDao.findByUserId(userId);
        
        // 敏感信息脱敏处理
        if (userProfile != null) {
            if (userProfile.getIdCardNo() != null && userProfile.getIdCardNo().length() > 8) {
                // 身份证号码脱敏处理，保留前4位和后4位
                String idCardNo = userProfile.getIdCardNo();
                String maskedIdCardNo = idCardNo.substring(0, 4) + "********" + idCardNo.substring(idCardNo.length() - 4);
                userProfile.setIdCardNo(maskedIdCardNo);
            }
            
            if (userProfile.getBankCardNo() != null && userProfile.getBankCardNo().length() > 8) {
                // 银行卡号脱敏处理，只保留后4位
                String bankCardNo = userProfile.getBankCardNo();
                String maskedBankCardNo = "************" + bankCardNo.substring(bankCardNo.length() - 4);
                userProfile.setBankCardNo(maskedBankCardNo);
            }
        }
        
        return userProfile;
    }
    
    @Override
    public UserCredit getUserCreditByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return userDao.getUserCredit(userId);
    }

    @Override
    public String sendSmsCode(String phone) {
        // 手机号格式验证
        if (phone == null || phone.length() != 11) {
            throw new BusinessException("请输入有效的手机号");
        }
        
        // 生成6位随机验证码
        String code = codeGeneratorUtil.generateSmsCode();
        
        // 保存验证码到Redis，设置过期时间
        String key = SMS_CODE_PREFIX + phone;
        redisUtil.setString(key, code, smsCodeExpireSeconds, TimeUnit.SECONDS);
        
        // 利用app通知模拟验证码发送
        pushserviceImpl.clientPush(UserHolder.getClientId(),"验证码为: " + code, "");
        return code;
    }

    @Override
    public boolean verifySmsCode(String phone, String code) {
        if (phone == null || code == null) {
            return false;
        }
        
        String key = SMS_CODE_PREFIX + phone;
        String savedCode = redisUtil.getString(key);
        
        if (savedCode == null) {
            return false;
        }
        
        boolean verified = savedCode.equals(code);
        
        // 验证成功或失败都删除验证码
        redisUtil.delete(key);
        
        return verified;
    }

    @Override
    public UserReportResponseDTO submitUserReport(Long userId, UserReportRequestDTO reportRequestDTO) {
        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 构建UserReportDTO对象
        UserReportDTO userReportDTO = new UserReportDTO();
        userReportDTO.setUserId(userId);
        
        // 从DTO中提取数据
        userReportDTO.setOverdue30pCnt2y(reportRequestDTO.getOverdue30pCnt2y());
        userReportDTO.setOpenCreditLinesCnt(reportRequestDTO.getOpenCreditLinesCnt());
        
        // 转换日期字符串为Date
        if (reportRequestDTO.getEarliestCreditOpenDate() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                userReportDTO.setEarliestCreditOpenDate(sdf.parse(reportRequestDTO.getEarliestCreditOpenDate()));
            } catch (Exception e) {
                throw new BusinessException("日期格式不正确");
            }
        }
        
        userReportDTO.setDerogCnt(reportRequestDTO.getDerogCnt());
        userReportDTO.setPublicRecordCleanCnt(reportRequestDTO.getPublicRecordCleanCnt());
        userReportDTO.setHousingStatus(reportRequestDTO.getHousingStatus());
        userReportDTO.setPotentialLoanPurpose(reportRequestDTO.getPotentialLoanPurpose());
        userReportDTO.setExtEarlyAmtTotal(reportRequestDTO.getExtEarlyAmtTotal());
        userReportDTO.setExtEarlyCntTotal(reportRequestDTO.getExtEarlyCntTotal());
        userReportDTO.setExtEarlyAmt3m(reportRequestDTO.getExtEarlyAmt3m());
        
        // 提交用户自报信息
        Long snapshotId = mlService.submitUserReport(userReportDTO);
        
        // 返回结果
        UserReportResponseDTO responseDTO = new UserReportResponseDTO();
        responseDTO.setSnapshotId(snapshotId);
        
        return responseDTO;
    }
    
    @Override
    public CreditStatusDTO getUserCreditStatus(Long userId) {
        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 获取用户信用信息
        UserCredit userCredit = userDao.getUserCredit(userId);
        
        CreditStatusDTO creditStatusDTO = new CreditStatusDTO();
        
        if (userCredit != null) {
            // 添加信用信息
            creditStatusDTO.setCreditScore(userCredit.getCreditScore());
            creditStatusDTO.setCreditLimit(userCredit.getCreditLimit());
            creditStatusDTO.setUsedCredit(userCredit.getUsedCredit());
            creditStatusDTO.setAvailableCredit(userCredit.getAvailableCredit());
            
            // 添加评估状态
            int evaluating = userCredit.getEvaluating() != null ? userCredit.getEvaluating() : UserCredit.EVAL_STATUS_WAITING;
            creditStatusDTO.setEvaluating(evaluating);
            
            // 获取过期信息
            MlEvalResult latestEvalResult = mlService.getLatestEvalResult(userId);
            if (latestEvalResult != null) {
                creditStatusDTO.setCreateTime(latestEvalResult.getCreateTime());
                creditStatusDTO.setExpireTime(latestEvalResult.getExpireTime());
                
                // 检查是否过期
                boolean expired = latestEvalResult.getExpireTime() != null && 
                                 latestEvalResult.getExpireTime().before(new Date());
                creditStatusDTO.setExpired(expired);
                
                // 如果过期且状态不是正在评估，则更新用户信用状态为等待评估
                if (expired && evaluating != UserCredit.EVAL_STATUS_EVALUATING) {
                    userCredit.setEvaluating(UserCredit.EVAL_STATUS_WAITING);
                    userCredit.setUpdateTime(new Date());
                    userDao.updateUserCredit(userCredit);
                    creditStatusDTO.setEvaluating(UserCredit.EVAL_STATUS_WAITING);
                }
            } else {
                creditStatusDTO.setExpired(true);
            }
        } else {
            // 没有信用信息
            creditStatusDTO.setEvaluating(UserCredit.EVAL_STATUS_WAITING);
            creditStatusDTO.setExpired(true);
        }
        
        return creditStatusDTO;
    }
} 