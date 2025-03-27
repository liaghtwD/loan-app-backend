package cyou.oxling.loanappbackend.service.impl;

import cyou.oxling.loanappbackend.dao.UserDao;
import cyou.oxling.loanappbackend.dto.credit.UserCreditInfoDTO;
import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import cyou.oxling.loanappbackend.service.CreditService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 信用服务实现类
 */
@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserCreditInfoDTO getUserCreditInfo(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }

        Map<String, Object> userProfile = userDao.getUserProfile(userId);
        if (userProfile == null || userProfile.isEmpty()) {
            throw new BusinessException("用户信用信息不存在");
        }

        UserCreditInfoDTO creditInfoDTO = new UserCreditInfoDTO();
        creditInfoDTO.setUserId(userId);
        
        // 从用户详情中提取信用相关信息
        if (userProfile.containsKey("credit_score")) {
            creditInfoDTO.setCreditScore((Integer) userProfile.get("credit_score"));
        }
        
        if (userProfile.containsKey("credit_limit")) {
            creditInfoDTO.setCreditLimit((java.math.BigDecimal) userProfile.get("credit_limit"));
        }
        
        if (userProfile.containsKey("used_credit")) {
            creditInfoDTO.setUsedCredit((java.math.BigDecimal) userProfile.get("used_credit"));
        }
        
        if (userProfile.containsKey("available_credit")) {
            creditInfoDTO.setAvailableCredit((java.math.BigDecimal) userProfile.get("available_credit"));
        }

        return creditInfoDTO;
    }

    @Override
    public boolean updateUserCredit(UserCredit userCredit) {
        if (userCredit == null || userCredit.getUserId() == null) {
            throw new BusinessException("用户信用信息不能为空");
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userCredit.getUserId());
        if (userInfo == null) {
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
} 