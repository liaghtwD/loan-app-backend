package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.common.Result;
import cyou.oxling.loanappbackend.dto.document.DocumentDownloadDTO;
import cyou.oxling.loanappbackend.dto.document.DocumentUploadDTO;
import cyou.oxling.loanappbackend.dto.user.UserDocumentDTO;
import cyou.oxling.loanappbackend.model.user.UserDocument;
import cyou.oxling.loanappbackend.service.UserDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户文档控制器，用于处理与用户文档相关的请求，包括上传、获取、更新和删除操作。
 */
@RestController
@RequestMapping("/api/user/document")
public class UserDocumentController {

    @Autowired
    private UserDocumentService userDocumentService;

    /**
     * 上传用户文档接口。
     * <p>
     * 该接口接收包含文档类型、文档描述和文件的DTO对象，调用服务层方法完成文档上传，
     * 并返回上传成功的文档ID。
     *
     * @param documentDTO 包含文档类型、文档描述和文件的DTO对象
     * @return 返回包含文档ID的成功结果
     */
    @PostMapping("/upload")
    public Result<DocumentUploadDTO> uploadDocument(@ModelAttribute UserDocumentDTO documentDTO) {
        return Result.success(userDocumentService.uploadDocument(documentDTO));
    }

    /**
     * 获取用户文档列表接口。
     * <p>
     * 根据传入的DTO对象中的用户ID和文档类型，调用服务层方法获取对应的文档列表。
     * 如果未指定文档类型，则返回该用户的所有文档；否则返回指定类型的文档。
     *
     * @param documentDTO 包含用户ID和文档类型的DTO对象
     * @return 返回包含文档列表的成功结果
     */
    @GetMapping("/list")
    public Result<List<UserDocument>> getUserDocuments(@ModelAttribute UserDocumentDTO documentDTO) {
        if (documentDTO.getDocType() == null) {
            return Result.success(userDocumentService.getUserDocuments(documentDTO.getUserId()));
        } else {
            return Result.success(userDocumentService.getUserDocumentsByType(documentDTO.getUserId(), documentDTO.getDocType()));
        }
    }

    /**
     * 更新用户文档接口。
     * <p>
     * 该接口接收包含文档类型、文档描述和文件的DTO对象，调用服务层方法完成文档更新，
     * 并返回更新结果。
     *
     * @param documentDTO 包含文档类型、文档描述和文件的DTO对象
     * @return 返回包含更新结果的成功结果
     */
    @PostMapping("/update")
    public Result<DocumentUploadDTO> updateDocument(@ModelAttribute UserDocumentDTO documentDTO) {
        return Result.success(userDocumentService.updateDocument(documentDTO));
    }


    /**
     * 删除用户文档接口。
     * <p>
     * 该接口接收包含文档类型、文档描述和文件的DTO对象，调用服务层方法完成文档删除，
     * 并返回删除结果。
     *
     * @param documentDTO 包含文档类型、文档描述和文件的DTO对象
     * @return 返回包含删除结果的成功结果
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteDocument(@ModelAttribute UserDocumentDTO documentDTO) {
        return Result.success(userDocumentService.deleteDocument(documentDTO.getId()));
    }

    @GetMapping("/download")
    public ResponseEntity<FileSystemResource> downloadDocument(@ModelAttribute DocumentDownloadDTO documentDownloadDTO) {
        return userDocumentService.downloadDocument(documentDownloadDTO);
    }
}
