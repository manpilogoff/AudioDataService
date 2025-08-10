package com.anpilogoff.audioDataService.service;

import com.anpilogoff.audioDataService.config.SearchProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QobuzApiServiceTest {
    private SearchProperties properties;
    private WebClient webClient;
    private QobuzApiService service;

    @BeforeEach
    void setUp() {
        properties = mock(SearchProperties.class);
        webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);

        when(properties.getQobuzBaseUrl()).thenReturn("https://test.qobuz.com/");
        when(properties.getAppId()).thenReturn("app123");
        when(properties.getAppSecret()).thenReturn("secretXYZ");
        when(properties.getUserAuthToken()).thenReturn("tokenA");
        when(properties.getUserAlternativeAuthToken()).thenReturn("tokenB");

        service = new QobuzApiService(properties, webClient);
    }

    @Test
    void testCalculateMD5() throws Exception {
        // Доступ через Reflection для приватного метода
        var method = QobuzApiService.class.getDeclaredMethod("calculateMD5", String.class);
        method.setAccessible(true);

        String md5 = (String) method.invoke(service, "test");
        assertEquals("098f6bcd4621d373cade4e832627b4f6", md5);
    }

    @Test
    void testReplaceSearchToken() {
        assertEquals("tokenA", properties.getUserAuthToken());
        service.replaceSearchToken();
        assertEquals("tokenB", properties.getUserAlternativeAuthToken());
        service.replaceSearchToken();
        assertEquals("tokenA", properties.getUserAuthToken());
    }

    @Test
    void testReplaceFileUrlToken() {
        assertEquals("tokenA", properties.getUserAuthToken());
        service.replaceFileUrlToken();
        assertEquals("tokenB", properties.getUserAlternativeAuthToken());
        service.replaceFileUrlToken();
        assertEquals("tokenA", properties.getUserAuthToken());
    }
}
