package cyou.oxling.loanappbackend.model.user;

import lombok.Data;

import java.util.Date;

/**
 * 用户文档实体类
 * 字段说明：
 * id - 主键ID
 * userId - 用户ID，关联user_info表
 * fileName - 原始文件名，用户查看文件列表和下载时应该返回的名字，同时便于审计追踪
 * docType - 资料类型：1=手持身份证；2=收入证明；3=地址证明；4=征信报告；5=资产证明；6=工作证明等
 * docUrl - 资料存储路径或文件名
 * docDescription - 资料描述
 * status - 审核状态：0=待审核；1=通过；2=拒绝
 * score - 资料评分，用于综合评估
 * createTime - 创建时间
 * updateTime - 更新时间
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
    private String fileName;
    private String docUrl;
    private String docDescription;
    private Integer status;
    private Integer score;
    private Date createTime;
    private Date updateTime;
} 