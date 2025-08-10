package com.anpilogoff.audioDataService.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "qobuz")
@Setter
@Getter
public class SearchProperties {
    private String appId;
    private String appSecret;
    private String qobuzBaseUrl;
    private String userAuthToken;
    private String userAlternativeAuthToken;
}
