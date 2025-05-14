package cyou.oxling.loanappbackend.service.impl;

import cyou.oxling.loanappbackend.crawler.ShixinCrawlerClient;
import cyou.oxling.loanappbackend.service.ShixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 只调用爬虫接口的实现类，不使用 Redis 和数据库
 */
@Service("noredis")
public class ShixintoServiceImpl implements ShixinService {

    @Autowired
    private ShixinCrawlerClient crawlerClient;

    @Override
    public Map<String, Object> queryShixinInfo(String name, String idCard) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 直接调用爬虫接口
            Map<String, Object> crawlerResult = crawlerClient.queryFromCrawler(name, idCard);
            boolean found = "found".equals(crawlerResult.get("status"));

            if (found) {
                result.put("status", "found");
                result.put("message", "爬虫接口命中：此人为失信人");
            } else {
                result.put("status", "not_found");
                result.put("message", "此人不是失信人");
            }

            return result;

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "查询失败：" + e.getMessage());
            return result;
        }
    }
}
