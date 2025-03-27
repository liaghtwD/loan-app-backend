package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.loan.LoanApplication;
import cyou.oxling.loanappbackend.model.loan.RepaymentSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 贷款数据访问接口
 */
@Mapper
public interface LoanDao {
    
    /**
     * 保存贷款申请
     * @param loanApplication 贷款申请信息
     * @return 影响行数
     */
    int saveLoanApplication(LoanApplication loanApplication);
    
    /**
     * 保存还款计划
     * @param repaymentSchedule 还款计划
     * @return 影响行数
     */
    int saveRepaymentSchedule(RepaymentSchedule repaymentSchedule);
    
    /**
     * 批量保存还款计划
     * @param repaymentSchedules 还款计划列表
     * @return 影响行数
     */
    int batchSaveRepaymentSchedule(@Param("list") List<RepaymentSchedule> repaymentSchedules);
    
    /**
     * 根据贷款ID查询贷款信息
     * @param loanId 贷款ID
     * @return 贷款信息
     */
    LoanApplication findById(@Param("loanId") Long loanId);
    
    /**
     * 根据用户ID查询进行中的贷款
     * @param userId 用户ID
     * @return 贷款列表
     */
    List<LoanApplication> findActiveByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询已完成的贷款
     * @param userId 用户ID
     * @return 贷款列表
     */
    List<LoanApplication> findCompletedByUserId(@Param("userId") Long userId);
    
    /**
     * 根据贷款ID查询还款计划
     * @param loanId 贷款ID
     * @return 还款计划列表
     */
    List<RepaymentSchedule> findRepaymentScheduleByLoanId(@Param("loanId") Long loanId);
    
    /**
     * 更新贷款状态
     * @param loanId 贷款ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("loanId") Long loanId, @Param("status") Integer status);
    
    /**
     * 更新还款计划状态
     * @param loanId 贷款ID
     * @param installmentNo 期数
     * @param status 状态
     * @return 影响行数
     */
    int updateRepaymentStatus(@Param("loanId") Long loanId, @Param("installmentNo") Integer installmentNo, @Param("status") Integer status);
} 