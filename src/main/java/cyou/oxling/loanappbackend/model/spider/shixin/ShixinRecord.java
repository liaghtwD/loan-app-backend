package cyou.oxling.loanappbackend.model.spider.shixin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 失信记录实体类
 * 字段说明：
 * idCard - 身份证号（主键）
 * isShixin - 是否为失信人（0=否，1=是）
 * name - 失信人姓名
 * caseCode - 案件编号
 * courtName - 执行法院
 * duty - 义务内容
 * publishDate - 公示日期
 * performance - 履行情况
 * lastUpdated - 最后更新时间
 */
@Data
public class ShixinRecord {

    /**
     * 是否为失信人：否
     */
    public static final int NOT_SHIXIN = 0;

    /**
     * 是否为失信人：是
     */
    public static final int IS_SHIXIN = 1;

    private String idCard;
    private Integer isShixin;
    private String name;
    private String caseCode;
    private String courtName;
    private String duty;
    private String publishDate;
    private String performance;
    private LocalDateTime lastUpdated;
}
