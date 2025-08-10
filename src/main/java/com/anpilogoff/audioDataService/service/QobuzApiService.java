package com.anpilogoff.audioDataService.service;

import com.anpilogoff.audioDataService.config.SearchProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

@Service
@Slf4j
public class QobuzApiService {
    private final SearchProperties properties;
    private final WebClient webClient;
    @Setter
    private volatile String validSearchToken;
    @Setter
    private volatile String validFileUrlToken;

    /**
     * Конструктор сервиса Qobuz API.
     *
     * @param properties  конфигурация поиска и API ключей
     * @param webClient   настроенный экземпляр {@link WebClient} для запросов к Qobuz
     */
    public QobuzApiService(SearchProperties properties, WebClient webClient) {
        this.properties = properties;
        this.webClient = webClient;
        this.validSearchToken = properties.getUserAuthToken();
        this.validFileUrlToken = properties.getUserAuthToken();
    }

    /**
     * Выполняет поиск по каталогу Qobuz.
     *
     * @param query поисковый запрос (например, имя исполнителя или название трека)
     * @param type  тип поиска (tracks, albums, artists и пр.), если null или пусто — используется "tracks"
     * @return      Mono с JSON-строкой ответа от API Qobuz
     */
    public Mono<String> search(String query, String type) {
        String url = UriComponentsBuilder.fromUriString(properties.getQobuzBaseUrl() + "catalog/search")
                .queryParam("app_id", properties.getAppId())
                .queryParam("query", query)
                .queryParam("type", type !=null && !type.isEmpty() ? type : "tracks")
                .queryParam("user_auth_token", validSearchToken)
                .toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Получает URL для стриминга или загрузки файла трека.
     *
     * @param trackId  ID трека в каталоге Qobuz
     * @param formatId ID формата файла (например, 6 для FLAC 16-bit)
     * @return         Mono с JSON-строкой ответа от API Qobuz (ссылка и параметры файла)
     */
    public Mono<String> getFileUrl(int trackId, int formatId) {
        long timestamp = Instant.now().getEpochSecond();
        String signatureRaw = String.format("trackgetFileUrlformat_id%sintentstreamtrack_id%s%s%s",
                formatId, trackId, timestamp, properties.getAppSecret());

        String signature = calculateMD5(signatureRaw);

        String url = UriComponentsBuilder.fromUriString(properties.getQobuzBaseUrl() + "track/getFileUrl")
                .queryParam("app_id", properties.getAppId())
                .queryParam("track_id", trackId)
                .queryParam("format_id", formatId)
                .queryParam("intent", "stream")
                .queryParam("request_ts", timestamp)
                .queryParam("request_sig", signature)
                .queryParam("user_auth_token", validFileUrlToken)
                .toUriString();


        return webClient.get()
                .uri(url)
                .header("X-User-Auth-Token", validFileUrlToken) // добавляем заголовок
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Ошибка при вызове Qobuz getFileUrl", e)));
    }


    /**
     * Вычисляет MD5-хэш для заданной строки.
     *
     * @param input входная строка
     * @return      строка хэша в шестнадцатеричном виде
     * @throws RuntimeException если алгоритм MD5 недоступен в JVM
     */
    private String calculateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e){
            throw new RuntimeException("Error calculating MD5", e);

        }
    }

    /**
     * Переключает токен поиска между основным и дополнительным.
     * Используется при ошибках или ограничениях на текущий токен.
     */
    public void replaceSearchToken(){
        if(validSearchToken.equals(properties.getUserAuthToken())){
            this.validSearchToken = properties.getUserAlternativeAuthToken();
        }else {
            this.validSearchToken = properties.getUserAuthToken();
        }
        log.info("search token replaced");
    }

    /**
     * Переключает токен для получения файлов между основным и дополнительным.
     * Используется при ошибках получения URL.
     */
    public void replaceFileUrlToken(){
        if(validFileUrlToken.equals(properties.getUserAuthToken())){
            this.validFileUrlToken = properties.getUserAlternativeAuthToken();
        }else {
            this.validFileUrlToken = properties.getUserAuthToken();
        }
        log.info("fileUrl token replaced");
    }

}
