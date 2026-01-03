package model;

public class HandScorer {

    public static int calculateScore(Card[] hand, int cardCount, int target) {
        int total = 0;
        int aceCount = 0;

        // Sum base values
        for (int i = 0; i < cardCount; i++) {
            total += hand[i].getBaseValue();
            if (hand[i].getRank() == 14) {
                aceCount++;
            }
        }

        int optimisedTotal = total;
        while (optimisedTotal > target && aceCount > 0) {
            optimisedTotal -= 10;
            aceCount--;
        }

        if (total == target) {
            return -5;
        } else if (total < target) {
            return target - total;
        } else {
            return 2 * (total - target);
        }
    }
}
