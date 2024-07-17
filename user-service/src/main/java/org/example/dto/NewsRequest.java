package org.example.dto;

import java.util.List;

public record NewsRequest(List<String> preferences, String userId, String email) {
}
