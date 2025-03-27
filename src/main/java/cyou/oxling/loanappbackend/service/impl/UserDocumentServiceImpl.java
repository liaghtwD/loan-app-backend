package cyou.oxling.loanappbackend.service.impl;

import cyou.oxling.loanappbackend.dao.UserDao;
import cyou.oxling.loanappbackend.dao.UserDocumentDao;
import cyou.oxling.loanappbackend.dto.user.UserDocumentDTO;
import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.model.user.UserDocument;
import cyou.oxling.loanappbackend.model.user.UserInfo;
import cyou.oxling.loanappbackend.service.UserDocumentService;
import cyou.oxling.loanappbackend.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 用户文档服务实现类
 */
@Service
public class UserDocumentServiceImpl implements UserDocumentService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDocumentDao userDocumentDao;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public Long uploadDocument(UserDocumentDTO userDocumentDTO) {
        if (userDocumentDTO == null || userDocumentDTO.getUserId() == null) {
            throw new BusinessException("用户文档信息不能为空");
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userDocumentDTO.getUserId());
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证文档类型是否合法
        if (userDocumentDTO.getDocType() == null || userDocumentDTO.getDocType() < 1 || userDocumentDTO.getDocType() > 6) {
            throw new BusinessException("文档类型不合法");
        }

        // 验证文件是否为空
        if (userDocumentDTO.getFile() == null || userDocumentDTO.getFile().isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 保存文件
        String docUrl = saveFile(userDocumentDTO.getFile(), userDocumentDTO.getUserId());

        // 创建用户文档记录
        UserDocument userDocument = new UserDocument();
        userDocument.setUserId(userDocumentDTO.getUserId());
        userDocument.setDocType(userDocumentDTO.getDocType());
        userDocument.setDocUrl(docUrl);
        userDocument.setDocDescription(userDocumentDTO.getDocDescription());
        userDocument.setStatus(UserDocument.STATUS_PENDING); // 默认待审核
        userDocument.setScore(0); // 默认评分为0
        userDocument.setCreateTime(new Date());
        userDocument.setUpdateTime(new Date());

        // 保存用户文档信息
        userDocumentDao.saveUserDocument(userDocument);

        return userDocument.getId();
    }

    @Override
    public List<UserDocument> getUserDocuments(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }

        return userDocumentDao.findByUserId(userId);
    }

    @Override
    public List<UserDocument> getUserDocumentsByType(Long userId, Integer docType) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 验证用户是否存在
        UserInfo userInfo = userDao.findById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证文档类型是否合法
        if (docType == null || docType < 1 || docType > 6) {
            throw new BusinessException("文档类型不合法");
        }

        return userDocumentDao.findByUserIdAndType(userId, docType);
    }

    @Override
    public boolean updateDocument(Long id, Integer docType, String docDescription, MultipartFile file) {
        if (id == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 验证文档是否存在
        UserDocument userDocument = userDocumentDao.findById(id);
        if (userDocument == null) {
            throw new BusinessException("文档不存在");
        }
        
        // 验证当前用户是否是文档所有者
        Long currentUserId = RequestUtil.getCurrentUserId();
        if (!userDocument.getUserId().equals(currentUserId)) {
            throw new BusinessException(403, "无权限操作他人文档");
        }

        // 如果有新文件上传，则更新文件
        if (file != null && !file.isEmpty()) {
            String docUrl = saveFile(file, userDocument.getUserId());
            userDocument.setDocUrl(docUrl);
        }

        // 更新文档类型和描述
        if (docType != null) {
            if (docType < 1 || docType > 6) {
                throw new BusinessException("文档类型不合法");
            }
            userDocument.setDocType(docType);
        }

        if (StringUtils.hasText(docDescription)) {
            userDocument.setDocDescription(docDescription);
        }

        userDocument.setStatus(UserDocument.STATUS_PENDING); // 更新后重新审核
        userDocument.setUpdateTime(new Date());

        return userDocumentDao.updateUserDocument(userDocument) > 0;
    }

    /**
     * 保存文件
     * @param file 文件
     * @param userId 用户ID
     * @return 文件URL
     */
    private String saveFile(MultipartFile file, Long userId) {
        // 创建用户文件目录
        String userDirPath = uploadPath + "/user_" + userId;
        File userDir = new File(userDirPath);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = Paths.get(userDirPath, filename);
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new BusinessException("文件保存失败: " + e.getMessage());
        }

        // 返回相对路径
        return "/user_" + userId + "/" + filename;
    }
} 