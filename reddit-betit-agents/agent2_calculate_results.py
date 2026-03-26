import os
import requests
from dotenv import load_dotenv
from db import get_connection

load_dotenv()

API_KEY  = os.environ["ODDS_API_KEY"]
BASE_URL = "https://api.the-odds-api.com/v4"
SPORT    = "basketball_nba"

def fetch_completed_scores():
    url = f"{BASE_URL}/sports/{SPORT}/scores"
    params = {
        "apiKey":     API_KEY,
        "daysFrom":   1,
        "dateFormat": "iso"
    }
    resp = requests.get(url, params=params)
    resp.raise_for_status()
    print(f"API requests remaining: {resp.headers.get('x-requests-remaining', '?')}")
    return [g for g in resp.json() if g.get("completed")]

def resolve_team_id(conn, team_name):
    with conn.cursor() as cur:
        cur.execute("""
            SELECT id FROM teams
            WHERE sport = 'NBA'
            AND name ILIKE %s
            LIMIT 1
        """, (f"%{team_name.split()[-1]}%",))
        row = cur.fetchone()
        return row[0] if row else None

def calculate_covered(home_score, away_score, home_spread):
    margin = home_score + home_spread - away_score
    if margin > 0:
        return True
    elif margin < 0:
        return False
    else:
        return None

def process_game(conn, game):
    odds_api_id = game["id"]
    home_team   = game["home_team"]
    away_team   = game["away_team"]
    scores      = game.get("scores") or []

    if len(scores) < 2:
        print(f"  No scores for {home_team} vs {away_team} — skipping")
        return

    score_map  = {s["name"]: int(s["score"]) for s in scores}
    home_score = score_map.get(home_team)
    away_score = score_map.get(away_team)

    if home_score is None or away_score is None:
        print(f"  Could not parse scores for {home_team} vs {away_team} — skipping")
        return

    with conn.cursor() as cur:
        cur.execute("""
            SELECT id, home_spread, game_date
            FROM pending_spreads
            WHERE odds_api_id = %s AND processed = FALSE
        """, (odds_api_id,))
        row = cur.fetchone()

    if not row:
        print(f"  No pending spread for {home_team} vs {away_team} — skipping")
        return

    pending_id, home_spread, game_date = row
    covered     = calculate_covered(home_score, away_score, float(home_spread))
    covered_str = "COVERED" if covered else ("PUSH" if covered is None else "NO COVER")

    home_team_id = resolve_team_id(conn, home_team)
    away_team_id = resolve_team_id(conn, away_team)

    with conn.cursor() as cur:
        cur.execute("""
            INSERT INTO ats_results
                (team_id, opponent_id, game_date, season, sport,
                 team_score, opponent_score, spread, covered, is_home)
            VALUES (%s, %s, %s, %s, 'NBA', %s, %s, %s, %s, TRUE)
            ON CONFLICT (team_id, game_date) DO UPDATE SET
                team_score     = EXCLUDED.team_score,
                opponent_score = EXCLUDED.opponent_score,
                spread         = EXCLUDED.spread,
                covered        = EXCLUDED.covered
        """, (home_team_id, away_team_id, game_date, "2024-25",
              home_score, away_score, home_spread, covered))

        if away_team_id and home_team_id:
            away_covered = None if covered is None else (not covered)
            cur.execute("""
                INSERT INTO ats_results
                    (team_id, opponent_id, game_date, season, sport,
                     team_score, opponent_score, spread, covered, is_home)
                VALUES (%s, %s, %s, %s, 'NBA', %s, %s, %s, %s, FALSE)
                ON CONFLICT (team_id, game_date) DO UPDATE SET
                    team_score     = EXCLUDED.team_score,
                    opponent_score = EXCLUDED.opponent_score,
                    spread         = EXCLUDED.spread,
                    covered        = EXCLUDED.covered
            """, (away_team_id, home_team_id, game_date, "2024-25",
                  away_score, home_score, -float(home_spread), away_covered))

        cur.execute("UPDATE pending_spreads SET processed = TRUE WHERE id = %s", (pending_id,))

    conn.commit()
    print(f"  {away_team} @ {home_team}: {away_score}-{home_score}  spread: {home_spread}  → {covered_str}")

def run():
    print("=== Agent 2: Calculating ATS results ===")
    games = fetch_completed_scores()
    if not games:
        print("No completed games found.")
        return
    conn = get_connection()
    try:
        for game in games:
            process_game(conn, game)
        print(f"\nDone. Processed {len(games)} completed game(s).")
    finally:
        conn.close()

if __name__ == "__main__":
    run()
