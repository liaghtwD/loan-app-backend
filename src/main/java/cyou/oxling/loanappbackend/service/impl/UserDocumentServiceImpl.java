package cyou.oxling.loanappbackend.service.impl;

import cyou.oxling.loanappbackend.dao.UserDao;
import cyou.oxling.loanappbackend.dao.UserDocumentDao;
import cyou.oxling.loanappbackend.dto.document.DocumentDownloadDTO;
import cyou.oxling.loanappbackend.dto.document.DocumentUploadDTO;
import cyou.oxling.loanappbackend.dto.user.UserDocumentDTO;
import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.model.user.UserDocument;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import cyou.oxling.loanappbackend.service.UserDocumentService;
import cyou.oxling.loanappbackend.util.DocumentUtil;
import cyou.oxling.loanappbackend.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 用户文档服务实现类
 */
@Service
public class UserDocumentServiceImpl implements UserDocumentService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDocumentDao userDocumentDao;

    @Autowired
    private DocumentUtil documentUtil;

    // 定义文档保存的目录
    private static final String docDir = "/host/user/document";


    public class ERROR_MESSAGES {
        public static final String DOCUMENT_ID_NULL = "文档ID不能为空";
        public static final String DOCUMENT_NOT_FOUND = "文档不存在";
        public static final String NO_PERMISSION = "无权限操作他人文档";
        public static final String USER_DOCUMENT_INFO_NULL = "用户文档信息不能为空";
        public static final String USER_NOT_FOUND = "用户不存在";
        public static final String DOC_TYPE_INVALID = "文档类型不合法";
        public static final String FILE_EMPTY = "文件不能为空";
        public static final String FILE_SAVE_FAILED = "文件保存失败";
    }

    @Override
    public DocumentUploadDTO uploadDocument(UserDocumentDTO userDocumentDTO) {
        if (userDocumentDTO == null || userDocumentDTO.getUserId() == null) {
            throw new BusinessException(ERROR_MESSAGES.USER_DOCUMENT_INFO_NULL);
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userDocumentDTO.getUserId());
        if (userInfo == null) {
            throw new BusinessException(ERROR_MESSAGES.USER_NOT_FOUND);
        }

        // 验证文档类型是否合法
        if (userDocumentDTO.getDocType() == null || userDocumentDTO.getDocType() < 1 || userDocumentDTO.getDocType() > 6) {
            throw new BusinessException(ERROR_MESSAGES.DOC_TYPE_INVALID);
        }

        // 验证文件是否为空
        if (userDocumentDTO.getFile() == null || userDocumentDTO.getFile().isEmpty()) {
            throw new BusinessException(ERROR_MESSAGES.FILE_EMPTY);
        }

        // 保存文件
        String docUrl = documentUtil.saveDocument(userDocumentDTO.getFile(), userDocumentDTO.getUserId(), userDocumentDTO.getDocType(), docDir);
        if (docUrl == null){
            throw new BusinessException(ERROR_MESSAGES.FILE_SAVE_FAILED);
        }

        // 创建用户文档记录
        UserDocument userDocument = new UserDocument();
        userDocument.setUserId(userDocumentDTO.getUserId());
        userDocument.setDocType(userDocumentDTO.getDocType());
        userDocument.setFileName(userDocumentDTO.getFile().getOriginalFilename());
        userDocument.setDocUrl(docUrl);
        userDocument.setDocDescription(userDocumentDTO.getDocDescription());
        userDocument.setStatus(UserDocument.STATUS_PENDING); // 默认待审核
        userDocument.setScore(0); // 默认评分为0
        userDocument.setCreateTime(new Date());
        userDocument.setUpdateTime(new Date());

        // 保存用户文档信息
        userDocumentDao.saveUserDocument(userDocument);

        // 创建用户文档响应体
        DocumentUploadDTO documentUploadDTO = new DocumentUploadDTO();
        documentUploadDTO.setId(userDocument.getId());
        documentUploadDTO.setDocType(userDocumentDTO.getDocType());
        // 修改文档地址为一次性链接，永远不返回文档真实路径给前端
        documentUploadDTO.setDocUrl(documentUtil.generateDownloadLink(userDocumentDTO.getUserId(), userDocument.getId(), docUrl));
        return documentUploadDTO;
    }

    @Override
    public List<UserDocument> getUserDocuments(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException(ERROR_MESSAGES.USER_NOT_FOUND);
        }

        // 修改文档地址为一次性链接
        List<UserDocument> userDocumentList = userDocumentDao.findByUserId(userId);
        for(UserDocument userDocument : userDocumentList){
            String download_url = documentUtil.generateDownloadLink(userId,userDocument.getId(), userDocument.getDocUrl());
            userDocument.setDocUrl(download_url);
        }

        return userDocumentList;
    }

    @Override
    public List<UserDocument> getUserDocumentsByType(Long userId, Integer docType) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException(ERROR_MESSAGES.USER_NOT_FOUND);
        }

        // 验证文档类型是否合法
        if (docType == null || docType < 1 || docType > 6) {
            throw new BusinessException(ERROR_MESSAGES.DOC_TYPE_INVALID);
        }

        // 修改文档地址为一次性链接
        List<UserDocument> userDocumentList = userDocumentDao.findByUserIdAndType(userId, docType);
        for(UserDocument userDocument : userDocumentList){
            String download_url = documentUtil.generateDownloadLink(userId,userDocument.getId(), userDocument.getDocUrl());
            userDocument.setDocUrl(download_url);
        }

        return userDocumentList;
    }

    @Override
    public DocumentUploadDTO updateDocument(UserDocumentDTO userDocumentDTO) {
        // 检查文档ID是否为空
        if (userDocumentDTO.getId() == null) {
            throw new BusinessException(ERROR_MESSAGES.DOCUMENT_ID_NULL);
        }

        // 验证文档是否存在
        UserDocument userDocument = userDocumentDao.findById(userDocumentDTO.getId());
        if (userDocument == null) {
            throw new BusinessException(ERROR_MESSAGES.DOCUMENT_NOT_FOUND);
        }

        // 验证当前用户是否是文档所有者
        Long currentUserId = RequestUtil.getCurrentUserId();
        if (!userDocument.getUserId().equals(currentUserId)) {
            throw new BusinessException(403, ERROR_MESSAGES.NO_PERMISSION);
        }

        // 如果有新文件上传，则更新文件
        MultipartFile file = userDocumentDTO.getFile();
        if (file != null && !file.isEmpty()) {
            String docUrl = documentUtil.saveDocument(file, userDocument.getUserId(), userDocument.getDocType(), docDir);
            userDocument.setDocUrl(docUrl);
        }

        // 更新文档类型和描述
        Integer docType = userDocumentDTO.getDocType();
        if (docType != null) {
            // 验证文档类型是否有效
            if (docType < 1 || docType > 6) {
                throw new BusinessException(ERROR_MESSAGES.DOC_TYPE_INVALID);
            }
            userDocument.setDocType(docType);
            userDocumentDTO.setDocType(docType);
        }

        // 更新文档描述
        String docDescription = userDocumentDTO.getDocDescription();
        if (StringUtils.hasText(docDescription)) {
            userDocument.setDocDescription(docDescription);
        }

        // 更新文档状态为待审核
        userDocument.setStatus(UserDocument.STATUS_PENDING);
        userDocument.setUpdateTime(new Date());
        userDocumentDao.updateUserDocument(userDocument);

        // 创建并返回文档上传DTO
        DocumentUploadDTO documentUploadDTO = new DocumentUploadDTO();
        documentUploadDTO.setId(userDocument.getId());
        documentUploadDTO.setDocUrl(documentUtil.generateDownloadLink(userDocument.getUserId(), userDocument.getId(), userDocument.getDocUrl()));

        return documentUploadDTO;
    }


/**
 * 删除文档
 * 此方法用于删除用户文档根据文件ID它首先检查文件ID是否为空，然后查找对应的用户文档如果文档不存在或当前用户
 * 没有权限删除该文档，将抛出相应的异常如果文档可以被删除，它将从存储和数据库中删除该文档
 *
 * @param fileId 文档的唯一标识符如果为null或对应的文档不存在，将抛出异常
 * @return 删除操作成功返回true
 * @throws BusinessException 如果文件ID为空、文档不存在、当前用户没有权限删除或删除过程中发生错误，将抛出此异常
 */
@Override
public boolean deleteDocument(Long fileId) {
    // 检查文件ID是否为空
    if (fileId == null) {
        throw new BusinessException(ERROR_MESSAGES.DOCUMENT_ID_NULL);
    }

    // 根据文件ID查找用户文档
    UserDocument userDocument = userDocumentDao.findById(fileId);
    // 检查用户文档是否存在
    if (userDocument == null) {
        throw new BusinessException(ERROR_MESSAGES.DOCUMENT_NOT_FOUND);
    }

    // 获取当前用户ID
    Long currentUserId = RequestUtil.getCurrentUserId();
    // 检查当前用户是否有权限删除该文档
    if (!userDocument.getUserId().equals(currentUserId)) {
        throw new BusinessException(403, ERROR_MESSAGES.NO_PERMISSION);
    }

    try {
        // 删除文档
        documentUtil.deleteFile(userDocument.getDocUrl());
        // 删除数据库记录
        userDocumentDao.deleteById(fileId);
    } catch (Exception e) {
        // 处理删除过程中可能发生的异常
        throw new BusinessException(500, "删除文档时发生错误，请稍后重试");
    }
    return true;
}



    @Override
    public ResponseEntity<FileSystemResource> downloadDocument(DocumentDownloadDTO documentDownloadDTO) {
        if (documentDownloadDTO.getId() == null) {
            throw new BusinessException(ERROR_MESSAGES.DOCUMENT_ID_NULL);
        }
        if (documentDownloadDTO.getUserId() == null) {
            throw new BusinessException(ERROR_MESSAGES.USER_NOT_FOUND);
        }
        String[] errorMsg = new String[1];// 用于接收错误信息
        if (!documentUtil.validateDownloadLink(documentDownloadDTO.getEncodedDownloadLink(), documentDownloadDTO.getUserId(), documentDownloadDTO.getId(), errorMsg)){
            throw new BusinessException(errorMsg[0]);
        }
        UserDocument userDocument = userDocumentDao.findById(documentDownloadDTO.getId());
        ResponseEntity<FileSystemResource> fileSystemResource = documentUtil.downloadDocument(documentDownloadDTO.getEncodedDownloadLink(), userDocument.getDocUrl());
        if (documentDownloadDTO.getDocType() != 0){
            documentUtil.deleteDownloadLink(documentDownloadDTO.getEncodedDownloadLink());
        }
        return fileSystemResource;
    }
}
