package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.dto.loan.LoanApplyRequest;
import cyou.oxling.loanappbackend.dto.loan.LoanSimulationRequest;
import cyou.oxling.loanappbackend.dto.loan.LoanUpdateRequest;
import cyou.oxling.loanappbackend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 贷款控制器
 */
@RestController
@RequestMapping("/api/loan")
public class LoanController {
    
    @Autowired
    private LoanService loanService;
    
    /**
     * 申请贷款
     */
    @PostMapping("/apply")
    public Result<?> applyLoan(@RequestAttribute("userId") Long userId, @RequestBody LoanApplyRequest request) {
        Long loanId = loanService.applyLoan(userId, request);
        return Result.success("贷款申请成功", loanId);
    }
    
    /**
     * 贷款模拟计算
     */
    @PostMapping("/simulation")
    public Result<?> simulateLoan(@RequestBody LoanSimulationRequest request) {
        return Result.success(loanService.simulateLoan(request));
    }
    
    /**
     * 获取用户当前进行中的贷款
     */
    @GetMapping("/now")
    public Result<?> getCurrentLoan(@RequestAttribute("userId") Long userId) {
        Map<String, Object> loan = loanService.getCurrentLoan(userId);
        return Result.success(loan);
    }
    
    /**
     * 更新贷款申请
     */
    @PutMapping("/update/{loanId}")
    public Result<?> updateLoan(@RequestAttribute("userId") Long userId, 
                              @PathVariable Long loanId, 
                              @RequestBody LoanUpdateRequest request) {
        boolean success = loanService.updateLoan(userId, loanId, request);
        return Result.success("更新成功", success);
    }
    
    /**
     * 获取贷款详情
     */
    @GetMapping("/{loanId}")
    public Result<?> getLoanDetail(@PathVariable Long loanId) {
        Map<String, Object> detail = loanService.getLoanDetailFull(loanId);
        return Result.success(detail);
    }
} 