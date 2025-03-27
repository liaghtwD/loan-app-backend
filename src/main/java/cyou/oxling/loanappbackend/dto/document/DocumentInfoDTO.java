package cyou.oxling.loanappbackend.dto.document;

import lombok.Data;

/**
 * 文档信息DTO
 */
@Data
public class DocumentInfoDTO {
    /**
     * 文档ID
     */
    private Long documentId;
    
    /**
     * 文档类型：1=手持身份证，2=收入证明等
     */
    private Integer docType;
    
    /**
     * 文档状态：0=待审核，1=已通过，2=已拒绝
     */
    private Integer status;
    
    /**
     * 文档URL
     */
    private String docUrl;
} 