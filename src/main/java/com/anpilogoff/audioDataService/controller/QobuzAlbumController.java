package com.anpilogoff.audioDataService.controller;

import com.anpilogoff.audioDataService.service.QobuzApiService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/qobuz",produces = MediaType.APPLICATION_JSON_VALUE)
public class QobuzAlbumController {

    private final QobuzApiService qobuzApiService;

    public QobuzAlbumController(QobuzApiService qobuzApiService) {
        this.qobuzApiService = qobuzApiService;
    }

    @GetMapping("/album/{albumId}")
    public Mono<String> getAlbum(@PathVariable String albumId) {
        return qobuzApiService.getAlbumById(albumId);
    }}

