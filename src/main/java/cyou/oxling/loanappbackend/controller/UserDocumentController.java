package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.dto.user.UserDocumentDTO;
import cyou.oxling.loanappbackend.model.user.UserDocument;
import cyou.oxling.loanappbackend.service.UserDocumentService;
import cyou.oxling.loanappbackend.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户文档控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserDocumentController {

    @Autowired
    private UserDocumentService userDocumentService;

    /**
     * 上传文档
     * @param docType 文档类型
     * @param docDescription 文档描述
     * @param file 文件
     * @return 文档ID
     */
    @PostMapping("/documents")
    public Result<Long> uploadDocument(
            @RequestParam Integer docType,
            @RequestParam(required = false) String docDescription,
            @RequestParam MultipartFile file) {
        
        // 从JWT中获取用户ID
        Long userId = RequestUtil.getCurrentUserId();
        
        UserDocumentDTO documentDTO = new UserDocumentDTO();
        documentDTO.setUserId(userId);
        documentDTO.setDocType(docType);
        documentDTO.setDocDescription(docDescription);
        documentDTO.setFile(file);
        
        return Result.success(userDocumentService.uploadDocument(documentDTO));
    }

    /**
     * 获取用户文档列表
     * @param docType 文档类型（可选）
     * @return 文档列表
     */
    @GetMapping("/documents/list")
    public Result<List<UserDocument>> getUserDocuments(
            @RequestParam(required = false) Integer docType) {
        
        // 从JWT中获取用户ID
        Long userId = RequestUtil.getCurrentUserId();
        
        if (docType != null) {
            return Result.success(userDocumentService.getUserDocumentsByType(userId, docType));
        } else {
            return Result.success(userDocumentService.getUserDocuments(userId));
        }
    }

    /**
     * 更新文档
     * @param id 文档ID
     * @param docType 文档类型
     * @param docDescription 文档描述
     * @param file 文件
     * @return 更新结果
     */
    @PostMapping("/documents/update/{id}")
    public Result<Boolean> updateDocument(
            @PathVariable Long id,
            @RequestParam(required = false) Integer docType,
            @RequestParam(required = false) String docDescription,
            @RequestParam(required = false) MultipartFile file) {
        
        // 从JWT中获取用户ID（用于权限验证）
        Long userId = RequestUtil.getCurrentUserId();
        
        // 验证该文档是否属于当前用户（此步骤应在Service中实现）
        
        return Result.success(userDocumentService.updateDocument(id, docType, docDescription, file));
    }
} 