package com.redditbetit.backend.dto;

import com.redditbetit.backend.model.TrendQueryType;
import java.util.List;

public record TrendResultDTO(
        TrendQueryType queryType,
        String displayLabel,
        int hits,
        int sampleSize,
        double hitRate,
        Double avgStat,
        List<String> gameResults,
        String insight,
        String sport
) {}
