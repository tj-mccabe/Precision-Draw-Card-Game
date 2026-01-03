package model;

public class HandScorer {

    public static int calculateScore(Card[] hand, int cardCount, int target) {
        int total = 0;
        int aceCount = 0;

        // Sum base values and count aces
        for (int i = 0; i < cardCount; i++) {
            total += hand[i].getBaseValue();
            if (hand[i].getRank() == 14) {
                aceCount++;
            }
        }

        // Each ace reduced from 11 → 1 subtracts 10
        while (total > target && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        // Score calculated using optimised total
        if (total == target) {
            return -5;
        } else if (total < target) {
            return target - total;
        } else {
            return 2 * (total - target);
        }
    }
}
