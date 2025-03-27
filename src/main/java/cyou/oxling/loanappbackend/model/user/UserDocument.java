package cyou.oxling.loanappbackend.model.user;

import lombok.Data;

import java.util.Date;

/**
 * 用户文档实体类
 */
@Data
public class UserDocument {
    
    /**
     * 文档状态：待审核
     */
    public static final int STATUS_PENDING = 0;
    
    /**
     * 文档状态：通过
     */
    public static final int STATUS_APPROVED = 1;
    
    /**
     * 文档状态：拒绝
     */
    public static final int STATUS_REJECTED = 2;
    
    /**
     * 文档类型：手持身份证
     */
    public static final int DOC_TYPE_ID_CARD = 1;
    
    /**
     * 文档类型：收入证明
     */
    public static final int DOC_TYPE_INCOME = 2;
    
    /**
     * 文档类型：地址证明
     */
    public static final int DOC_TYPE_ADDRESS = 3;
    
    /**
     * 文档类型：征信报告
     */
    public static final int DOC_TYPE_CREDIT_REPORT = 4;
    
    /**
     * 文档类型：资产证明
     */
    public static final int DOC_TYPE_ASSET = 5;
    
    /**
     * 文档类型：工作证明
     */
    public static final int DOC_TYPE_EMPLOYMENT = 6;
    
    private Long id;
    private Long userId;
    private Integer docType;
    private String docUrl;
    private String docDescription;
    private Integer status;
    private Integer score;
    private Date createTime;
    private Date updateTime;
} 