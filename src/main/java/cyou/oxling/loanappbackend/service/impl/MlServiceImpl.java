package cyou.oxling.loanappbackend.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import cyou.oxling.loanappbackend.dao.MlEvalResultDao;
import cyou.oxling.loanappbackend.dao.MlFeatureSnapshotDao;
import cyou.oxling.loanappbackend.dao.MlTaskQueueDao;
import cyou.oxling.loanappbackend.dao.UserDao;
import cyou.oxling.loanappbackend.dao.UserReportDao;
import cyou.oxling.loanappbackend.dto.ml.MlEvalResultDTO;
import cyou.oxling.loanappbackend.dto.ml.UserReportDTO;
import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.model.ml.MlEvalResult;
import cyou.oxling.loanappbackend.model.ml.MlFeatureSnapshot;
import cyou.oxling.loanappbackend.model.ml.MlTaskQueue;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import cyou.oxling.loanappbackend.model.user.UserReport;
import cyou.oxling.loanappbackend.service.MlService;
import cyou.oxling.loanappbackend.service.MlTaskProcessor;

/**
 * ML服务实现类
 */
@Service
public class MlServiceImpl implements MlService {

    private static final Logger logger = LoggerFactory.getLogger(MlServiceImpl.class);

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private UserReportDao userReportDao;
    
    @Autowired
    private MlFeatureSnapshotDao mlFeatureSnapshotDao;
    
    @Autowired
    private MlEvalResultDao mlEvalResultDao;
    
    @Autowired
    private MlTaskQueueDao mlTaskQueueDao;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MlTaskProcessor mlTaskProcessor;
    
    @Value("${ml.task.retry.interval:300000}") // 默认5分钟
    private long retryInterval;
    
    @Value("${ml.task.max.retries:3}")
    private int maxRetries;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitUserReport(UserReportDTO userReportDTO) {
        if (userReportDTO == null || userReportDTO.getUserId() == null) {
            throw new BusinessException("用户自报信息不能为空");
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userReportDTO.getUserId());
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }

        // 创建自报信息记录
        UserReport userReport = new UserReport();
        userReport.setUserId(userReportDTO.getUserId());
        userReport.setStatus(UserReport.STATUS_PENDING);
        userReport.setOverdue30pCnt2y(userReportDTO.getOverdue30pCnt2y());
        userReport.setOpenCreditLinesCnt(userReportDTO.getOpenCreditLinesCnt());
        userReport.setEarliestCreditOpenDate(userReportDTO.getEarliestCreditOpenDate());
        userReport.setDerogCnt(userReportDTO.getDerogCnt());
        userReport.setPublicRecordCleanCnt(userReportDTO.getPublicRecordCleanCnt());
        userReport.setHousingStatus(userReportDTO.getHousingStatus());
        userReport.setPotentialLoanPurpose(userReportDTO.getPotentialLoanPurpose());
        userReport.setExtEarlyAmtTotal(userReportDTO.getExtEarlyAmtTotal());
        userReport.setExtEarlyCntTotal(userReportDTO.getExtEarlyCntTotal());
        userReport.setExtEarlyAmt3m(userReportDTO.getExtEarlyAmt3m());
        
        // 设置过期时间（90天）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 90);
        userReport.setExpireTime(calendar.getTime());
        
        userReport.setCreateTime(new Date());
        
        // 保存自报信息
        userReportDao.saveUserReport(userReport);
        
        // 更新用户信用状态为正在评估
        UserCredit userCredit = userDao.getUserCredit(userReportDTO.getUserId());
        if (userCredit != null) {
            userCredit.setEvaluating(UserCredit.EVAL_STATUS_EVALUATING);
            userCredit.setUpdateTime(new Date());
            userDao.updateUserCredit(userCredit);
        }
        
        // 创建特征快照
        MlFeatureSnapshot snapshot = createFeatureSnapshot(userReportDTO.getUserId(), MlFeatureSnapshot.SOURCE_SELF_REFRESH);
        
        // 创建评估任务
        createEvalTask(userReportDTO.getUserId(), snapshot.getId());
        
        return snapshot.getId();
    }

    @Override
    public MlEvalResultDTO getUserCredit(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 获取用户信用信息
        UserCredit userCredit = userDao.getUserCredit(userId);
        if (userCredit == null) {
            // 如果没有信用信息，则返回空对象
            return new MlEvalResultDTO();
        }
        
        // 构建返回DTO
        MlEvalResultDTO resultDTO = new MlEvalResultDTO();
        resultDTO.setUserId(userId);
        resultDTO.setCreditScore(userCredit.getCreditScore());
        resultDTO.setCreditLimit(userCredit.getCreditLimit());
        resultDTO.setModelVer(userCredit.getModelVer());
        
        // 获取最新的评估结果以获取过期时间
        MlEvalResult latestEvalResult = mlEvalResultDao.findLatestByUserId(userId);
        if (latestEvalResult != null) {
            resultDTO.setSnapshotId(latestEvalResult.getSnapshotId());
            resultDTO.setCreateTime(latestEvalResult.getCreateTime());
            resultDTO.setExpireTime(latestEvalResult.getExpireTime());
            
            // 检查是否过期
            boolean expired = latestEvalResult.getExpireTime() != null && 
                             latestEvalResult.getExpireTime().before(new Date());
            resultDTO.setExpired(expired);
            
            // 如果过期且状态不是正在评估，则更新用户信用状态为等待评估
            if (expired && userCredit.getEvaluating() != UserCredit.EVAL_STATUS_EVALUATING) {
                userCredit.setEvaluating(UserCredit.EVAL_STATUS_WAITING);
                userCredit.setUpdateTime(new Date());
                userDao.updateUserCredit(userCredit);
            }
        } else {
            // 没有评估结果，设置为过期
            resultDTO.setExpired(true);
        }
        
        return resultDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MlFeatureSnapshot createFeatureSnapshot(Long userId, String source) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 获取最新的自报信息
        UserReport userReport = userReportDao.findLatestByUserId(userId);
        if (userReport == null) {
            throw new BusinessException("用户自报信息不存在");
        }
        
        // 构建特征JSON
        Map<String, Object> featureMap = new HashMap<>();
        featureMap.put("userId", userId);
        featureMap.put("overdue30pCnt2y", userReport.getOverdue30pCnt2y());
        featureMap.put("openCreditLinesCnt", userReport.getOpenCreditLinesCnt());
        featureMap.put("earliestCreditOpenDate", userReport.getEarliestCreditOpenDate());
        featureMap.put("derogCnt", userReport.getDerogCnt());
        featureMap.put("publicRecordCleanCnt", userReport.getPublicRecordCleanCnt());
        featureMap.put("housingStatus", userReport.getHousingStatus());
        featureMap.put("potentialLoanPurpose", userReport.getPotentialLoanPurpose());
        featureMap.put("extEarlyAmtTotal", userReport.getExtEarlyAmtTotal());
        featureMap.put("extEarlyCntTotal", userReport.getExtEarlyCntTotal());
        featureMap.put("extEarlyAmt3m", userReport.getExtEarlyAmt3m());
        
        // 添加其他可能的特征...
        
        String featureJson;
        try {
            featureJson = objectMapper.writeValueAsString(featureMap);
        } catch (Exception e) {
            throw new BusinessException("特征序列化失败: " + e.getMessage());
        }
        
        // 创建特征快照
        MlFeatureSnapshot snapshot = new MlFeatureSnapshot();
        snapshot.setUserId(userId);
        snapshot.setFeatureJson(featureJson);
        snapshot.setSource(source);
        snapshot.setCreateTime(new Date());
        
        // 保存特征快照
        mlFeatureSnapshotDao.save(snapshot);
        
        return snapshot;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEvalTask(Long userId, Long snapshotId) {
        if (userId == null || snapshotId == null) {
            throw new BusinessException("用户ID和特征快照ID不能为空");
        }
        
        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证特征快照是否存在
        MlFeatureSnapshot snapshot = mlFeatureSnapshotDao.findById(snapshotId);
        if (snapshot == null) {
            throw new BusinessException("特征快照不存在");
        }
        
        // 创建评估任务
        MlTaskQueue taskQueue = new MlTaskQueue();
        taskQueue.setUserId(userId);
        taskQueue.setSnapshotId(snapshotId);
        taskQueue.setRetries(0);
        taskQueue.setStatus(MlTaskQueue.STATUS_PENDING);
        taskQueue.setNextRunTime(new Date());  // 立即执行
        taskQueue.setCreateTime(new Date());
        
        // 保存评估任务
        mlTaskQueueDao.save(taskQueue);
        
        // 增加Redis中的待处理任务计数，并在需要时激活处理器
        try {
            mlTaskProcessor.incrementPendingTaskCount();
        } catch (Exception e) {
            logger.error("Failed to increment pending task count in Redis", e);
            // 继续执行，不影响主流程
        }
        
        return taskQueue.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processEvalTask(Long taskId) {
        if (taskId == null) {
            throw new BusinessException("任务ID不能为空");
        }
        
        // 获取任务信息
        MlTaskQueue taskQueue = mlTaskQueueDao.findById(taskId);
        if (taskQueue == null) {
            throw new BusinessException("任务不存在");
        }
        
        // 检查任务状态
        if (taskQueue.getStatus() != MlTaskQueue.STATUS_PENDING) {
            return false;  // 任务已经处理过
        }
        
        try {
            // 获取特征快照
            MlFeatureSnapshot snapshot = mlFeatureSnapshotDao.findById(taskQueue.getSnapshotId());
            if (snapshot == null) {
                throw new BusinessException("特征快照不存在");
            }
            
            // TODO: 调用ML模型进行评估
            // 这里应该是调用外部ML服务或本地ML模型进行评估
            // 返回信用分和信用额度
            
            // 模拟评估结果
            Integer creditScore = 700;  // 模拟信用分
            java.math.BigDecimal creditLimit = new java.math.BigDecimal("10000");  // 模拟信用额度
            
            // 创建评估结果
            MlEvalResult evalResult = new MlEvalResult();
            evalResult.setUserId(taskQueue.getUserId());
            evalResult.setSnapshotId(taskQueue.getSnapshotId());
            evalResult.setCreditScore(creditScore);
            evalResult.setCreditLimit(creditLimit);
            evalResult.setModelVer("v1.0");  // 模型版本
            evalResult.setCreateTime(new Date());
            
            // 设置过期时间（90天）
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 90);
            evalResult.setExpireTime(calendar.getTime());
            
            // 保存评估结果
            mlEvalResultDao.save(evalResult);
            
            // 更新用户信用信息
            updateUserCredit(evalResult);
            
            // 更新任务状态为成功
            mlTaskQueueDao.updateStatus(taskId, MlTaskQueue.STATUS_SUCCESS, taskQueue.getRetries(), null);
            
            return true;
        } catch (Exception e) {
            // 处理失败，增加重试次数
            int retries = taskQueue.getRetries() + 1;
            Integer status = retries >= maxRetries ? MlTaskQueue.STATUS_FAILED : MlTaskQueue.STATUS_PENDING;
            
            // 计算下次运行时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, (int) retryInterval);
            Date nextRunTime = retries >= maxRetries ? null : calendar.getTime();
            
            // 更新任务状态
            mlTaskQueueDao.updateStatus(taskId, status, retries, nextRunTime);
            
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserCredit(MlEvalResult evalResult) {
        if (evalResult == null || evalResult.getUserId() == null) {
            throw new BusinessException("评估结果不能为空");
        }
        
        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(evalResult.getUserId());
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 获取当前用户信用信息
        UserCredit userCredit = userDao.getUserCredit(evalResult.getUserId());
        
        if (userCredit == null) {
            // 如果不存在，则创建新的信用信息
            userCredit = new UserCredit();
            userCredit.setUserId(evalResult.getUserId());
            userCredit.setCreditScore(evalResult.getCreditScore());
            userCredit.setCreditLimit(evalResult.getCreditLimit());
            userCredit.setUsedCredit(java.math.BigDecimal.ZERO);  // 初始已用额度为0
            userCredit.setEvaluating(UserCredit.EVAL_STATUS_STABLE);  // 设置为稳定状态
            userCredit.setModelVer(evalResult.getModelVer());  // 设置模型版本
            userCredit.setCreateTime(new Date());
            userCredit.setUpdateTime(new Date());
            
            return userDao.createUserCredit(userCredit) > 0;
        } else {
            // 如果存在，则更新信用信息
            userCredit.setCreditScore(evalResult.getCreditScore());
            userCredit.setCreditLimit(evalResult.getCreditLimit());
            userCredit.setEvaluating(UserCredit.EVAL_STATUS_STABLE);  // 设置为稳定状态
            userCredit.setModelVer(evalResult.getModelVer());  // 设置模型版本
            userCredit.setUpdateTime(new Date());
            
            return userDao.updateUserCredit(userCredit) > 0;
        }
    }

    @Override
    public MlEvalResult getLatestEvalResult(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return mlEvalResultDao.findLatestByUserId(userId);
    }
} 