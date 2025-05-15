package cyou.oxling.loanappbackend.service.impl;

import com.getui.push.v2.sdk.api.PushApi;
import com.getui.push.v2.sdk.api.UserApi;
import com.getui.push.v2.sdk.dto.req.CidAliasListDTO;
import com.getui.push.v2.sdk.dto.req.CidAliasListDTO.CidAlias;
import cyou.oxling.loanappbackend.service.PushService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        userApi.bindAlias(aliasListDTO.add(new CidAlias(cid, userId.toString())));
    }

    @Override
    public Long clientPush(String cId, String title, String body) {
        return 0L;
    }

    @Override
    public Long userPush(Long userId, String title, String body) {
        return 0L;
    }
}
