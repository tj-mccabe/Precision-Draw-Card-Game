package model;

public interface MatchObserver {
    void onRoundStart(int roundNumber, int target, Player firstPlayer);
    void onPlayerTurn(Player player, int guess, Card[] hand, int handCount, int optimisedTotal, int score, int target);
    void onRoundEnd(int roundNumber, int targetBefore, int targetAfter, int p1RoundScore, int p2RoundScore,
                    int p1MatchScoreSoFar, int p2MatchScoreSoFar);
}
