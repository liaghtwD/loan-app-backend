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
     * 文档URL
     */
    private String docUrl;
}
