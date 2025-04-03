package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.loan.ActualRepayment;
import cyou.oxling.loanappbackend.model.loan.LoanApplication;
import cyou.oxling.loanappbackend.model.loan.RepaymentSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 贷款数据访问接口
 */
@Mapper
public interface LoanDao {
    
    /**
     * 创建贷款申请
     * 
     * @param loanApplication 贷款申请信息
     * @return 影响行数
     */
    int createLoanApplication(LoanApplication loanApplication);
    
    /**
     * 根据ID查询贷款申请
     * 
     * @param id 贷款ID
     * @return 贷款申请信息
     */
    LoanApplication findLoanById(Long id);
    
    /**
     * 更新贷款申请
     * 
     * @param loanApplication 贷款申请信息
     * @return 影响行数
     */
    int updateLoanApplication(LoanApplication loanApplication);
    
    /**
     * 获取用户当前进行中的贷款
     * 
     * @param userId 用户ID
     * @return 贷款申请信息
     */
    LoanApplication getCurrentLoan(Long userId);
    
    /**
     * 获取用户贷款历史记录
     * 
     * @param userId 用户ID
     * @return 贷款历史记录
     */
    List<LoanApplication> getLoanHistory(Long userId);
    
    /**
     * 创建还款计划
     * 
     * @param repaymentSchedule 还款计划
     * @return 影响行数
     */
    int createRepaymentSchedule(RepaymentSchedule repaymentSchedule);
    
    /**
     * 批量创建还款计划
     * 
     * @param repaymentSchedules 还款计划列表
     * @return 影响行数
     */
    int batchCreateRepaymentSchedule(List<RepaymentSchedule> repaymentSchedules);
    
    /**
     * 获取贷款的还款计划
     * 
     * @param loanId 贷款ID
     * @return 还款计划列表
     */
    List<RepaymentSchedule> getRepaymentScheduleByLoanId(Long loanId);
    
    /**
     * 获取当前期待还款计划
     * 
     * @param loanId 贷款ID
     * @return 当前期待还款计划
     */
    RepaymentSchedule getCurrentRepaymentSchedule(Long loanId);
    
    /**
     * 删除贷款的所有还款计划
     * 
     * @param loanId 贷款ID
     * @return 影响行数
     */
    int deleteRepaymentScheduleByLoanId(Long loanId);
    
    /**
     * 删除指定贷款ID和期数的还款计划
     * 
     * @param loanId 贷款ID
     * @param installmentNo 期数
     * @return 影响行数
     */
    int deleteRepaymentSchedule(@Param("loanId") Long loanId, @Param("installmentNo") Integer installmentNo);
    
    /**
     * 记录实际还款
     * 
     * @param actualRepayment 实际还款记录
     * @return 影响行数
     */
    int createActualRepayment(ActualRepayment actualRepayment);
    
    /**
     * 获取用户还款历史
     * 
     * @param userId 用户ID
     * @return 还款历史记录
     */
    List<ActualRepayment> getRepaymentHistory(Long userId);
    
    /**
     * 获取贷款详情
     * 
     * @param loanId 贷款ID
     * @return 贷款详情
     */
    Map<String, Object> getLoanDetail(Long loanId);
    
    /**
     * 获取贷款的所有还款计划
     * 
     * @param loanId 贷款ID
     * @return 还款计划列表
     */
    List<Map<String, Object>> getRepaymentSchedulesByLoanId(Long loanId);
    
    /**
     * 获取贷款的所有实际还款记录
     * 
     * @param loanId 贷款ID
     * @return 实际还款记录列表
     */
    List<Map<String, Object>> getActualRepaymentsByLoanId(Long loanId);
    
    /**
     * 获取贷款详情，包括基本信息、还款计划和实际还款记录
     * 
     * @param loanId 贷款ID
     * @return 贷款详情
     */
    Map<String, Object> getLoanDetailWithPlans(Long loanId);
} 