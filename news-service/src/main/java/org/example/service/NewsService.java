package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.GeminiResponse;
import org.example.dto.NewsDataResponse;
import org.example.model.News;
import org.example.dto.NewsRequest;
import org.example.repository.NewsRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void fetchNews(NewsRequest newsRequest) {
        log.info("Fetching news for preferences: {}", newsRequest.preferences());

        for (String category : newsRequest.preferences()) {
            String url = "https://newsdata.io/api/1/news?apikey=YOUR_API_KEY&category=" + category;
            NewsDataResponse response = restTemplate.getForObject(url, NewsDataResponse.class);
            if (response != null && response.results() != null) {
                List<News> newsList = Arrays.stream(response.results())
                        .map(result -> News.builder()
                                .title(result.title())
                                .description(result.description())
                                .content(result.content())
                                .url(result.url())
                                .source(result.source())
                                .publishedAt(LocalDateTime.parse(result.publishedAt()))
                                .category(category)
                                .build())
                        .collect(Collectors.toList());
                newsRepository.saveAll(newsList);
                summarizeNews(newsList);
            }
        }
    }

    @Async
    public void summarizeNews(List<News> newsList) {
        log.info("Summarizing news using AI");
        for (News news : newsList) {
            try {
                String summary = generateSummary(news.getContent());
                news.setDescription(summary);
                newsRepository.save(news);
            } catch (Exception e) {
                log.error("Error summarizing news with ID: {}", news.getId(), e);
            }
        }
    }

    private String generateSummary(String content) {
        // Implement the API call to Gemini AI API to summarize the content
        // Example API call (replace with actual implementation):
        String url = "https://api.gemini.ai/summarize";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer YOUR_GEMINI_API_KEY");

        String requestBody = "{\"content\":\"" + content + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(url, entity, GeminiResponse.class);
        return response.getBody().summary();
    }

}