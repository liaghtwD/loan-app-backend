package cyou.oxling.loanappbackend.service;

import cyou.oxling.loanappbackend.dto.loan.*;
import cyou.oxling.loanappbackend.model.loan.ActualRepayment;
import cyou.oxling.loanappbackend.model.loan.LoanApplication;

import java.util.List;
import java.util.Map;

/**
 * 贷款服务接口
 */
public interface LoanService {
    
    /**
     * 申请贷款
     * 
     * @param userId 用户ID
     * @param request 贷款申请请求
     * @return 贷款申请ID
     */
    Long applyLoan(Long userId, LoanApplyRequest request);
    
    /**
     * 获取贷款详情
     * 
     * @param loanId 贷款ID
     * @return 贷款详情
     */
    Map<String, Object> getLoanDetail(Long loanId);
    
    /**
     * 获取贷款详情，包括还款计划和实际还款记录
     * 
     * @param loanId 贷款ID
     * @return 贷款详情
     */
    Map<String, Object> getLoanDetailFull(Long loanId);
    
    /**
     * 更新贷款申请
     * 
     * @param userId 用户ID
     * @param loanId 贷款ID
     * @param request 更新请求
     * @return 是否成功
     */
    boolean updateLoan(Long userId, Long loanId, LoanUpdateRequest request);
    
    /**
     * 贷款模拟计算
     * 
     * @param request 模拟请求
     * @return 模拟结果
     */
    LoanSimulationResponse simulateLoan(LoanSimulationRequest request);
    
    /**
     * 获取用户当前进行中的贷款
     * 
     * @param userId 用户ID
     * @return 贷款详情
     */
    Map<String, Object> getCurrentLoan(Long userId);
    
    /**
     * 获取用户贷款历史记录
     * 
     * @param userId 用户ID
     * @return 贷款历史记录
     */
    List<LoanApplication> getLoanHistory(Long userId);
    
    /**
     * 还款
     * 
     * @param userId 用户ID
     * @param request 还款请求
     * @return 是否成功
     */
    boolean repayment(Long userId, RepaymentRequest request);
    
    /**
     * 获取用户还款历史
     * 
     * @param userId 用户ID
     * @return 还款历史记录
     */
    List<ActualRepayment> getRepaymentHistory(Long userId);
} 