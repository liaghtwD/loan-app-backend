package cyou.oxling.loanappbackend.service.impl;

import cyou.oxling.loanappbackend.dao.LoanDao;
import cyou.oxling.loanappbackend.dao.UserDao;
import cyou.oxling.loanappbackend.dto.loan.*;
import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.model.loan.ActualRepayment;
import cyou.oxling.loanappbackend.model.loan.LoanApplication;
import cyou.oxling.loanappbackend.model.loan.RepaymentSchedule;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 贷款服务实现类
 */
@Service
public class LoanServiceImpl implements LoanService {
    
    @Autowired
    private LoanDao loanDao;
    
    @Autowired
    private UserDao userDao;
    
    @Value("${loan.interest-rate}")
    private BigDecimal defaultInterestRate;
    
    /**
     * 申请贷款
     *
     * @param userId  用户ID
     * @param request 贷款申请请求
     * @return 贷款申请ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyLoan(Long userId, LoanApplyRequest request) {
        // 1. 检查用户是否有资格申请贷款
        UserCredit userCredit = userDao.getUserCredit(userId);
        if (userCredit == null) {
            throw new BusinessException(4001, "用户信用信息不存在");
        }
        
        // 2. 检查用户是否有未完成的贷款
        LoanApplication currentLoan = loanDao.getCurrentLoan(userId);
        if (currentLoan != null) {
            throw new BusinessException(4002, "您有一笔正在进行中的贷款，不能重复申请");
        }
        
        // 3. 检查贷款金额是否超出用户可用额度
        BigDecimal availableCredit = userCredit.getCreditLimit().subtract(userCredit.getUsedCredit());
        if (request.getLoanAmount().compareTo(availableCredit) > 0) {
            throw new BusinessException(4003, "贷款金额超出可用额度");
        }
        
        // 4. 创建贷款申请
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setUserId(userId);
        loanApplication.setLoanAmount(request.getLoanAmount());
        loanApplication.setLoanPeriod(request.getLoanPeriod());
        loanApplication.setRepaymentMethod(request.getRepaymentMethod());
        loanApplication.setLoanPurpose(request.getLoanPurpose());
        loanApplication.setInterestRate(defaultInterestRate);
        loanApplication.setStatus(0); // 审核中
        loanApplication.setApplyTime(new Date());
        loanApplication.setUpdateTime(new Date());
        loanApplication.setActualRepaymentAmount(BigDecimal.ZERO); // 初始化实际已还款金额为0
        
        // 5. 判断是否需要人工审核
        boolean needManualReview = request.getLoanAmount().compareTo(new BigDecimal("200000")) > 0 || 
                                  userCredit.getCreditScore() < 50;
        
        // 6. 如果不需要人工审核，直接放款
        if (!needManualReview) {
            loanApplication.setStatus(1); // 已放款
            loanApplication.setApproveTime(new Date());
            loanApplication.setActualLoanAmount(request.getLoanAmount());
            
            // 更新用户已用额度
            userCredit.setUsedCredit(userCredit.getUsedCredit().add(request.getLoanAmount()));
            userCredit.setUpdateTime(new Date());
            userDao.updateUserCredit(userCredit);
        }
        
        // 7. 保存贷款申请
        loanDao.createLoanApplication(loanApplication);
        
        // 8. 如果不需要人工审核，生成还款计划
        if (!needManualReview) {
            generateRepaymentSchedule(loanApplication);
            // 更新用户当前贷款ID
            userDao.updateUserNowLoan(userId, loanApplication.getId());
        }
        
        return loanApplication.getId();
    }
    
    /**
     * 获取贷款详情
     *
     * @param loanId 贷款ID
     * @return 贷款详情
     */
    @Override
    public Map<String, Object> getLoanDetail(Long loanId) {
        return loanDao.getLoanDetail(loanId);
    }
    
    /**
     * 获取贷款详情，包括还款计划和实际还款记录
     *
     * @param loanId 贷款ID
     * @return 贷款详情
     */
    @Override
    public Map<String, Object> getLoanDetailFull(Long loanId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取贷款基本信息
        Map<String, Object> loanDetail = loanDao.getLoanDetailWithPlans(loanId);
        if (loanDetail == null) {
            return null;
        }
        
        // 获取还款计划
        List<Map<String, Object>> repaymentSchedules = loanDao.getRepaymentSchedulesByLoanId(loanId);
        
        // 获取实际还款记录
        List<Map<String, Object>> actualRepayments = loanDao.getActualRepaymentsByLoanId(loanId);
        
        // 合并数据
        result.put("loanInfo", loanDetail);
        result.put("repaymentSchedules", repaymentSchedules);
        result.put("actualRepayments", actualRepayments);
        
        return result;
    }
    
    /**
     * 更新贷款申请
     *
     * @param userId  用户ID
     * @param loanId  贷款ID
     * @param request 更新请求
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLoan(Long userId, Long loanId, LoanUpdateRequest request) {
        // 1. 查询贷款信息
        LoanApplication loanApplication = loanDao.findLoanById(loanId);
        if (loanApplication == null) {
            throw new BusinessException(4004, "贷款不存在");
        }
        
        // 2. 检查是否是当前用户的贷款
        if (!loanApplication.getUserId().equals(userId)) {
            throw new BusinessException(4005, "无权操作此贷款");
        }
        
        // 3. 检查贷款状态是否为审核中
        if (loanApplication.getStatus() != 0) {
            throw new BusinessException(4006, "只有审核中的贷款才能更新");
        }
        
        // 4. 更新贷款信息
        if (request.getLoanAmount() != null) {
            loanApplication.setLoanAmount(request.getLoanAmount());
        }
        if (request.getLoanPeriod() != null) {
            loanApplication.setLoanPeriod(request.getLoanPeriod());
        }
        if (request.getRepaymentMethod() != null) {
            loanApplication.setRepaymentMethod(request.getRepaymentMethod());
        }
        if (request.getLoanPurpose() != null) {
            loanApplication.setLoanPurpose(request.getLoanPurpose());
        }
        if (request.getStatus() != null) {
            loanApplication.setStatus(request.getStatus());
            
            // 如果状态更新为已放款，需要更新用户额度并生成还款计划
            if (request.getStatus() == 1) {
                UserCredit userCredit = userDao.getUserCredit(userId);
                if (userCredit != null) {
                    userCredit.setUsedCredit(userCredit.getUsedCredit().add(loanApplication.getLoanAmount()));
                    userCredit.setUpdateTime(new Date());
                    userDao.updateUserCredit(userCredit);
                }
                
                loanApplication.setApproveTime(new Date());
                loanApplication.setActualLoanAmount(loanApplication.getLoanAmount());
                loanApplication.setActualRepaymentAmount(BigDecimal.ZERO); // 初始化实际已还款金额为0
                
                // 更新用户当前贷款ID
                userDao.updateUserNowLoan(userId, loanApplication.getId());
                
                // 先更新贷款信息
                loanApplication.setUpdateTime(new Date());
                loanDao.updateLoanApplication(loanApplication);
                
                // 再生成还款计划
                generateRepaymentSchedule(loanApplication);
                
                // 已经更新过了，不需要再更新
                return true;
            }
        }
        
        loanApplication.setUpdateTime(new Date());
        loanDao.updateLoanApplication(loanApplication);
        
        return true;
    }
    
    /**
     * 贷款模拟计算
     *
     * @param request 模拟请求
     * @return 模拟结果
     */
    @Override
    public LoanSimulationResponse simulateLoan(LoanSimulationRequest request) {
        LoanSimulationResponse response = new LoanSimulationResponse();
        
        // 设置默认值
        Integer loanPeriod = request.getLoanPeriod() != null ? request.getLoanPeriod() : 6;
        Integer repaymentMethod = request.getRepaymentMethod() != null ? request.getRepaymentMethod() : 0;
        
        response.setLoanAmount(request.getLoanAmount());
        response.setLoanPeriod(loanPeriod);
        response.setRepaymentMethod(repaymentMethod);
        response.setInterestRate(defaultInterestRate);
        
        BigDecimal loanAmount = request.getLoanAmount();
        
        // 计算总还款金额
        BigDecimal totalInterest = loanAmount.multiply(defaultInterestRate).multiply(new BigDecimal(loanPeriod)).divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
        BigDecimal totalRepayment = loanAmount.add(totalInterest);
        response.setTotalRepayment(totalRepayment);
        
        // 根据还款方式生成还款计划
        if (repaymentMethod == 0) {
            // 一次性还款
            List<LoanSimulationResponse.RepaymentPlan> plans = new ArrayList<>();
            LoanSimulationResponse.RepaymentPlan plan = new LoanSimulationResponse.RepaymentPlan();
            plan.setInstallmentNo(1);
            plan.setPrincipal(loanAmount);
            plan.setInterest(totalInterest);
            plan.setAmountDue(totalRepayment);
            plans.add(plan);
            
            response.setPrincipalRepaymentPlans(plans);
            response.setAnnuityRepaymentPlans(plans);
        } else if (repaymentMethod == 1) {
            // 等额本金
            response.setPrincipalRepaymentPlans(calculatePrincipalRepayment(loanAmount, loanPeriod, defaultInterestRate));
            response.setAnnuityRepaymentPlans(null);
        } else if (repaymentMethod == 2) {
            // 等额本息
            response.setPrincipalRepaymentPlans(null);
            response.setAnnuityRepaymentPlans(calculateAnnuityRepayment(loanAmount, loanPeriod, defaultInterestRate));
        }
        
        return response;
    }
    
    /**
     * 获取用户当前进行中的贷款
     *
     * @param userId 用户ID
     * @return 贷款详情
     */
    @Override
    public Map<String, Object> getCurrentLoan(Long userId) {
        LoanApplication currentLoan = loanDao.getCurrentLoan(userId);
        if (currentLoan == null) {
            return null;
        }
        
        // 获取详细信息
        return getLoanDetailFull(currentLoan.getId());
    }
    
    /**
     * 获取用户贷款历史记录
     *
     * @param userId 用户ID
     * @return 贷款历史记录
     */
    @Override
    public List<LoanApplication> getLoanHistory(Long userId) {
        return loanDao.getLoanHistory(userId);
    }
    
    /**
     * 还款
     *
     * @param userId  用户ID
     * @param request 还款请求
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean repayment(Long userId, RepaymentRequest request) {
        // 1. 查询贷款信息
        LoanApplication loanApplication = loanDao.findLoanById(request.getLoanId());
        if (loanApplication == null) {
            throw new BusinessException(4007, "贷款不存在");
        }
        
        // 2. 检查是否是当前用户的贷款
        if (!loanApplication.getUserId().equals(userId)) {
            throw new BusinessException(4008, "无权操作此贷款");
        }
        
        // 3. 检查贷款状态是否为已放款
        if (loanApplication.getStatus() != 1 && loanApplication.getStatus() != 3) {
            throw new BusinessException(4009, "只有已放款或逾期的贷款才能还款");
        }
        
        // 4. 获取当前还款计划
        RepaymentSchedule currentSchedule;
        if (request.getInstallmentNo() != null) {
            // 如果指定了期数，查询指定期数的还款计划
            List<RepaymentSchedule> schedules = loanDao.getRepaymentScheduleByLoanId(request.getLoanId());
            currentSchedule = schedules.stream()
                    .filter(s -> s.getInstallmentNo().equals(request.getInstallmentNo()))
                    .findFirst()
                    .orElse(null);
            
            if (currentSchedule == null) {
                throw new BusinessException(4010, "指定期数的还款计划不存在");
            }
        } else {
            // 否则查询当前期数的还款计划
            currentSchedule = loanDao.getCurrentRepaymentSchedule(request.getLoanId());
            if (currentSchedule == null) {
                throw new BusinessException(4011, "当前没有待还款的计划");
            }
        }
        
        // 5. 检查还款金额
        if (!request.getAmount().equals(currentSchedule.getAmountDue())) {
            throw new BusinessException(4012, "还款金额必须与应还金额完全相等，应还金额为：" + currentSchedule.getAmountDue());
        }
        
        // 6. 创建实际还款记录
        ActualRepayment actualRepayment = new ActualRepayment();
        actualRepayment.setLoanId(request.getLoanId());
        actualRepayment.setUserId(userId);
        actualRepayment.setInstallmentNo(currentSchedule.getInstallmentNo());
        actualRepayment.setPrincipal(currentSchedule.getPrincipal());
        actualRepayment.setInterest(currentSchedule.getInterest());
        actualRepayment.setRepaymentAmount(request.getAmount());
        actualRepayment.setRepaymentTime(currentSchedule.getDueDate());
        actualRepayment.setStatus(0); // 正常还款
        actualRepayment.setActualRepaymentTime(new Date());
        actualRepayment.setUpdateTime(new Date());
        loanDao.createActualRepayment(actualRepayment);
        
        // 7. 删除当前还款计划
        loanDao.deleteRepaymentSchedule(request.getLoanId(), currentSchedule.getInstallmentNo());
        
        // 8. 更新贷款已还款金额
        loanApplication.setActualRepaymentAmount(
            loanApplication.getActualRepaymentAmount() != null ? 
            loanApplication.getActualRepaymentAmount().add(request.getAmount()) : 
            request.getAmount()
        );
        loanApplication.setUpdateTime(new Date());
        loanDao.updateLoanApplication(loanApplication);
        
        // 9. 检查是否是最后一期，如果是则更新贷款状态为已还清
        List<RepaymentSchedule> remainingSchedules = loanDao.getRepaymentScheduleByLoanId(request.getLoanId());
        if (remainingSchedules.isEmpty()) {
            loanApplication.setStatus(2); // 已还清
            loanApplication.setUpdateTime(new Date());
            loanDao.updateLoanApplication(loanApplication);
            
            // 更新用户已用额度
            UserCredit userCredit = userDao.getUserCredit(userId);
            if (userCredit != null) {
                userCredit.setUsedCredit(userCredit.getUsedCredit().subtract(loanApplication.getActualLoanAmount()));
                userCredit.setUpdateTime(new Date());
                userDao.updateUserCredit(userCredit);
            }
            
            // 清除用户当前贷款ID
            userDao.clearUserNowLoan(userId);
        }
        
        return true;
    }
    
    /**
     * 获取用户还款历史
     *
     * @param userId 用户ID
     * @return 还款历史记录
     */
    @Override
    public List<ActualRepayment> getRepaymentHistory(Long userId) {
        return loanDao.getRepaymentHistory(userId);
    }
    
    /**
     * 生成还款计划
     *
     * @param loanApplication 贷款申请信息
     */
    private void generateRepaymentSchedule(LoanApplication loanApplication) {
        // 先删除旧的还款计划
        loanDao.deleteRepaymentScheduleByLoanId(loanApplication.getId());
        
        List<RepaymentSchedule> schedules = new ArrayList<>();
        
        // 一次性全款
        if (loanApplication.getRepaymentMethod() == 0) {
            RepaymentSchedule schedule = new RepaymentSchedule();
            schedule.setLoanId(loanApplication.getId());
            schedule.setUserId(loanApplication.getUserId());
            schedule.setInstallmentNo(1);
            
            // 计算利息
            BigDecimal interest = loanApplication.getLoanAmount()
                    .multiply(loanApplication.getInterestRate())
                    .multiply(new BigDecimal(loanApplication.getLoanPeriod()))
                    .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
            
            schedule.setPrincipal(loanApplication.getLoanAmount());
            schedule.setInterest(interest);
            schedule.setAmountDue(loanApplication.getLoanAmount().add(interest));
            
            // 设置到期日
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(loanApplication.getApproveTime());
            calendar.add(Calendar.MONTH, loanApplication.getLoanPeriod());
            schedule.setDueDate(calendar.getTime());
            
            schedule.setStatus(0); // 未还
            schedule.setUpdateTime(new Date());
            
            schedules.add(schedule);
        } else if (loanApplication.getRepaymentMethod() == 1) {
            // 等额本金
            List<LoanSimulationResponse.RepaymentPlan> plans = calculatePrincipalRepayment(
                    loanApplication.getLoanAmount(),
                    loanApplication.getLoanPeriod(),
                    loanApplication.getInterestRate()
            );
            
            for (int i = 0; i < plans.size(); i++) {
                LoanSimulationResponse.RepaymentPlan plan = plans.get(i);
                
                RepaymentSchedule schedule = new RepaymentSchedule();
                schedule.setLoanId(loanApplication.getId());
                schedule.setUserId(loanApplication.getUserId());
                schedule.setInstallmentNo(plan.getInstallmentNo());
                schedule.setPrincipal(plan.getPrincipal());
                schedule.setInterest(plan.getInterest());
                schedule.setAmountDue(plan.getAmountDue());
                
                // 设置到期日
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(loanApplication.getApproveTime());
                calendar.add(Calendar.MONTH, i + 1);
                schedule.setDueDate(calendar.getTime());
                
                schedule.setStatus(0); // 未还
                schedule.setUpdateTime(new Date());
                
                schedules.add(schedule);
            }
        } else if (loanApplication.getRepaymentMethod() == 2) {
            // 等额本息
            List<LoanSimulationResponse.RepaymentPlan> plans = calculateAnnuityRepayment(
                    loanApplication.getLoanAmount(),
                    loanApplication.getLoanPeriod(),
                    loanApplication.getInterestRate()
            );
            
            for (int i = 0; i < plans.size(); i++) {
                LoanSimulationResponse.RepaymentPlan plan = plans.get(i);
                
                RepaymentSchedule schedule = new RepaymentSchedule();
                schedule.setLoanId(loanApplication.getId());
                schedule.setUserId(loanApplication.getUserId());
                schedule.setInstallmentNo(plan.getInstallmentNo());
                schedule.setPrincipal(plan.getPrincipal());
                schedule.setInterest(plan.getInterest());
                schedule.setAmountDue(plan.getAmountDue());
                
                // 设置到期日
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(loanApplication.getApproveTime());
                calendar.add(Calendar.MONTH, i + 1);
                schedule.setDueDate(calendar.getTime());
                
                schedule.setStatus(0); // 未还
                schedule.setUpdateTime(new Date());
                
                schedules.add(schedule);
            }
        }
        
        // 批量保存还款计划
        loanDao.batchCreateRepaymentSchedule(schedules);
    }
    
    /**
     * 计算等额本金还款计划
     *
     * @param loanAmount   贷款金额
     * @param loanPeriod   贷款期限
     * @param interestRate 利率
     * @return 还款计划
     */
    private List<LoanSimulationResponse.RepaymentPlan> calculatePrincipalRepayment(BigDecimal loanAmount, Integer loanPeriod, BigDecimal interestRate) {
        List<LoanSimulationResponse.RepaymentPlan> plans = new ArrayList<>();
        
        // 每月本金（先不四舍五入，保留精度）
        BigDecimal exactMonthlyPrincipal = loanAmount.divide(new BigDecimal(loanPeriod), 10, RoundingMode.HALF_UP);
        // 四舍五入到分的每月本金
        BigDecimal monthlyPrincipal = exactMonthlyPrincipal.setScale(2, RoundingMode.HALF_UP);
        
        // 每月利率
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        
        // 计算前n-1期四舍五入后的本金总和
        BigDecimal totalPrincipalBeforeLast = monthlyPrincipal.multiply(new BigDecimal(loanPeriod - 1));
        
        // 最后一期本金 = 贷款总额 - 前n-1期本金之和
        BigDecimal lastPrincipal = loanAmount.subtract(totalPrincipalBeforeLast).setScale(2, RoundingMode.HALF_UP);
        
        // 计算每期还款
        BigDecimal remainingPrincipal = loanAmount;
        
        for (int i = 1; i <= loanPeriod; i++) {
            // 确定本期还款本金
            BigDecimal principal;
            if (i == loanPeriod) {
                principal = lastPrincipal; // 最后一期使用校正后的本金
            } else {
                principal = monthlyPrincipal;
            }
            
            // 计算本期利息：剩余本金 × 月利率
            BigDecimal interest = remainingPrincipal.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            
            // 构建还款计划
            LoanSimulationResponse.RepaymentPlan plan = new LoanSimulationResponse.RepaymentPlan();
            plan.setInstallmentNo(i);
            plan.setPrincipal(principal);
            plan.setInterest(interest);
            plan.setAmountDue(principal.add(interest));
            
            plans.add(plan);
            
            // 更新剩余本金
            remainingPrincipal = remainingPrincipal.subtract(principal);
        }
        
        return plans;
    }
    
    /**
     * 计算等额本息还款计划
     *
     * @param loanAmount   贷款金额
     * @param loanPeriod   贷款期限
     * @param interestRate 利率
     * @return 还款计划
     */
    private List<LoanSimulationResponse.RepaymentPlan> calculateAnnuityRepayment(BigDecimal loanAmount, Integer loanPeriod, BigDecimal interestRate) {
        List<LoanSimulationResponse.RepaymentPlan> plans = new ArrayList<>();
        
        // 每月利率
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        
        // 计算每月等额本息还款金额
        // 每月还款额 = 贷款本金 × 月利率 × (1 + 月利率)^贷款期数 / [(1 + 月利率)^贷款期数 - 1]
        BigDecimal monthlyPayment = loanAmount.multiply(monthlyRate)
                .multiply(BigDecimal.ONE.add(monthlyRate).pow(loanPeriod))
                .divide(BigDecimal.ONE.add(monthlyRate).pow(loanPeriod).subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        
        // 累计本金和利息，用于校正最后一期
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal remainingPrincipal = loanAmount;
        
        // 创建前n-1期还款计划
        for (int i = 1; i < loanPeriod; i++) {
            // 利息 = 剩余本金 × 月利率
            BigDecimal interest = remainingPrincipal.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            totalInterest = totalInterest.add(interest);
            
            // 本金 = 每月还款额 - 利息
            BigDecimal principal = monthlyPayment.subtract(interest);
            totalPrincipal = totalPrincipal.add(principal);
            
            LoanSimulationResponse.RepaymentPlan plan = new LoanSimulationResponse.RepaymentPlan();
            plan.setInstallmentNo(i);
            plan.setPrincipal(principal);
            plan.setInterest(interest);
            plan.setAmountDue(monthlyPayment);
            
            plans.add(plan);
            
            remainingPrincipal = remainingPrincipal.subtract(principal);
        }
        
        // 处理最后一期，校正尾差
        if (loanPeriod > 0) {
            // 最后一期本金就是剩余的本金
            BigDecimal lastPrincipal = remainingPrincipal.setScale(2, RoundingMode.HALF_UP);
            
            // 确保最后一期利息不为负
            BigDecimal lastInterest = monthlyPayment.subtract(lastPrincipal);
            if (lastInterest.compareTo(BigDecimal.ZERO) < 0) {
                lastInterest = BigDecimal.ZERO;
            }
            
            // 最后一期总金额可能会与月供不同
            BigDecimal lastAmountDue = lastPrincipal.add(lastInterest);
            
            LoanSimulationResponse.RepaymentPlan lastPlan = new LoanSimulationResponse.RepaymentPlan();
            lastPlan.setInstallmentNo(loanPeriod);
            lastPlan.setPrincipal(lastPrincipal);
            lastPlan.setInterest(lastInterest);
            lastPlan.setAmountDue(lastAmountDue);
            
            plans.add(lastPlan);
        }
        
        return plans;
    }
    
    /**
     * 提前还款
     *
     * @param userId  用户ID
     * @param request 提前还款请求
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean prepayment(Long userId, PrepaymentRequest request) {
        // 1. 查询贷款信息
        LoanApplication loanApplication = loanDao.findLoanById(request.getLoanId());
        if (loanApplication == null) {
            throw new BusinessException(4007, "贷款不存在");
        }
        
        // 2. 检查是否是当前用户的贷款
        if (!loanApplication.getUserId().equals(userId)) {
            throw new BusinessException(4008, "无权操作此贷款");
        }
        
        // 3. 检查贷款状态是否为已放款
        if (loanApplication.getStatus() != 1 && loanApplication.getStatus() != 3) {
            throw new BusinessException(4009, "只有已放款或逾期的贷款才能还款");
        }
        
        // 4. 获取所有的还款计划以计算剩余本金
        List<RepaymentSchedule> allSchedules = loanDao.getRepaymentScheduleByLoanId(request.getLoanId());
        if (allSchedules.isEmpty()) {
            throw new BusinessException(4011, "当前没有待还款的计划");
        }
        
        // 5. 计算剩余本金
        BigDecimal remainingPrincipal = allSchedules.stream()
                .map(RepaymentSchedule::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 6. 计算已支付的利息总额
        List<ActualRepayment> paidRepayments = loanDao.getRepaymentHistory(userId);
        // 筛选出当前贷款的还款记录
        paidRepayments = paidRepayments.stream()
                .filter(r -> r.getLoanId().equals(request.getLoanId()))
                .collect(Collectors.toList());
        
        BigDecimal paidInterest = paidRepayments.stream()
                .map(ActualRepayment::getInterest)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 7. 计算提前还款利息（从放款日到还款日的实际天数）
        Date approveDate = loanApplication.getApproveTime();
        Date currentDate = new Date();
        long daysElapsed = (currentDate.getTime() - approveDate.getTime()) / (1000 * 60 * 60 * 24);
        
        // 计算实际天数的日利率
        BigDecimal dailyRate = loanApplication.getInterestRate().divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
        
        // 按照实际借贷总金额计算从借贷日到提前还款日的总利息
        BigDecimal totalInterestForLoanPeriod = loanApplication.getActualLoanAmount()
                .multiply(dailyRate)
                .multiply(new BigDecimal(daysElapsed))
                .setScale(2, RoundingMode.HALF_UP);
        
        // 计算应付利息 = 总期间利息 - 已还利息
        BigDecimal interestDue = totalInterestForLoanPeriod.subtract(paidInterest);
        // 确保应付利息不为负
        if (interestDue.compareTo(BigDecimal.ZERO) < 0) {
            interestDue = BigDecimal.ZERO;
        }
        
        // 8. 计算总应还金额 = 剩余本金 + 应付利息
        BigDecimal totalDue = remainingPrincipal.add(interestDue);
        
        // 9. 检查提前还款金额是否与应还金额相等
        if (!request.getAmount().equals(totalDue)) {
            throw new BusinessException(4012, "提前还款金额必须与应还金额完全相等，应还金额为：" + totalDue);
        }
        
        // 10. 创建实际还款记录
        ActualRepayment actualRepayment = new ActualRepayment();
        actualRepayment.setLoanId(request.getLoanId());
        actualRepayment.setUserId(userId);
        actualRepayment.setInstallmentNo(0); // 提前还款使用0表示所有期数
        actualRepayment.setPrincipal(remainingPrincipal);
        actualRepayment.setInterest(interestDue);
        actualRepayment.setRepaymentAmount(request.getAmount());
        actualRepayment.setRepaymentTime(new Date());
        actualRepayment.setStatus(1); // 提前还款
        actualRepayment.setActualRepaymentTime(new Date());
        actualRepayment.setUpdateTime(new Date());
        loanDao.createActualRepayment(actualRepayment);
        
        // 11. 删除所有的还款计划
        loanDao.deleteRepaymentScheduleByLoanId(request.getLoanId());
        
        // 12. 更新贷款已还款金额和状态
        loanApplication.setActualRepaymentAmount(
            loanApplication.getActualRepaymentAmount() != null ? 
            loanApplication.getActualRepaymentAmount().add(request.getAmount()) : 
            request.getAmount()
        );
        loanApplication.setStatus(2); // 已还清
        loanApplication.setUpdateTime(new Date());
        loanDao.updateLoanApplication(loanApplication);
        
        // 13. 更新用户已用额度
        UserCredit userCredit = userDao.getUserCredit(userId);
        if (userCredit != null) {
            userCredit.setUsedCredit(userCredit.getUsedCredit().subtract(loanApplication.getActualLoanAmount()));
            userCredit.setUpdateTime(new Date());
            userDao.updateUserCredit(userCredit);
        }
        
        // 14. 清除用户当前贷款ID
        userDao.clearUserNowLoan(userId);
        
        return true;
    }
    
    /**
     * 获取当期需要还款的金额信息
     *
     * @param userId 用户ID
     * @param loanId 贷款ID
     * @return 当期还款信息，包含应还金额、期数等
     */
    @Override
    public Map<String, Object> getCurrentRepaymentAmount(Long userId, Long loanId) {
        // 1. 查询贷款信息
        LoanApplication loanApplication = loanDao.findLoanById(loanId);
        if (loanApplication == null) {
            throw new BusinessException(4007, "贷款不存在");
        }
        
        // 2. 检查是否是当前用户的贷款
        if (!loanApplication.getUserId().equals(userId)) {
            throw new BusinessException(4008, "无权操作此贷款");
        }
        
        // 3. 检查贷款状态是否为已放款
        if (loanApplication.getStatus() != 1 && loanApplication.getStatus() != 3) {
            throw new BusinessException(4009, "只有已放款或逾期的贷款才能还款");
        }
        
        // 4. 获取当前还款计划
        RepaymentSchedule currentSchedule = loanDao.getCurrentRepaymentSchedule(loanId);
        if (currentSchedule == null) {
            throw new BusinessException(4011, "当前没有待还款的计划");
        }
        
        // 5. 返回还款信息
        Map<String, Object> result = new HashMap<>();
        result.put("loanId", loanId);
        result.put("installmentNo", currentSchedule.getInstallmentNo());
        result.put("amountDue", currentSchedule.getAmountDue());
        result.put("dueDate", currentSchedule.getDueDate());
        
        return result;
    }
    
    /**
     * 获取提前还款需要的金额信息
     *
     * @param userId 用户ID
     * @param loanId 贷款ID
     * @return 提前还款信息，包含总应还金额等
     */
    @Override
    public Map<String, Object> getPrepaymentAmount(Long userId, Long loanId) {
        // 1. 查询贷款信息
        LoanApplication loanApplication = loanDao.findLoanById(loanId);
        if (loanApplication == null) {
            throw new BusinessException(4007, "贷款不存在");
        }
        
        // 2. 检查是否是当前用户的贷款
        if (!loanApplication.getUserId().equals(userId)) {
            throw new BusinessException(4008, "无权操作此贷款");
        }
        
        // 3. 检查贷款状态是否为已放款
        if (loanApplication.getStatus() != 1 && loanApplication.getStatus() != 3) {
            throw new BusinessException(4009, "只有已放款或逾期的贷款才能还款");
        }
        
        // 4. 获取所有的还款计划以计算剩余本金
        List<RepaymentSchedule> allSchedules = loanDao.getRepaymentScheduleByLoanId(loanId);
        if (allSchedules.isEmpty()) {
            throw new BusinessException(4011, "当前没有待还款的计划");
        }
        
        // 5. 计算剩余本金
        BigDecimal remainingPrincipal = allSchedules.stream()
                .map(RepaymentSchedule::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 6. 计算已支付的利息总额
        List<ActualRepayment> paidRepayments = loanDao.getRepaymentHistory(userId);
        // 筛选出当前贷款的还款记录
        paidRepayments = paidRepayments.stream()
                .filter(r -> r.getLoanId().equals(loanId))
                .collect(Collectors.toList());
        
        BigDecimal paidInterest = paidRepayments.stream()
                .map(ActualRepayment::getInterest)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 7. 计算提前还款利息（从放款日到还款日的实际天数）
        Date approveDate = loanApplication.getApproveTime();
        Date currentDate = new Date();
        long daysElapsed = (currentDate.getTime() - approveDate.getTime()) / (1000 * 60 * 60 * 24);
        
        // 计算实际天数的日利率
        BigDecimal dailyRate = loanApplication.getInterestRate().divide(new BigDecimal("365"), 10, RoundingMode.HALF_UP);
        
        // 按照实际借贷总金额计算从借贷日到提前还款日的总利息
        BigDecimal totalInterestForLoanPeriod = loanApplication.getActualLoanAmount()
                .multiply(dailyRate)
                .multiply(new BigDecimal(daysElapsed))
                .setScale(2, RoundingMode.HALF_UP);
        
        // 计算应付利息 = 总期间利息 - 已还利息
        BigDecimal interestDue = totalInterestForLoanPeriod.subtract(paidInterest);
        // 确保应付利息不为负
        if (interestDue.compareTo(BigDecimal.ZERO) < 0) {
            interestDue = BigDecimal.ZERO;
        }
        
        // 8. 计算总应还金额 = 剩余本金 + 应付利息
        BigDecimal totalDue = remainingPrincipal.add(interestDue);
        
        // 9. 统计剩余期数
        int remainingInstallments = allSchedules.size();
        
        // 10. 返回提前还款信息
        Map<String, Object> result = new HashMap<>();
        result.put("loanId", loanId);
        result.put("remainingPrincipal", remainingPrincipal);
        result.put("interestDue", interestDue);
        result.put("totalInterestForPeriod", totalInterestForLoanPeriod);
        result.put("paidInterest", paidInterest);
        result.put("totalAmount", totalDue);
        result.put("remainingInstallments", remainingInstallments);
        result.put("loanStatus", loanApplication.getStatus());
        result.put("daysElapsed", daysElapsed);
        
        return result;
    }
} 