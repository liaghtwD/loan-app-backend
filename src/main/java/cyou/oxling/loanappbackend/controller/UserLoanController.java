package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.model.loan.LoanApplication;
import cyou.oxling.loanappbackend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户贷款历史控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserLoanController {
    
    @Autowired
    private LoanService loanService;
    
    /**
     * 获取用户贷款历史记录
     */
    @GetMapping("/loan-history")
    public Result<?> getLoanHistory(@RequestAttribute("userId") Long userId) {
        List<LoanApplication> history = loanService.getLoanHistory(userId);
        return Result.success(history);
    }
} 