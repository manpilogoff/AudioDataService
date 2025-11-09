package com.anpilogoff.audioDataService.controller;

import com.anpilogoff.audioDataService.service.QobuzApiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/qobuz",produces = MediaType.APPLICATION_JSON_VALUE)
public class QobuzArtistController {

    private final QobuzApiService qobuzApiService;

    public QobuzArtistController(QobuzApiService qobuzApiService) {
        this.qobuzApiService = qobuzApiService;
    }

    // Получение артиста с альбомами по artist_id
    @GetMapping("/artist/{artistId}")
    public Mono<String> getArtistById(@PathVariable String artistId) {
        return qobuzApiService.getArtistWithAlbums(artistId);
    }}




