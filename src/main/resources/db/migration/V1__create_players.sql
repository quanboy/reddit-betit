CREATE TABLE players (
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT        NOT NULL,
    sport       VARCHAR(10) NOT NULL,
    external_id TEXT        UNIQUE,
    created_at  TIMESTAMP   DEFAULT NOW()
);
CREATE INDEX idx_players_sport ON players(sport);
CREATE INDEX idx_players_external_id ON players(external_id);
