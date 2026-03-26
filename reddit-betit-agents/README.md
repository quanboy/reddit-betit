# reddit-betit-agents

Two daily ingestion agents that populate the `ats_results` table.

## Setup

1. Copy `.env.example` to `.env` and fill in values:
   - DATABASE_URL — Railway PostgreSQL public URL (postgresql:// format, NOT jdbc://)
   - ODDS_API_KEY — free key from https://the-odds-api.com

2. Install dependencies:
   pip install -r requirements.txt

## Daily schedule (via GitHub Actions)
- Agent 1 runs at 10am ET — captures today's spreads before tip-off
- Agent 2 runs at 3am ET — calculates ATS results after all games finish

## How it works
Agent 1 stores today's spread for every NBA game before it starts.
Agent 2 fetches final scores, joins with stored spreads, calculates
cover/no-cover, and writes both home and away perspectives to ats_results.

## API usage (free tier: 500 req/month)
~60 requests/month total — well within free tier.
