package org.example.dto;

public record NewsDataResponse (NewsResult[] results){
    public record NewsResult(String title, String description,String content, String url, String source, String publishedAt ) {
    }
}