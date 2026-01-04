package model;

public class MatchOutcome {
    public final Player player1;
    public final Player player2;

    public final int[] targetBeforeRound;   // length 4
    public final int[] targetAfterRound;    // length 4
    public final boolean[] p1StartsRound;   // length 4

    public final int[] p1Totals;            // optimised totals
    public final int[] p2Totals;
    public final int[] p1Scores;
    public final int[] p2Scores;

    public final int p1MatchScore;
    public final int p2MatchScore;

    public final Player winner;

    public MatchOutcome(Player p1, Player p2,
                        int[] targetBefore, int[] targetAfter,
                        boolean[] p1StartsRound,
                        int[] p1Totals, int[] p2Totals,
                        int[] p1Scores, int[] p2Scores,
                        int p1MatchScore, int p2MatchScore,
                        Player winner) {
        this.player1 = p1;
        this.player2 = p2;
        this.targetBeforeRound = targetBefore;
        this.targetAfterRound = targetAfter;
        this.p1StartsRound = p1StartsRound;
        this.p1Totals = p1Totals;
        this.p2Totals = p2Totals;
        this.p1Scores = p1Scores;
        this.p2Scores = p2Scores;
        this.p1MatchScore = p1MatchScore;
        this.p2MatchScore = p2MatchScore;
        this.winner = winner;
    }
}
