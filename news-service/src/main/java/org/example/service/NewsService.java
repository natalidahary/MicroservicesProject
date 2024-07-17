package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.client.DaprClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NewsDataResponse;
import org.example.dto.NotificationRequest;
import org.example.model.News;
import org.example.dto.NewsRequest;
import org.example.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;
    private final DaprClient daprClient;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${newsdata.api.key}")
    private String newsDataApiKey;

    @Value("${edenai.api.key}")
    private String edenaiApiKey;



    public void processPreferences(NewsRequest newsRequest) throws UnsupportedEncodingException {
        try {
            fetchNews(newsRequest);
        } catch (Exception e) {
            log.error("Error processing preferences: {}", newsRequest, e);
            throw e; // or handle it as needed
        }
    }

    @Async
    public void fetchNews(NewsRequest newsRequest) throws UnsupportedEncodingException {
        log.info("Fetching news for preferences: {}", newsRequest.preferences());
        for (String preference : newsRequest.preferences()) {
            String url = "https://newsdata.io/api/1/latest?apikey=" + newsDataApiKey + "&q=" + URLEncoder.encode(preference, "UTF-8") + "&language=en";
            try {
                NewsDataResponse response = restTemplate.getForObject(url, NewsDataResponse.class);
                if (response != null && response.results() != null) {
                    List<News> newsList = Arrays.stream(response.results())
                            .map(result -> News.builder()
                                    .link(result.link())
                                    .title(result.title())
                                    .content(result.content())
                                    .build())
                            .collect(Collectors.toList());
                    summarizeNews(newsList, newsRequest.email());
                    newsRepository.saveAll(newsList);
                }
            } catch (HttpClientErrorException e) {
                log.error("Error fetching news for preference: {} - {}", preference, e.getMessage());
            }
        }
    }
    @Async
    public void summarizeNews(List<News> newsList, String email) {
        log.info("Summarizing news using AI");
        for (News news : newsList) {
            try {
                String summary = generateSummary(news.getLink());
                if (summary != null) {
                    log.info("Generated summary for news: {}", summary);
                    news.setContent(summary);
                    newsRepository.save(news);
                    // Notify the user
                    sendNotification(new NotificationRequest(email, news.getTitle(), summary, news.getLink()));
                } else {
                    log.error("Failed to generate summary");
                }
            } catch (Exception e) {
                log.error("Error summarizing news", e);
            }
        }
    }

    private String generateSummary(String link) throws Exception {
        String url = "https://api.edenai.run/v2/text/summarize";

        String requestBody = objectMapper.createObjectNode()
                .put("providers", "openai")
                .put("text", link)
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("authorization", "Bearer " + edenaiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        log.info("Sending request to EdenAI API with payload: {}", requestBody);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Received response from EdenAI API: {} - {}", response.statusCode(), response.body());

        return parseSummary(response.body());
    }

    private String parseSummary(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode resultNode = rootNode.path("openai").path("result");
            if (resultNode.isMissingNode()) {
                log.error("Error parsing summary response: 'openai.result' node is missing");
                return null;
            }
            return resultNode.asText();
        } catch (Exception e) {
            log.error("Error parsing summary response", e);
            return null;
        }
    }

    public void sendNotification(NotificationRequest notificationRequest) {
        String notificationJson;
        try {
            notificationJson = objectMapper.writeValueAsString(notificationRequest);
            invokeOtherService("notification-service", "notifications/sendNotification", notificationJson);
        } catch (JsonProcessingException e) {
            log.error("Error converting notification request to JSON", e);
        }
    }

    public void invokeOtherService(String serviceId, String methodName, String requestBody) {
        log.info("Invoking service {} with method {}", serviceId, methodName);
        try {
            String daprUrl = String.format("http://localhost:3501/v1.0/invoke/%s/method/%s", serviceId, methodName);
            log.info("Dapr URL: {}", daprUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(daprUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Service {} invoked successfully with method {}", serviceId, methodName);
            log.info("Response: {}", response.body());
        } catch (Exception e) {
            log.error("Error invoking service {} with method {}", serviceId, methodName, e);
        }
    }

}
