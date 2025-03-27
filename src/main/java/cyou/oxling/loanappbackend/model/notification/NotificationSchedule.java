package cyou.oxling.loanappbackend.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 通知计划/发送记录表
 * 
 * 字段说明：
 * id - 主键ID
 * userId - 用户ID，关联user_info.id
 * title - 通知标题
 * content - 通知内容
 * sendChannel - 发送渠道：0=短信；1=邮箱；2=站内消息；3=微信推送等
 * msgType - 消息类型
 * status - 状态：0=未发送；1=已发送；2=发送失败等
 * createTime - 创建时间
 * sendTime - 实际发送时间，可为空
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSchedule {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer sendChannel;
    private String msgType;
    private Integer status;
    private Date createTime;
    private Date sendTime;
} 