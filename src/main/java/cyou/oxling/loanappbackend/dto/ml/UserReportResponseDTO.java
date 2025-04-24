package cyou.oxling.loanappbackend.dto.ml;

import lombok.Data;

/**
 * 用户自报信息响应DTO
 */
@Data
public class UserReportResponseDTO {
    private Long snapshotId;  // 特征快照ID
} 