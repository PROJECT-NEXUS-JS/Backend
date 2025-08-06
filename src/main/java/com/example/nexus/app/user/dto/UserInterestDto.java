package com.example.nexus.app.user.dto;

import java.util.Set;

public class UserInterestDto {

    public record UserInterestRequest(
            Set<String> mainCategories,
            Set<String> platformCategories,
            Set<String> genreCategories
    ) {}

    public record UserInterestResponse(
            Set<String> mainCategories,
            Set<String> platformCategories,
            Set<String> genreCategories
    ) {}
}
