package org.example.dto;

public record NotificationRequest(String email, String title, String content, String link) { }