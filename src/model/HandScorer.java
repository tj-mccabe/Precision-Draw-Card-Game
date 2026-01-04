package model;

public class HandScorer {

    public static int calculateScore(Card[] hand, int cardCount, int target) {
        int total = calculateOptimisedTotal(hand, cardCount, target);

        if (total == target) return -5;
        if (total < target) return target - total;
        return 2 * (total - target);
    }

    // used by MatchEngine for under/over and display
    public static int calculateOptimisedTotal(Card[] hand, int cardCount, int target) {
        int total = 0;
        int aceCount = 0;

        for (int i = 0; i < cardCount; i++) {
            total += hand[i].getBaseValue();
            if (hand[i].getRank() == 14) aceCount++;
        }

        while (total > target && aceCount > 0) {
            total -= 10; // Ace 11 -> 1
            aceCount--;
        }

        return total;
    }
}
