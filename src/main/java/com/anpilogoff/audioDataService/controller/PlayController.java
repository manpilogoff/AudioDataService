package com.anpilogoff.audioDataService.controller;

import com.anpilogoff.audioDataService.util.JsonStreamingUtils;
import com.anpilogoff.audioDataService.service.QobuzApiService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/play")
@AllArgsConstructor
public class PlayController {
    private final QobuzApiService qobuzApiService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> play(
            @RequestParam (value = "track_id") int trackId,
            @RequestParam (value = "format_id", required = false) Integer formatId) {
        System.out.println(formatId);
        return qobuzApiService.getFileUrl(trackId, formatId).flatMap(resp -> {
            if (JsonStreamingUtils.hasTrackFullDuration(resp)) {
                return Mono.just(resp);
            }else {
                qobuzApiService.replaceFileUrlToken();
                return qobuzApiService.getFileUrl(trackId, formatId);
            }
        });
    }
}
