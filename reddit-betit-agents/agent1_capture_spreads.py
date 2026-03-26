import os
import requests
from dotenv import load_dotenv
from db import get_connection

load_dotenv()

API_KEY  = os.environ["ODDS_API_KEY"]
BASE_URL = "https://api.the-odds-api.com/v4"
SPORT    = "basketball_nba"

def fetch_todays_spreads():
    url = f"{BASE_URL}/sports/{SPORT}/odds"
    params = {
        "apiKey":     API_KEY,
        "regions":    "us",
        "markets":    "spreads",
        "oddsFormat": "american",
        "dateFormat": "iso"
    }
    resp = requests.get(url, params=params)
    resp.raise_for_status()
    print(f"API requests remaining: {resp.headers.get('x-requests-remaining', '?')}")
    return resp.json()

def upsert_spread(conn, game):
    odds_api_id   = game["id"]
    home_team     = game["home_team"]
    away_team     = game["away_team"]
    commence_time = game["commence_time"]
    game_date     = commence_time[:10]

    home_spread = None
    preferred   = ["draftkings", "fanduel", "betmgm"]

    for key in preferred:
        for bm in game.get("bookmakers", []):
            if bm["key"] == key:
                for market in bm.get("markets", []):
                    if market["key"] == "spreads":
                        for outcome in market["outcomes"]:
                            if outcome["name"] == home_team:
                                home_spread = outcome["point"]
                                break

    if home_spread is None:
        for bm in game.get("bookmakers", []):
            for market in bm.get("markets", []):
                if market["key"] == "spreads":
                    for outcome in market["outcomes"]:
                        if outcome["name"] == home_team:
                            home_spread = outcome["point"]
                            break

    if home_spread is None:
        print(f"  No spread found for {home_team} vs {away_team} — skipping")
        return

    with conn.cursor() as cur:
        cur.execute("""
            INSERT INTO pending_spreads
                (odds_api_id, home_team, away_team, home_spread, game_date, commence_time)
            VALUES (%s, %s, %s, %s, %s, %s)
            ON CONFLICT (odds_api_id) DO UPDATE SET
                home_spread   = EXCLUDED.home_spread,
                commence_time = EXCLUDED.commence_time
        """, (odds_api_id, home_team, away_team, home_spread, game_date, commence_time))
    conn.commit()
    print(f"  Stored: {away_team} @ {home_team}  spread: {home_spread}")

def run():
    print("=== Agent 1: Capturing today's spreads ===")
    games = fetch_todays_spreads()
    if not games:
        print("No games found for today.")
        return
    conn = get_connection()
    try:
        for game in games:
            upsert_spread(conn, game)
        print(f"\nDone. Stored spreads for {len(games)} game(s).")
    finally:
        conn.close()

if __name__ == "__main__":
    run()
