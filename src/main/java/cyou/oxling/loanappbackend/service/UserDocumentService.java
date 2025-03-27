package cyou.oxling.loanappbackend.service;

import cyou.oxling.loanappbackend.dto.user.UserDocumentDTO;
import cyou.oxling.loanappbackend.model.user.UserDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户文档服务接口
 */
public interface UserDocumentService {
    
    /**
     * 上传用户文档
     * @param userDocumentDTO 用户文档信息
     * @return 文档ID
     */
    Long uploadDocument(UserDocumentDTO userDocumentDTO);
    
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
     * @param id 文档ID
     * @param docType 文档类型
     * @param docDescription 文档描述
     * @param file 文档文件
     * @return 是否更新成功
     */
    boolean updateDocument(Long id, Integer docType, String docDescription, MultipartFile file);
} 