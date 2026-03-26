CREATE TABLE ats_results (
    id              BIGSERIAL PRIMARY KEY,
    team_id         BIGINT      REFERENCES teams(id),
    opponent_id     BIGINT      REFERENCES teams(id),
    game_date       DATE        NOT NULL,
    season          VARCHAR(10) NOT NULL,
    sport           VARCHAR(10) NOT NULL,
    team_score      INT,
    opponent_score  INT,
    spread          NUMERIC(5,1),
    covered         BOOLEAN,
    is_home         BOOLEAN,
    situation_tags  TEXT[],
    created_at      TIMESTAMP DEFAULT NOW(),
    UNIQUE(team_id, game_date)
);
CREATE INDEX idx_ats_team_id     ON ats_results(team_id);
CREATE INDEX idx_ats_opponent_id ON ats_results(opponent_id);
CREATE INDEX idx_ats_game_date   ON ats_results(game_date);
CREATE INDEX idx_ats_sport       ON ats_results(sport);
