package org.example.dto;

import org.example.model.Category;

import java.util.List;

public record UserRequest(String email, String username, String password, List<Category> categories) { }