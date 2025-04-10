package cyou.oxling.loanappbackend.service.impl;

import cyou.oxling.loanappbackend.dao.UserDocumentDao;
import cyou.oxling.loanappbackend.dto.user.UserDocumentDTO;
import cyou.oxling.loanappbackend.exception.BusinessException;
import cyou.oxling.loanappbackend.model.user.UserDocument;
import cyou.oxling.loanappbackend.service.UserDocumentService;
import cyou.oxling.loanappbackend.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserDocumentServiceImpl implements UserDocumentService {

    @Autowired
    private UserDocumentDao userDocumentDao;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Long uploadDocument(UserDocumentDTO userDocumentDTO) {

        // 保存文件
        String realPath = fileUtil.saveFile(userDocumentDTO.getFile(), userDocumentDTO.getUserId(), userDocumentDTO.getDocType());

        // 创建用户文档记录
        UserDocument userDocument = new UserDocument();
        userDocument.setUserId(userDocumentDTO.getUserId());
        userDocument.setDocType(userDocumentDTO.getDocType());
        userDocument.setDocUrl(realPath); // 存储真实路径
        userDocument.setDocDescription(userDocumentDTO.getDocDescription());
        userDocument.setStatus(UserDocument.STATUS_PENDING);
        userDocument.setScore(0);
        userDocument.setCreateTime(new Date());
        userDocument.setUpdateTime(new Date());

        // 保存用户文档信息
        userDocumentDao.saveUserDocument(userDocument);

        return userDocument.getId();
    }

    @Override
    public boolean updateDocument(Long id, Integer docType, String docDescription, MultipartFile file) {

        // 如果有新文件上传，则更新文件
        if (file != null && !file.isEmpty()) {
            String realPath = fileUtil.saveFile(file, userDocument.getUserId(), userDocument.getDocType());
            userDocument.setDocUrl(realPath); // 更新真实路径
        }

        // 更新文档类型和描述
        if (docType != null) {
            userDocument.setDocType(docType);
        }

        if (docDescription != null) {
            userDocument.setDocDescription(docDescription);
        }

        userDocument.setStatus(UserDocument.STATUS_PENDING);
        userDocument.setUpdateTime(new Date());

        return userDocumentDao.updateUserDocument(userDocument) > 0;
    }

    @Override
    public String generateDownloadLink(Long fileId, String filePath, long expirationTime) {
        // 生成UUID作为哈希值
        String hash = UUID.randomUUID().toString();

        // 将哈希值与文件路径存入Redis
        redisTemplate.opsForValue().set(hash, filePath, expirationTime, TimeUnit.SECONDS);

        // 返回下载链接
        return "/download/" + hash;
    }

    @Override
    public boolean softDeleteFile(Long fileId) {
        // 软删除文件
        fileUtil.softDeleteFile(fileId);
        return true;
    }

    @Override
    public boolean deleteFile(Long fileId) {
        // 删除文件
        fileUtil.deleteFile(fileId);
        return true;
    }
}