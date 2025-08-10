package com.anpilogoff.audioDataService.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonStreamingUtils {

    /**
     * Проверяет наличие непустого массива items в объекте tracks,
     * не загружая весь JSON в память.
     * @param jsonString JSON строка
     * @return true, если tracks.items — массив с элементами, false, если отсутствует или пустой
     */
    public static boolean checkTracksItemsNotEmpty(String jsonString) {
        try (JsonParser parser = new JsonFactory().createParser(jsonString)) {
            boolean inTracks = false;
            int tracksDepth = 0;

            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == null) break;

                if (token == JsonToken.FIELD_NAME && "tracks".equals(parser.getCurrentName())) {
                    token = parser.nextToken();
                    if (token == JsonToken.START_OBJECT) {
                        inTracks = true;
                        tracksDepth = 1;
                    }
                }
                else if (inTracks) {
                    if (token == JsonToken.START_OBJECT) {
                        tracksDepth++;
                    } else if (token == JsonToken.END_OBJECT) {
                        tracksDepth--;
                        if (tracksDepth == 0) {
                            inTracks = false;
                        }
                    } else if (token == JsonToken.FIELD_NAME && "items".equals(parser.getCurrentName())) {
                        token = parser.nextToken();
                        if (token == JsonToken.START_ARRAY) {
                            token = parser.nextToken();
                            return token != JsonToken.END_ARRAY;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return false;
    }

    /**
     * Возвращает true, если duration > 30, иначе false.
     * Парсинг останавливается сразу после нахождения значения.
     */
    public static boolean hasTrackFullDuration(String jsonString) {
        try (JsonParser parser = new JsonFactory().createParser(jsonString)) {
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == null) break;

                // Нашли поле "duration"
                if (token == JsonToken.FIELD_NAME && "duration".equals(parser.getCurrentName())) {
                    token = parser.nextToken(); // читаем значение duration
                    if (token.isNumeric()) {
                        int duration = parser.getIntValue();
                        // если duration > 30, сразу возвращаем
                        if (duration > 30) {
                            return true;
                        } else {
                            return false; // <= 30 — значит sample
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return false; // по умолчанию — нет подходящей длительности
    }

}
