package cyou.oxling.loanappbackend.config;

import com.getui.push.v2.sdk.ApiHelper;
import com.getui.push.v2.sdk.GtApiConfiguration;
import com.getui.push.v2.sdk.api.PushApi;
import com.getui.push.v2.sdk.api.UserApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Chanler
 * @date: 2025/5/15 - 15:58
 */
@Configuration
public class PushConfig {

    @Value("${getui.appId}") // 从配置文件读取
    private String appId;

    @Value("${getui.appKey}")
    private String appKey;

    @Value("${getui.masterSecret}")
    private String masterSecret;

    @Value("${getui.domain:https://restapi.getui.com/v2/}") // 提供默认值
    private String domain;

    @Bean
    public ApiHelper apiHelper() {
        System.setProperty("http.maxConnections", "200");
        GtApiConfiguration apiConfiguration = new GtApiConfiguration();
        apiConfiguration.setAppId(appId);
        apiConfiguration.setAppKey(appKey);
        apiConfiguration.setMasterSecret(masterSecret);
        apiConfiguration.setDomain(domain);
        return ApiHelper.build(apiConfiguration);
    }

    @Bean
    public PushApi pushApi(ApiHelper apiHelper) {
        return apiHelper.creatApi(PushApi.class);
    }

    @Bean
    public UserApi userApi(ApiHelper apiHelper) {
        return apiHelper.creatApi(UserApi.class);
    }
}
