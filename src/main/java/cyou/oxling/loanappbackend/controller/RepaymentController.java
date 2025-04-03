package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.dto.loan.RepaymentRequest;
import cyou.oxling.loanappbackend.model.loan.ActualRepayment;
import cyou.oxling.loanappbackend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 还款控制器
 */
@RestController
@RequestMapping("/api/repayment")
public class RepaymentController {
    
    @Autowired
    private LoanService loanService;
    
    /**
     * 还款
     */
    @PostMapping("/pay")
    public Result<?> repayment(@RequestAttribute("userId") Long userId, @RequestBody RepaymentRequest request) {
        boolean success = loanService.repayment(userId, request);
        return Result.success("还款成功", success);
    }
    
    /**
     * 获取用户还款历史
     */
    @GetMapping("/history")
    public Result<?> getRepaymentHistory(@RequestAttribute("userId") Long userId) {
        List<ActualRepayment> history = loanService.getRepaymentHistory(userId);
        return Result.success(history);
    }
} 