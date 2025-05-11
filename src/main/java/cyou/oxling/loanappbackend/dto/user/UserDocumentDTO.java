package cyou.oxling.loanappbackend.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户文档DTO
 */
@Data
public class UserDocumentDTO {

    /**
     * 文档ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

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
private Integer docType;

    
    /**
     * 文档描述
     */
    private String docDescription;
    
    /**
     * 文档文件
     */
    private MultipartFile file;

    /**
     * 文档下载地址
     */
    private String downloadUrl;
} 