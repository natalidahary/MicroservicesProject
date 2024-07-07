package org.example.controller;

import org.example.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchNews(@RequestBody List<String> preferences) {
        String newsList = newsService.fetchNews(preferences);
        return ResponseEntity.ok(newsList);
    }
}