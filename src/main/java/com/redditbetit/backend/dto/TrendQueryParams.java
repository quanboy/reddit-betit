package com.redditbetit.backend.dto;

import com.redditbetit.backend.model.TrendQueryType;

public record TrendQueryParams(
        TrendQueryType queryType,
        String sport,
        String playerName,
        String teamName,
        String opponentName,
        String stat,
        Double propLine,
        Integer sampleSize,
        String season
) {}
