package cyou.oxling.loanappbackend.scheduler;

import cyou.oxling.loanappbackend.crawler.LprCrawlerClient;
import lombok.extern.slf4j.Slf4j; // 引入 Lombok 日志注解
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j  // 自动生成 log 变量
public class LprTaskScheduler {

    @Autowired
    private LprCrawlerClient crawlerClient;

    /**
     * 每月 21 日凌晨 0:00 执行一次
     */
    @Scheduled(cron = "0 0 0 21 * ?")
    public void fetchMonthlyLpr() {
        log.info("开始抓取最新 LPR 利率数据...");
        crawlerClient.crawlAndSave();
    }
}
