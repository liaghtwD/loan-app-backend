package cyou.oxling.loanappbackend.service.impl;

import cyou.oxling.loanappbackend.crawler.ShixinCrawlerClient;
import cyou.oxling.loanappbackend.dao.ShixinRecordDao;
import cyou.oxling.loanappbackend.model.spider.shixin.ResponseData;
import cyou.oxling.loanappbackend.model.spider.shixin.DispDataItem;
import cyou.oxling.loanappbackend.model.spider.shixin.DispDataDetail;
import cyou.oxling.loanappbackend.model.spider.shixin.ShixinRecord;
import cyou.oxling.loanappbackend.service.ShixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("isredis")
public class ShixinServiceImpl implements ShixinService {

        @Autowired
        private ShixinCrawlerClient crawlerClient;

        @Autowired
        private ShixinRecordDao shixinRecordDao;

        @Autowired
        private RedisTemplate<String, Map<String, Object>> mapRedisTemplate;

        private static final String REDIS_KEY_PREFIX = "shixin:";

        @Override
    public Map<String, Object> queryShixinInfo(String name, String idCard) {
        Map<String, Object> result = new HashMap<>();
        String redisKey = REDIS_KEY_PREFIX + idCard;

        // Step 1: 查询 Redis 缓存
        Map<String, Object> redisData = mapRedisTemplate.opsForValue().get(redisKey);
        if (redisData != null) {
            Boolean isShixin = (Boolean) redisData.get("isShixin");

            // 获取 lastUpdated 并统一转换为字符串格式（推荐）
            Object lastUpdatedObj = redisData.get("lastUpdated");
            String lastUpdatedStr = null;

            if (lastUpdatedObj instanceof LocalDateTime) {
                lastUpdatedStr = ((LocalDateTime) lastUpdatedObj).toString();
            } else if (lastUpdatedObj instanceof String) {
                lastUpdatedStr = (String) lastUpdatedObj;
            }

            if (Boolean.FALSE.equals(isShixin)) {
                result.put("status", "not_found");
                result.put("message", "缓存命中：此身份证号不是失信人");
                result.put("lastUpdated", lastUpdatedStr); // 返回 lastUpdated
                return result;
            } else {
                result.put("status", "found");
                result.put("message", "缓存命中：此人为失信人");
                result.put("lastUpdated", lastUpdatedStr); // 返回 lastUpdated
                return result;
            }
        }

        // Step 2: Redis 未命中，查询数据库
        ShixinRecord dbRecord = shixinRecordDao.findByIdCard(idCard);

        if (dbRecord != null) {
            boolean isShixin = dbRecord.getIsShixin() == ShixinRecord.IS_SHIXIN;

            // 构建 Redis 缓存数据
            Map<String, Object> cacheData = new HashMap<>();
            cacheData.put("isShixin", isShixin);
            cacheData.put("lastUpdated", dbRecord.getLastUpdated().toString()); // 存入 Redis 的时候转成字符串

            mapRedisTemplate.opsForValue().set(redisKey, cacheData, 7, TimeUnit.DAYS);

            // 返回结果
            if (isShixin) {
                result.put("status", "found");
                result.put("message", "数据库命中：此人为失信人");
            } else {
                result.put("status", "not_found");
                result.put("message", "数据库命中：此身份证号不是失信人");
            }

            result.put("lastUpdated", dbRecord.getLastUpdated().toString());
            return result;
        }

        // Step 3: Redis & DB 都未命中，调用爬虫接口
        try {
            Map<String, Object> crawlerResult = crawlerClient.queryFromCrawler(name, idCard);
            boolean found = "found".equals(crawlerResult.get("status"));

            LocalDateTime now = LocalDateTime.now();

            // 构建 Redis 缓存数据
            Map<String, Object> cacheData = new HashMap<>();
            cacheData.put("isShixin", found);
            cacheData.put("lastUpdated", now.toString());

            // 构建数据库记录
            ShixinRecord record = new ShixinRecord();
            record.setIdCard(idCard);
            record.setIsShixin(found ? ShixinRecord.IS_SHIXIN : ShixinRecord.NOT_SHIXIN);
            record.setLastUpdated(now);

            if (found) {
                ResponseData responseData = (ResponseData) crawlerResult.get("data");
                DispDataItem item = responseData.getData().get(0); // 取结果
                DispDataDetail detail = item.getDisp_data().get(0); // 获取 disp_data 第一个对象

                // 存入所有字段
                record.setName(detail.getIname());
                record.setCaseCode(detail.getCaseCode());
                record.setCourtName(detail.getCourtName());
                record.setDuty(detail.getDuty());
                record.setPublishDate(detail.getPublishDate());
                record.setPerformance(detail.getPerformance());
            }

            // 插入或更新数据库
            ShixinRecord existingRecord = shixinRecordDao.findByIdCard(idCard);
            if (existingRecord == null) {
                shixinRecordDao.saveShixinRecord(record);
            } else {
                shixinRecordDao.updateShixinRecord(record);
            }

            // 更新 Redis 缓存
            mapRedisTemplate.opsForValue().set(redisKey, cacheData, 7, TimeUnit.DAYS);

            // 返回结果
            if (found) {
                result.put("status", "found");
                result.put("message", "爬虫接口命中：此人为失信人");
            } else {
                result.put("status", "not_found");
                result.put("message", "爬虫未命中：此人不是失信人");
            }

            result.put("lastUpdated", now.toString());
            return result;

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "查询失败：" + e.getMessage());
            return result;
        }
    }
}