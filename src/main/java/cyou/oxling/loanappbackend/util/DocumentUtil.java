package cyou.oxling.loanappbackend.util;

import cyou.oxling.loanappbackend.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文档工具类，用于处理文档保存、生成下载链接和验证下载链接等功能
 */
@Component
public class DocumentUtil {


    // 定义文档下载API的路径
    private static final String DOWNLOAD_API_PATH = "/api/user/document/download";

    // 注入Redis工具类，用于缓存下载链接等信息
    @Autowired
    private RedisUtil redisUtil;


    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("gif", "image/gif");
        // 更多类型可继续添加
    }

    /**
     * 保存文档到指定目录
     *
     * @param file     要保存的文件
     * @param userId   用户ID
     * @param docType  文档类型
     * @return         保存的文件路径
     * @throws IOException 当文件保存失败时抛出此异常
     */
    public String saveDocument(MultipartFile file, Long userId, Integer docType, String docDir) {
        try {
            // 生成文件路径
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = now.format(formatter);
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = timestamp + fileExtension; // 保留原始扩展名
            String filePath = Paths.get(docDir, userId.toString(), docType.toString(), filename).toString();

            // 保存文件
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);

            return filePath; // 返回保存的文件路径
        } catch (IOException e) {
            // 处理文件保存时的IO异常
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成文档下载链接
     *
     * @param userId       用户ID
     * @param fileId       文件ID
     * @param filePath     文件路径
     * @param expirationTime   链接过期时间
     * @return             编码后的下载链接
     */
 /**
 * 生成文档下载链接（不再设置过期时间）
 *
 * @param userId       用户ID
 * @param fileId       文件ID
 * @param filePath     文件路径
 * @return             编码后的下载链接
 */
public String generateDownloadLink(Long userId, Long fileId, String filePath) {
    // 生成UUID
    String uuid = UUID.randomUUID().toString();

    // 拼接UUID、用户ID、文件ID和接口路径
    String downloadLinkData = uuid + ":" + userId + ":" + fileId + ":" + DOWNLOAD_API_PATH + uuid ;

    // 编码拼接后的字符串
    String encodedDownloadLink = Base64.getUrlEncoder().withoutPadding().encodeToString(downloadLinkData.getBytes());

    // 存储到Redis（不设置过期时间）
    String key = "download_link:" + encodedDownloadLink;
    redisUtil.setString(key, userId + ":" + fileId + ":" + filePath);

    return encodedDownloadLink; // 返回编码后的下载链接
}

/**
 * 显式删除指定的下载链接
 *
 * @param encodedDownloadLink 编码后的下载链接
 */
public void deleteDownloadLink(String encodedDownloadLink) {
    String key = "download_link:" + encodedDownloadLink;
    redisUtil.delete(key);
}


    /**
     * 验证下载链接的有效性
     *
     * @param encodedDownloadLink 编码后的下载链接
     * @param userId              用户ID
     * @param fileId              文件ID
     * @param errorMsg            错误信息
     * @return                    链接是否有效
     */
    public boolean validateDownloadLink(String encodedDownloadLink, Long userId, Long fileId, String[] errorMsg) {
    try {
        // 解码下载链接
        String decodedDownloadLink = new String(Base64.getUrlDecoder().decode(encodedDownloadLink));
        String[] parts = decodedDownloadLink.split(":");

        if (parts.length != 4) {
            errorMsg[0] = "下载链接格式不正确";
            return false;
        }

        String uuid = parts[0];
        Long linkUserId = Long.parseLong(parts[1]);
        Long linkFileId = Long.parseLong(parts[2]);
        String apiPath = parts[3];

        if (!apiPath.equals(DOWNLOAD_API_PATH + uuid)) {
            errorMsg[0] = "下载路径不匹配";
            return false;
        }

        if (!userId.equals(RequestUtil.getCurrentUserId())) {
            errorMsg[0] = "无权访问该下载链接";
            return false;
        }

        if (!userId.equals(linkUserId) || !fileId.equals(linkFileId)) {
            errorMsg[0] = "用户ID或文件ID不匹配";
            return false;
        }

        String key = "download_link:" + encodedDownloadLink;
        String value = redisUtil.getString(key);

        if (value == null) {
            errorMsg[0] = "链接不存在";
            return false;
        }

        String[] valueParts = value.split(":");
        if (valueParts.length != 3) {
            errorMsg[0] = "存储下载链接格式不正确";
            return false;
        }

        Long storedUserId = Long.parseLong(valueParts[0]);
        Long storedFileId = Long.parseLong(valueParts[1]);

        if (!userId.equals(storedUserId) || !fileId.equals(storedFileId)) {
            errorMsg[0] = "存储用户ID或文件ID不匹配";
            return false;
        }

        errorMsg[0] = "验证通过";
        return true;

    } catch (IllegalArgumentException e) {
        errorMsg[0] = "解码失败：" + e.getMessage();
        return false;
    } catch (Exception e) {
        errorMsg[0] = "验证过程中发生异常：" + e.getMessage();
        return false;
    }
}
/**
 * 根据编码的下载链接下载文档
 * 该方法首先从Redis中获取下载链接的实际路径，然后检查文件是否存在，
 * 如果文件存在，则返回文件资源，否则抛出异常
 *
 * @param encodedDownloadLink 编码的下载链接字符串
 * @return 返回文件系统资源
 * @throws BusinessException 如果文件不存在，则抛出业务异常
 */
public ResponseEntity<FileSystemResource> downloadDocument(String method, String encodedDownloadLink,String fileName){
    // 构造Redis键
    String key = "download_link:" + encodedDownloadLink;
    // 从Redis中获取文件路径信息
    String value = redisUtil.getString(key);
    // 分割获取到的信息，提取文件路径
    String[] valueParts = value.split(":");
    String filePath = valueParts[2];

    // 获取文件路径的Path对象
    Path path = Paths.get(filePath);
    // 检查文件是否存在，如果不存在则抛出异常
    if (!Files.exists(path)) {
        throw new BusinessException("文件不存在");
    }
    return download(path,method,fileName);
}

public ResponseEntity<FileSystemResource> download(Path filePath, String method, String fileName){
    // 创建 FileSystemResource 对象
    FileSystemResource fileSystemResource = new FileSystemResource(filePath);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, method + "; filename=" + fileName);
    headers.setContentType(MediaType.parseMediaType(getMimeType(filePath.toString())));

    // 返回 ResponseEntity
    try {
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileSystemResource.contentLength())
                .body(fileSystemResource);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }

    public String getMimeType(String fileName) {
        String ext = getFileExtension(fileName);
        return MIME_TYPES.getOrDefault(ext, "application/octet-stream");
    }

    /**
     * 删除文件系统中的文件
     *
     * @param filePath 要删除的文件路径
     */
    public void deleteFile(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                Files.delete(path); // 从文件系统中删除文件
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file", e);
            }
        }
    }
}
