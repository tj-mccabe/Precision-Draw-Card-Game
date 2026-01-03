package model;

import java.util.Random;

public class Deck {

    private Card[] cards;
    private int nextIndex; // next card to deal (0..51)

    public Deck() {
        buildDeck();
        nextIndex = 0;
    }

    // Builds a standard 52-card deck (suits ignored; ranks only)
    private void buildDeck() {
        cards = new Card[52];
        int index = 0;

        // For each rank 2..14, create 4 copies (one per suit conceptually)
        for (int rank = 2; rank <= 14; rank++) {
            for (int i = 0; i < 4; i++) {
                cards[index] = new Card(rank);
                index++;
            }
        }
    }

    public int size() {
        return cards.length;
    }

    public int remainingCards() {
        return cards.length - nextIndex;
    }

    /**
     * Fisher–Yates shuffle (in-place).
     * O(n) time, O(1) extra space.
     */
    public void shuffle(Random rng) {
        for (int i = cards.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1); // 0..i
            Card temp = cards[i];
            cards[i] = cards[j];
            cards[j] = temp;
        }
        nextIndex = 0; // after shuffling, start dealing from the top
    }

    /**
     * Deals one card from the deck.
     */
    public Card deal() {
        if (nextIndex >= cards.length) {
            throw new IllegalStateException("No cards remaining in the deck");
        }
        Card dealt = cards[nextIndex];
        nextIndex++;
        return dealt;
    }

    /**
     * Rebuilds a fresh 52-card deck and shuffles it.
     * Used at the start of each round.
     */
    public void resetAndShuffle(Random rng) {
        buildDeck();
        shuffle(rng);
    }

    // Helper for tests/debugging: read card rank at a position (not for gameplay)
    public int peekRankAt(int index) {
        return cards[index].getRank();
    }
}
