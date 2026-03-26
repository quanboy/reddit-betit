package com.redditbetit.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RawQueryRequest(
        @NotBlank String query
) {}
