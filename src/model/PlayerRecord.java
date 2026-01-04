package model;

public class PlayerRecord {
    private final Player player;
    private int wins;
    private int matchesPlayed;

    private final MyArrayList<HistoryEntry> matchHistory;

    public PlayerRecord(Player player) {
        this.player = player;
        this.wins = 0;
        this.matchesPlayed = 0;
        this.matchHistory = new MyArrayList<>();
    }

    public Player getPlayer() { return player; }
    public String getName() { return player.getName(); }

    public int getWins() { return wins; }
    public int getMatchesPlayed() { return matchesPlayed; }

    public void recordMatch(MatchOutcome outcome, boolean isWinner) {
        matchesPlayed++;
        if (isWinner) wins++;
        matchHistory.add(new HistoryEntry(outcome, isWinner));
    }

    public MyArrayList<HistoryEntry> getMatchHistory() {
        return matchHistory;
    }

    public static class HistoryEntry {
        public final MatchOutcome outcome;
        public final boolean isWin;

        public HistoryEntry(MatchOutcome outcome, boolean isWin) {
            this.outcome = outcome;
            this.isWin = isWin;
        }
    }
}
