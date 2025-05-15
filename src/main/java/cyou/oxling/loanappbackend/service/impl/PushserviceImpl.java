package cyou.oxling.loanappbackend.service.impl;

import com.getui.push.v2.sdk.api.PushApi;
import com.getui.push.v2.sdk.api.UserApi;
import com.getui.push.v2.sdk.common.ApiResult;
import com.getui.push.v2.sdk.dto.req.Audience;
import com.getui.push.v2.sdk.dto.req.CidAliasListDTO;
import com.getui.push.v2.sdk.dto.req.CidAliasListDTO.CidAlias;
import com.getui.push.v2.sdk.dto.req.message.PushDTO;
import com.getui.push.v2.sdk.dto.req.message.PushMessage;
import com.getui.push.v2.sdk.dto.req.message.android.GTNotification;
import cyou.oxling.loanappbackend.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: Chanler
 * @date: 2025/5/15 - 15:52
 */
@Service
public class PushserviceImpl implements PushService {
    @Autowired
    private PushApi pushApi;

    @Autowired
    private UserApi userApi;

    @Override
    public Long bind(String cid, Long userId) {
        CidAliasListDTO aliasListDTO = new CidAliasListDTO();
        if (userApi.bindAlias(aliasListDTO.add(new CidAlias(cid, userId.toString())))
                .getMsg().equals("success")) {
            return 1L;
        }
        return 0L;
    }


    @Override
    public Long clientPush(String cId, String title, String body) {
        return realPush("client", cId, title, body);
    }


    @Override
    public Long userPush(Long userId, String title, String body) {
        return realPush("user", userId.toString(), title, body);
    }

    private Long realPush(String type, String id, String title, String body) {
        //根据cid进行单推
        PushDTO<Audience> pushDTO = new PushDTO<Audience>();
        pushDTO.setRequestId(System.currentTimeMillis() + "");

        // 设置个推通道参数
        PushMessage pushMessage = new PushMessage();
        pushDTO.setPushMessage(pushMessage);
        GTNotification notification = new GTNotification();
        pushMessage.setNotification(notification);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setClickType("url");
        notification.setUrl("https://www.getui.com");

        Audience audience = new Audience();
        pushDTO.setAudience(audience);
        if (type.equals("client")) {
            audience.addCid(id);
        } else if (type.equals("user")) {
            audience.addAlias(id);
        }

        ApiResult<Map<String, Map<String, String>>> apiResult = pushApi.pushToSingleByCid(pushDTO);
        if (apiResult.isSuccess()) {
            // success
            return 1L;
        } else {
            // failed
            return 0L;
        }
    }
}
