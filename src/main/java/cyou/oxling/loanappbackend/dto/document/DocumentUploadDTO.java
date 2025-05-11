package cyou.oxling.loanappbackend.dto.document;

import lombok.Data;

/**
 * 文档上传DTO
 */
@Data
public class DocumentUploadDTO {
    /**
     * 文件ID
     */
    private Long id;

    /**
     * 文档类型
     * 0 = 用户头像
     * 1 = 手持身份证
     * 2 = 收入证明
     * 3 = 地址证明
     * 4 = 征信报告
     * 5 = 资产证明
     * 6 = 工作证明
     */
    private int docType;

    /**
     * 文档URL
     */
    private String docUrl;

}
