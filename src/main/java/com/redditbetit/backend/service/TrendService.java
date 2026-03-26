package com.redditbetit.backend.service;

import com.redditbetit.backend.dto.TrendQueryParams;
import com.redditbetit.backend.dto.TrendResultDTO;
import org.springframework.stereotype.Service;

@Service
public class TrendService {

    private final AnthropicService anthropicService;

    public TrendService(AnthropicService anthropicService) {
        this.anthropicService = anthropicService;
    }

    public TrendResultDTO compute(TrendQueryParams params) {
        return switch (params.queryType()) {
            case PROP_TREND    -> computePropTrend(params);
            case STAT_TREND    -> computeStatTrend(params);
            case ATS_MATCHUP   -> computeAtsMatchup(params);
            case ATS_SITUATION -> computeAtsSituation(params);
        };
    }

    private TrendResultDTO computePropTrend(TrendQueryParams params) {
        // TODO: query game_logs by player + opponent + last N games
        // compare each game stat vs params.propLine()
        // build gameResults list ("O" / "U" / "P")
        throw new UnsupportedOperationException("PROP_TREND not yet implemented");
    }

    private TrendResultDTO computeStatTrend(TrendQueryParams params) {
        // TODO: query game_logs by player + last N games, return raw stat averages
        throw new UnsupportedOperationException("STAT_TREND not yet implemented");
    }

    private TrendResultDTO computeAtsMatchup(TrendQueryParams params) {
        // TODO: query ats_results by team + opponent + last N games
        throw new UnsupportedOperationException("ATS_MATCHUP not yet implemented");
    }

    private TrendResultDTO computeAtsSituation(TrendQueryParams params) {
        // TODO: query ats_results filtered by situation_tags
        throw new UnsupportedOperationException("ATS_SITUATION not yet implemented");
    }
}
