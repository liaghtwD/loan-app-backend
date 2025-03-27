package cyou.oxling.loanappbackend.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 人工审核记录表
 * 
 * 字段说明：
 * id - 主键ID
 * entityId - 关联具体审核对象的ID，如user_documents.id或loan_application.id等
 * entityType - 表示审核对象类型：1=资料；2=贷款申请；…
 * reviewerId - 审核人ID，关联admin_user.id
 * reviewResult - 审核结果：1=通过；2=拒绝；3=退回修改等
 * reviewRemarks - 备注
 * reviewTime - 审核时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualReview {
    private Long id;
    private Long entityId;
    private Integer entityType;
    private Long reviewerId;
    private Integer reviewResult;
    private String reviewRemarks;
    private Date reviewTime;
} 