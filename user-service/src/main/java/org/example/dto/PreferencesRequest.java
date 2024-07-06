package org.example.dto;

import java.util.List;

public record PreferencesRequest(String userId, List<String> preferences) { }
