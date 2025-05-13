package cyou.oxling.loanappbackend.controller;

import cyou.oxling.loanappbackend.service.ShixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShixinController {

    @Autowired
    @Qualifier("noredis")
    private ShixinService shixinService;

    @RequestMapping (path = "/query-shixin", method = RequestMethod.GET)
    public Map<String, Object> queryShixin(
            @RequestParam String name,
            @RequestParam String idCard) {
        return shixinService.queryShixinInfo(name, idCard);
    }
}

