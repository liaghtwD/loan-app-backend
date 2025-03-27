package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.dto.credit.UserCreditInfoDTO;
import cyou.oxling.loanappbackend.service.CreditService;
import cyou.oxling.loanappbackend.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 信用控制器
 */
@RestController
@RequestMapping("/api/credit")
public class CreditController {

    @Autowired
    private CreditService creditService;

    /**
     * 获取用户信用信息
     * @return 信用信息
     */
    @GetMapping("/info")
    public Result<UserCreditInfoDTO> getCreditInfo() {
        // 从JWT中获取用户ID
        Long userId = RequestUtil.getCurrentUserId();
        return Result.success(creditService.getUserCreditInfo(userId));
    }
} 