package cyou.oxling.loanappbackend.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户文档DTO
 */
@Data
public class UserDocumentDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 文档类型
     */
    private Integer docType;
    
    /**
     * 文档描述
     */
    private String docDescription;
    
    /**
     * 文档文件
     */
    private MultipartFile file;
} 