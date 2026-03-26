CREATE TABLE pending_spreads (
    id              BIGSERIAL PRIMARY KEY,
    odds_api_id     TEXT        NOT NULL UNIQUE,
    home_team       TEXT        NOT NULL,
    away_team       TEXT        NOT NULL,
    home_spread     NUMERIC(5,1) NOT NULL,
    game_date       DATE        NOT NULL,
    commence_time   TIMESTAMP   NOT NULL,
    processed       BOOLEAN     DEFAULT FALSE,
    created_at      TIMESTAMP   DEFAULT NOW()
);

CREATE INDEX idx_pending_spreads_game_date   ON pending_spreads(game_date);
CREATE INDEX idx_pending_spreads_processed   ON pending_spreads(processed);
CREATE INDEX idx_pending_spreads_odds_api_id ON pending_spreads(odds_api_id);
