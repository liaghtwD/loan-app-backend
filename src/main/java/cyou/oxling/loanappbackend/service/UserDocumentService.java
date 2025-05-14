package cyou.oxling.loanappbackend.service;

import cyou.oxling.loanappbackend.dto.document.DocumentDownloadDTO;
import cyou.oxling.loanappbackend.dto.document.DocumentUploadDTO;
import cyou.oxling.loanappbackend.dto.user.UserDocumentDTO;
import cyou.oxling.loanappbackend.model.user.UserDocument;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 用户文档服务接口
 */
public interface UserDocumentService {

    /**
     * 上传用户文档
     * @param userDocumentDTO 用户文档信息
     * @return 包含文档ID和下载链接的结果对象
     */
    DocumentUploadDTO uploadDocument(UserDocumentDTO userDocumentDTO);

    /**
     * 获取用户文档列表
     * @param userId 用户ID
     * @return 用户文档列表
     */
    List<UserDocument> getUserDocuments(Long userId);

    /**
     * 获取用户特定类型的文档列表
     * @param userId 用户ID
     * @param docType 文档类型
     * @return 用户文档列表
     */
    List<UserDocument> getUserDocumentsByType(Long userId, Integer docType);

    /**
     * 更新用户文档
     * @param userDocumentDTO 包含更新信息的DTO对象
     * @return 包含文档ID和下载链接的结果对象
     */
    DocumentUploadDTO updateDocument(UserDocumentDTO userDocumentDTO);

    /**
     * 删除文件
     * @param id 文件ID
     * @return 是否删除成功
     */
    boolean deleteDocument(Long id);

    /**
     * 下载文档
     * @param documentDownloadDTO 包含下载信息的DTO对象
     * @return 文件系统资源对象
     */
    ResponseEntity<FileSystemResource> downloadDocument(DocumentDownloadDTO documentDownloadDTO);

}
