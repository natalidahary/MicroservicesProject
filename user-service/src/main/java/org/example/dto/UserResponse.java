package org.example.dto;

import java.util.List;

public record UserResponse(String id, String email, List<String> preferences ) {}

