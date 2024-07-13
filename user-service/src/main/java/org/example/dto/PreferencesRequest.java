package org.example.dto;

import org.example.model.Category;

import java.util.List;

public record PreferencesRequest(String userId, List<Category> categories) { }
