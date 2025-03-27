package cyou.oxling.loanappbackend.dto.notification;

import lombok.Data;

/**
 * 通知DTO
 */
@Data
public class NotificationDTO {
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 发送渠道：0=站内信，1=短信，2=邮件
     */
    private Integer sendChannel;
    
    /**
     * 状态：0=未读，1=已读
     */
    private Integer status;
} 