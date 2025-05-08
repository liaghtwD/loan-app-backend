package cyou.oxling.loanappbackend.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cyou.oxling.loanappbackend.dao.MlEvalResultDao;
import cyou.oxling.loanappbackend.dao.MlTaskQueueDao;
import cyou.oxling.loanappbackend.dao.UserDao;
import cyou.oxling.loanappbackend.model.ml.MlEvalResult;
import cyou.oxling.loanappbackend.model.ml.MlTaskQueue;
import cyou.oxling.loanappbackend.model.user.UserCredit;
import cyou.oxling.loanappbackend.service.MlTaskProcessor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * ML任务处理器 - 使用Redis维护任务计数，优化数据库压力
 */
@Service
public class MlTaskProcessorImpl implements MlTaskProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MlTaskProcessorImpl.class);
    
    // Redis中保存的待处理任务数量的key
    private static final String PENDING_TASK_COUNT_KEY = "ml:task:pending:count";
    
    @Autowired
    private MlTaskQueueDao mlTaskQueueDao;
    
    @Autowired
    private MlEvalResultDao mlEvalResultDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private final Random random = new Random();
    private boolean running = true;
    private ExecutorService executorService;
    private AtomicBoolean processingActive = new AtomicBoolean(false);
    
    @Value("${ml.task.batch.size:10}")
    private int batchSize;
    
    @Value("${ml.task.processing.threads:3}")
    private int threadCount;
    
    @Value("${ml.task.sleep.interval:1000}")
    private long sleepInterval;
    
    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(this::processingLoop);
        }
        
        // 初始化Redis中的任务计数
        initPendingTaskCount();
        
        logger.info("Started ML task processor with {} threads", threadCount);
    }
    
    /**
     * 初始化Redis中的待处理任务计数
     */
    private void initPendingTaskCount() {
        try {
            // 如果Redis中不存在计数，则从数据库中查询并初始化
            if (Boolean.FALSE.equals(redisTemplate.hasKey(PENDING_TASK_COUNT_KEY))) {
                Date currentTime = new Date();
                Long count = mlTaskQueueDao.countPendingTasks(MlTaskQueue.STATUS_PENDING, currentTime);
                if (count != null && count > 0) {
                    redisTemplate.opsForValue().set(PENDING_TASK_COUNT_KEY, count.toString());
                    // 有待处理任务，激活处理器
                    activateProcessing();
                } else {
                    redisTemplate.opsForValue().set(PENDING_TASK_COUNT_KEY, "0");
                }
            }
        } catch (Exception e) {
            logger.error("Error initializing pending task count in Redis", e);
            // 出错时暂时激活处理器，确保不会错过任务
            activateProcessing();
        }
    }
    
    /**
     * 增加待处理任务计数
     */
    @Override
    public void incrementPendingTaskCount() {
        try {
            Long newCount = redisTemplate.opsForValue().increment(PENDING_TASK_COUNT_KEY);
            logger.debug("Pending task count incremented to: {}", newCount);
            
            // 如果从0变为>0，激活处理器
            if (newCount != null && newCount == 1) {
                activateProcessing();
            }
        } catch (Exception e) {
            logger.error("Error incrementing pending task count in Redis", e);
            // 出错时激活处理器，确保不会错过任务
            activateProcessing();
        }
    }
    
    /**
     * 减少待处理任务计数
     */
    private void decrementPendingTaskCount(int count) {
        if (count <= 0) return;
        
        try {
            Long newCount = redisTemplate.opsForValue().decrement(PENDING_TASK_COUNT_KEY, count);
            logger.debug("Pending task count decremented by {} to: {}", count, newCount);
            
            // 如果计数降为0，停止处理
            if (newCount != null && newCount <= 0) {
                // 为防止出现负数
                if (newCount < 0) {
                    redisTemplate.opsForValue().set(PENDING_TASK_COUNT_KEY, "0");
                }
                deactivateProcessing();
            }
        } catch (Exception e) {
            logger.error("Error decrementing pending task count in Redis", e);
        }
    }
    
    /**
     * 激活任务处理
     */
    private void activateProcessing() {
        if (processingActive.compareAndSet(false, true)) {
            logger.info("ML task processing activated");
        }
    }
    
    /**
     * 停止任务处理
     */
    private void deactivateProcessing() {
        if (processingActive.compareAndSet(true, false)) {
            logger.info("ML task processing deactivated - no pending tasks");
        }
    }
    
    @PreDestroy
    public void shutdown() {
        running = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("ML task processor shutdown completed");
    }
    
    private void processingLoop() {
        while (running) {
            try {
                // 只有在处理器激活状态下才处理任务
                if (processingActive.get()) {
                    int processedCount = processNextBatch();
                    
                    // 如果没有处理任何任务，短暂休眠避免CPU过度使用
                    if (processedCount == 0) {
                        Thread.sleep(sleepInterval);
                    }
                } else {
                    // 处理器未激活，休眠等待
                    Thread.sleep(sleepInterval * 2);
                }
            } catch (Exception e) {
                logger.error("Error in ML task processing loop", e);
                try {
                    Thread.sleep(5000); // 出错后等待一段时间再继续
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    @Transactional
    public int processNextBatch() {
        // 查询Redis中的剩余任务数
        String countStr = redisTemplate.opsForValue().get(PENDING_TASK_COUNT_KEY);
        int redisCount = 0;
        try {
            redisCount = countStr != null ? Integer.parseInt(countStr) : 0;
        } catch (NumberFormatException e) {
            redisCount = 0;
        }
        
        // 如果Redis中显示没有任务，则返回
        if (redisCount <= 0) {
            deactivateProcessing();
            return 0;
        }
        
        // 1. 查询待处理的任务
        Date currentTime = new Date();
        List<MlTaskQueue> pendingTasks = mlTaskQueueDao.findPendingTasks(
                MlTaskQueue.STATUS_PENDING, currentTime, batchSize);
        
        if (pendingTasks.isEmpty()) {
            // 数据库中没有待处理任务，但Redis中有计数，修正Redis计数
            if (redisCount > 0) {
                redisTemplate.opsForValue().set(PENDING_TASK_COUNT_KEY, "0");
                deactivateProcessing();
            }
            return 0;
        }
        
        int processedCount = 0;
        
        // 2. 处理每个任务
        for (MlTaskQueue task : pendingTasks) {
            // 先将任务标记为处理中，防止其他线程重复处理
            if (mlTaskQueueDao.updateStatusIfMatch(
                    task.getId(), 
                    MlTaskQueue.STATUS_PENDING, 
                    MlTaskQueue.STATUS_PROCESSING,
                    task.getRetries()) <= 0) {
                // 任务已被其他线程处理，跳过
                continue;
            }
            
            try {
                // 模拟处理延迟 (200-700ms)
                Thread.sleep(200 + random.nextInt(500));
                
                // 模拟评估结果
                int creditScore = 40 + random.nextInt(51); // 40-90
                BigDecimal creditLimit = new BigDecimal(5000 + random.nextInt(10001)); // 5000-15000
                
                // 创建评估结果
                MlEvalResult evalResult = new MlEvalResult();
                evalResult.setUserId(task.getUserId());
                evalResult.setSnapshotId(task.getSnapshotId());
                evalResult.setCreditScore(creditScore);
                evalResult.setCreditLimit(creditLimit);
                evalResult.setModelVer("mock-v1.0");
                evalResult.setCreateTime(new Date());
                
                // 设置过期时间（90天）
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 90);
                evalResult.setExpireTime(calendar.getTime());
                
                // 保存评估结果
                mlEvalResultDao.save(evalResult);
                
                // 更新用户信用信息
                UserCredit userCredit = userDao.getUserCredit(task.getUserId());
                if (userCredit != null) {
                    userCredit.setCreditScore(creditScore);
                    userCredit.setCreditLimit(creditLimit);
                    userCredit.setEvaluating(UserCredit.EVAL_STATUS_STABLE);
                    userCredit.setModelVer("mock-v1.0");
                    userCredit.setUpdateTime(new Date());
                    userDao.updateUserCredit(userCredit);
                }
                
                // 更新任务状态为成功
                mlTaskQueueDao.updateStatus(task.getId(), MlTaskQueue.STATUS_SUCCESS, 
                        task.getRetries(), null);
                
                processedCount++;
                logger.debug("Processed ML task: {}, user: {}, score: {}, limit: {}", 
                        task.getId(), task.getUserId(), creditScore, creditLimit);
                
            } catch (Exception e) {
                logger.error("Error processing ML task: " + task.getId(), e);
                
                // 处理失败，增加重试次数
                int retries = task.getRetries() + 1;
                Integer status = retries >= 3 ? MlTaskQueue.STATUS_FAILED : MlTaskQueue.STATUS_PENDING;
                
                // 计算下次运行时间（5分钟后）
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 5);
                Date nextRunTime = retries >= 3 ? null : calendar.getTime();
                
                // 更新任务状态
                mlTaskQueueDao.updateStatus(task.getId(), status, retries, nextRunTime);
                
                // 如果任务需要重试，不减少计数
                if (status.equals(MlTaskQueue.STATUS_PENDING)) {
                    processedCount--;
                }
            }
        }
        
        // 减少Redis中的待处理任务计数
        if (processedCount > 0) {
            decrementPendingTaskCount(processedCount);
        }
        
        return processedCount;
    }
} 