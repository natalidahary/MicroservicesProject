package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.NewsRequest;
import org.example.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchNews(@RequestBody NewsRequest newsRequest) {
        newsService.fetchNews(newsRequest);
        return new ResponseEntity<>("Request accepted", HttpStatus.ACCEPTED);
    }
}
