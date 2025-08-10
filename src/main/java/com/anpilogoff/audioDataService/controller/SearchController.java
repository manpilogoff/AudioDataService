package com.anpilogoff.audioDataService.controller;

import com.anpilogoff.audioDataService.util.JsonStreamingUtils;
import com.anpilogoff.audioDataService.service.QobuzApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final QobuzApiService qobuzApiService;

    public SearchController(QobuzApiService service) {
        this.qobuzApiService = service;
    }

    @GetMapping
    public Mono<String> search(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "type", required = false) String type) {

        return qobuzApiService.search(query, type)
                .flatMap(resp -> {
                    boolean notEmpty = JsonStreamingUtils.checkTracksItemsNotEmpty(resp);

                    if (notEmpty) {
                        return Mono.just(resp);
                    } else {
                       qobuzApiService.replaceSearchToken();
                       return qobuzApiService.search(query, type);
                    }
                });
    }
}
