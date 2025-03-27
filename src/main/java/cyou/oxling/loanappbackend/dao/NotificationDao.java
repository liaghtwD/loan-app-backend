package cyou.oxling.loanappbackend.dao;

import cyou.oxling.loanappbackend.model.notification.NotificationSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知数据访问接口
 */
@Mapper
public interface NotificationDao {
    
    /**
     * 保存通知
     * @param notification 通知信息
     * @return 影响行数
     */
    int saveNotification(NotificationSchedule notification);
    
    /**
     * 根据用户ID查询通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    List<NotificationSchedule> findByUserId(@Param("userId") Long userId);
    
    /**
     * 更新通知状态
     * @param notificationId 通知ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("notificationId") Long notificationId, @Param("status") Integer status);
    
    /**
     * 根据通知ID查询通知
     * @param notificationId 通知ID
     * @return 通知信息
     */
    NotificationSchedule findById(@Param("notificationId") Long notificationId);
    
    /**
     * 删除通知
     * @param notificationId 通知ID
     * @return 影响行数
     */
    int deleteById(@Param("notificationId") Long notificationId);
} 