package cyou.oxling.loanappbackend.service;

import cyou.oxling.loanappbackend.dto.credit.UserCreditInfoDTO;
import cyou.oxling.loanappbackend.model.user.UserCredit;

/**
 * 信用服务接口
 */
public interface CreditService {
    
    /**
     * 获取用户信用信息
     * @param userId 用户ID
     * @return 用户信用信息
     */
    UserCreditInfoDTO getUserCreditInfo(Long userId);
    
    /**
     * 更新用户信用额度
     * @param userCredit 用户信用信息
     * @return 是否更新成功
     */
    boolean updateUserCredit(UserCredit userCredit);
} 