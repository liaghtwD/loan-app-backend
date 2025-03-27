package cyou.oxling.loanappbackend.dto.document;

import lombok.Data;

/**
 * 文档上传DTO
 */
@Data
public class DocumentUploadDTO {
    /**
     * 文档类型：1=手持身份证，2=收入证明等
     */
    private Integer docType;
    
    /**
     * 文档URL
     */
    private String docUrl;
} 