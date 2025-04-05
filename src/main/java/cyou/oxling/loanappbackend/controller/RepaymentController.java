package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.dto.loan.RepaymentRequest;
import cyou.oxling.loanappbackend.dto.loan.PrepaymentRequest;
import cyou.oxling.loanappbackend.model.loan.ActualRepayment;
import cyou.oxling.loanappbackend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 还款控制器
 */
@RestController
@RequestMapping("/api/repayment")
public class RepaymentController {
    
    @Autowired
    private LoanService loanService;
    
    /**
     * 获取当期需要还款的金额
     */
    @GetMapping("/pay/{loanId}")
    public Result<?> getCurrentRepaymentAmount(@RequestAttribute("userId") Long userId, @PathVariable Long loanId) {
        Map<String, Object> paymentInfo = loanService.getCurrentRepaymentAmount(userId, loanId);
        return Result.success(paymentInfo);
    }
    
    /**
     * 获取提前还款需要的金额
     */
    @GetMapping("/prepay/{loanId}")
    public Result<?> getPrepaymentAmount(@RequestAttribute("userId") Long userId, @PathVariable Long loanId) {
        Map<String, Object> prepaymentInfo = loanService.getPrepaymentAmount(userId, loanId);
        return Result.success(prepaymentInfo);
    }
    
    /**
     * 正常还款
     */
    @PostMapping("/pay")
    public Result<?> repayment(@RequestAttribute("userId") Long userId, @RequestBody RepaymentRequest request) {
        boolean success = loanService.repayment(userId, request);
        return Result.success("还款成功", success);
    }
    
    /**
     * 提前还款
     */
    @PostMapping("/prepay")
    public Result<?> prepayment(@RequestAttribute("userId") Long userId, @RequestBody PrepaymentRequest request) {
        boolean success = loanService.prepayment(userId, request);
        return Result.success("提前还款成功", success);
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