package org.example.dto;

import org.example.model.Category;

import java.util.List;

public record UserResponse(String id, String email, String username, List<Category> categories) { }