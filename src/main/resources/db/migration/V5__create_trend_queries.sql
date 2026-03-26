CREATE TABLE trend_queries (
    id             BIGSERIAL PRIMARY KEY,
    raw_query      TEXT        NOT NULL,
    parsed_params  JSONB,
    result_summary JSONB,
    sport          VARCHAR(10),
    query_type     VARCHAR(30),
    created_at     TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_trend_queries_sport      ON trend_queries(sport);
CREATE INDEX idx_trend_queries_query_type ON trend_queries(query_type);
CREATE INDEX idx_trend_queries_created_at ON trend_queries(created_at);
