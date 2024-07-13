package org.example.dto;

import java.util.List;

public record UserRequest(String email, List<String> preferences ) {}