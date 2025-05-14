package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.user.UserDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户文档数据访问接口
 */
@Mapper
public interface UserDocumentDao {
    
    /**
     * 保存用户文档
     * @param userDocument 用户文档
     * @return 影响行数
     */
    int saveUserDocument(UserDocument userDocument);
    
    /**
     * 更新用户文档
     * @param userDocument 用户文档
     * @return 影响行数
     */
    int updateUserDocument(UserDocument userDocument);

    /**
     * 根据ID删除用户文档
     * @param id 文档ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询用户文档
     * @param id 文档ID
     * @return 用户文档
     */
    UserDocument findById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询用户文档列表
     * @param userId 用户ID
     * @return 用户文档列表
     */
    List<UserDocument> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和文档类型查询用户文档
     * @param userId 用户ID
     * @param docType 文档类型
     * @return 用户文档
     */
    List<UserDocument> findByUserIdAndType(@Param("userId") Long userId, @Param("docType") Integer docType);
}
