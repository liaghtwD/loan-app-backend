package cyou.oxling.loanappbackend.service;

import java.util.Map;

public interface ShixinService {
    Map<String, Object> queryShixinInfo(String name, String idCard);
}