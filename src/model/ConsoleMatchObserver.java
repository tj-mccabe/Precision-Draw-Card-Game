package model;

public class ConsoleMatchObserver implements MatchObserver {

    @Override
    public void onRoundStart(int roundNumber, int target, Player firstPlayer) {
        System.out.println();
        System.out.println("--- Round " + roundNumber + " ---");
        System.out.println("The current target is: " + target);
        System.out.println("The player to go first is: " + firstPlayer.getName());
        System.out.println();
    }

    @Override
    public void onPlayerTurn(Player player, int guess, Card[] hand, int handCount,
                             int optimisedTotal, int score, int target) {

        System.out.print(player.getName() + "'s cards: ");
        for (int i = 0; i < handCount; i++) {
            System.out.print(hand[i].toString() + " ");
        }
        System.out.println();

        if (optimisedTotal <= target) {
            System.out.println("Under with a round total of " + optimisedTotal + " (score: " + score + ")");
        } else {
            System.out.println("Over with a round total of " + optimisedTotal + " (score: " + score + ")");
        }
        System.out.println();
    }

    @Override
    public void onRoundEnd(int roundNumber, int targetBefore, int targetAfter,
                           int p1RoundScore, int p2RoundScore,
                           int p1MatchScoreSoFar, int p2MatchScoreSoFar) {

        System.out.println("Match score so far:");
        System.out.println("  Player 1 total: " + p1MatchScoreSoFar);
        System.out.println("  Player 2 total: " + p2MatchScoreSoFar);
        System.out.println();

        if (targetAfter > targetBefore) {
            System.out.println("Both players undershot in round " + roundNumber + " - adjusting target");
        } else if (targetAfter < targetBefore) {
            System.out.println("Both players overshot in round " + roundNumber + " - adjusting target");
        } else {
            System.out.println("Mixed result in round " + roundNumber + " - target unchanged");
        }
    }
}
