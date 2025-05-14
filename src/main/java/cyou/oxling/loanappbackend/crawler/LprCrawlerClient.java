package cyou.oxling.loanappbackend.crawler;

import cyou.oxling.loanappbackend.dao.LprRecordDao;
import cyou.oxling.loanappbackend.model.spider.lpr.LprRecord;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Component
public class LprCrawlerClient {

    private static final String URL = "https://www.boc.cn/fimarkets/lilv/fd32/201310/t20131031_2591219.html";

    @Autowired
    private LprRecordDao lprRecordDao;

    public void crawlAndSave() {
        try {
            // 使用 Jsoup 下载网页
            Document doc = Jsoup.connect(URL).get();

            // 查找表格
            Element table = doc.selectFirst("table[align=center][border=1]");
            if (table == null) {
                log.warn("未找到 LPR 利率表格");
                return;
            }

            // 遍历每一行
            for (Element row : table.select("tr")) {
                Elements cols = row.select("td");

                if (cols.size() >= 3 && !row.hasClass("title")) { // 忽略标题行和无数据行
                    String dateStr = cols.get(0).text().trim();
                    String oneYear = cols.get(1).text().trim();
                    String fiveYear = cols.get(2).text().trim();

                    // 解析日期
                    LocalDate publishDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    // 转换为数值
                    Double oneYearRate = parseRate(oneYear);
                    Double fiveYearRate = parseRate(fiveYear);

                    // 构建记录对象
                    LprRecord record = new LprRecord();
                    record.setPublishDate(publishDate);
                    record.setOneYearRate(oneYearRate);
                    record.setFiveYearRate(fiveYearRate);

                    // 防止重复插入
                    if (lprRecordDao.findByDate(publishDate) == null) {
                        lprRecordDao.save(record);
                        log.info("已保存：{}", record);
                    } else {
                        log.info("记录已存在：{}", publishDate);
                    }
                }
            }

        } catch (IOException e) {
            log.error("抓取失败：{}", e.getMessage());
        } catch (Exception e) {
            log.error("解析失败：{}", e.getMessage());
        }
    }

    /**
     * 将百分比字符串转为 double
     */
    private Double parseRate(String rateStr) {
        return Double.parseDouble(rateStr.replace("%", ""));
    }
}
