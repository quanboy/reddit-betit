package com.redditbetit.backend.controller;

import com.redditbetit.backend.dto.RawQueryRequest;
import com.redditbetit.backend.dto.TrendResultDTO;
import com.redditbetit.backend.service.AnthropicService;
import com.redditbetit.backend.service.TrendService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trends")
@CrossOrigin(origins = "*")
public class TrendController {

    private final AnthropicService anthropicService;
    private final TrendService trendService;

    public TrendController(AnthropicService anthropicService, TrendService trendService) {
        this.anthropicService = anthropicService;
        this.trendService = trendService;
    }

    @PostMapping("/query")
    public ResponseEntity<TrendResultDTO> query(@Valid @RequestBody RawQueryRequest request) {
        var params = anthropicService.parseQuery(request.query());
        var result = trendService.compute(params);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }
}
