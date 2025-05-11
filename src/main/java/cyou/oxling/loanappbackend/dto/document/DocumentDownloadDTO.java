package cyou.oxling.loanappbackend.dto.document;

import lombok.Data;

/**
 * 文档下载信息传输对象
 * 用于封装文档下载所需的信息，包括用户ID、文件ID和编码后的下载链接
 */
@Data
public class DocumentDownloadDTO {
    /**
     * 用户ID
     * 标识请求下载文档的用户
     */
    private Long userId;

    /**
     * 文件ID
     * 标识待下载的文档文件
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
     * 编码后的下载链接
     * 提供文档的下载地址，经过编码以确保安全传输
     */
    private String encodedDownloadLink;
}
