package cyou.oxling.loanappbackend.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 用户提交的资料表
 * 
 * 字段说明：
 * id - 主键ID
 * userId - 用户ID，关联user_info表
 * docType - 资料类型：1=手持身份证；2=收入证明；3=地址证明；4=征信报告；5=资产证明；6=工作证明等
 * docUrl - 资料存储路径或文件名
 * docDescription - 资料描述
 * status - 审核状态：0=待审核；1=通过；2=拒绝
 * score - 资料评分，用于综合评估
 * createTime - 创建时间
 * updateTime - 更新时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDocuments {
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