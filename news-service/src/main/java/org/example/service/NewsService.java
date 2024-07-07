package org.example.service;

import org.example.dto.NewsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NewsService {

    private static final String NEWS_API_URL = "https://newsapi.org/v2/top-headlines";
    private static final String API_KEY = "your_newsapi_key";

    public String fetchNews(List<String> preferences) {
        RestTemplate restTemplate = new RestTemplate();
        String url = NEWS_API_URL + "?category=" + String.join(",", preferences) + "&apiKey=" + API_KEY;
        NewsResponse response = restTemplate.getForObject(url, NewsResponse.class);
        return response != null ? response.summary() : null;
    }
}
