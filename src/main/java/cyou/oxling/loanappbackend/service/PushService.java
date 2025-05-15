package cyou.oxling.loanappbackend.service;

/**
 * @author: Chanler
 * @date: 2025/5/15 - 15:29
 */
public interface PushService {
    /** 
     * 设置用户id为设备id别名
     * @param cid
     * @param userId
     * @return Long
     */
    Long bind(String cid, Long userId);

    /**
     * 向设备推送消息
     * @param cId
     * @param title
     * @param body
     * @return Long
     */
    Long clientPush(String cId, String title, String body);

    /**
     * 向用户推送消息
     * @param userId
     * @param title
     * @param body
     * @return Long
     */
    Long userPush(Long userId, String title, String body);
}
