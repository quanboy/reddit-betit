CREATE TABLE teams (
    id           BIGSERIAL PRIMARY KEY,
    name         TEXT        NOT NULL,
    abbreviation VARCHAR(5)  NOT NULL,
    sport        VARCHAR(10) NOT NULL,
    created_at   TIMESTAMP   DEFAULT NOW()
);
CREATE INDEX idx_teams_sport ON teams(sport);
CREATE INDEX idx_teams_abbreviation ON teams(abbreviation);
