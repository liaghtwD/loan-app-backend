package cyou.oxling.loanappbackend.model.user;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 用户信用实体类
 */
@Data
public class UserCredit {
    
    private Long id;
    private Long userId;
    private Integer creditScore;
    private BigDecimal creditLimit;
    private BigDecimal usedCredit;
    private Integer evaluating; // 0表示稳定状态，1表示正在评估，2表示等待评估（资料过期但是没上传）
    private String modelVer; // 模型版本
    private String remark;
    private Date createTime;
    private Date updateTime;
    
    // 评估状态常量
    public static final int EVAL_STATUS_STABLE = 0; // 稳定状态
    public static final int EVAL_STATUS_EVALUATING = 1; // 正在评估
    public static final int EVAL_STATUS_WAITING = 2; // 等待评估
    
    /**
     * 获取可用额度
     * 这个计算不存储在数据库中，而是在查询时计算
     * @return 可用额度
     */
    public BigDecimal getAvailableCredit() {
        if (creditLimit != null && usedCredit != null) {
            return creditLimit.subtract(usedCredit);
        }
        return BigDecimal.ZERO;
    }
} 