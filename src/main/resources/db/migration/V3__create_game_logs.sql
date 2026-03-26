CREATE TABLE game_logs (
    id                  BIGSERIAL PRIMARY KEY,
    player_id           BIGINT      REFERENCES players(id),
    team_id             BIGINT      REFERENCES teams(id),
    opponent_id         BIGINT      REFERENCES teams(id),
    game_date           DATE        NOT NULL,
    season              VARCHAR(10) NOT NULL,
    sport               VARCHAR(10) NOT NULL,
    is_home             BOOLEAN,
    points              INT,
    assists             INT,
    rebounds            INT,
    three_pointers_made INT,
    steals              INT,
    blocks              INT,
    turnovers           INT,
    minutes_played      NUMERIC(4,1),
    passing_yards       INT,
    passing_tds         INT,
    rushing_yards       INT,
    rushing_tds         INT,
    receiving_yards     INT,
    receptions          INT,
    receiving_tds       INT,
    hits                INT,
    home_runs           INT,
    rbi                 INT,
    strikeouts_pitcher  INT,
    earned_runs         INT,
    walks_allowed       INT,
    innings_pitched     NUMERIC(4,1),
    created_at          TIMESTAMP DEFAULT NOW(),
    UNIQUE(player_id, game_date)
);
CREATE INDEX idx_game_logs_player_id   ON game_logs(player_id);
CREATE INDEX idx_game_logs_opponent_id ON game_logs(opponent_id);
CREATE INDEX idx_game_logs_game_date   ON game_logs(game_date);
CREATE INDEX idx_game_logs_sport       ON game_logs(sport);
CREATE INDEX idx_game_logs_season      ON game_logs(season);
