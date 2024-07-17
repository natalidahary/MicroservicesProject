package org.example.dto;

public record NewsDataResponse(String status, int totalResults, NewsResult[] results) {
    public static record NewsResult(String title, String content, String link) {}
}