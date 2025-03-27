package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.document.UserDocuments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档数据访问接口
 */
@Mapper
public interface DocumentDao {
    
    /**
     * 保存用户文档
     * @param document 文档信息
     * @return 影响行数
     */
    int saveDocument(UserDocuments document);
    
    /**
     * 根据用户ID查询文档列表
     * @param userId 用户ID
     * @return 文档列表
     */
    List<UserDocuments> findByUserId(@Param("userId") Long userId);
    
    /**
     * 更新文档状态
     * @param documentId 文档ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("documentId") Long documentId, @Param("status") Integer status);
    
    /**
     * 根据文档ID查询文档
     * @param documentId 文档ID
     * @return 文档信息
     */
    UserDocuments findById(@Param("documentId") Long documentId);
} 