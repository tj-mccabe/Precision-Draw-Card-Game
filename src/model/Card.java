package model;

public class Card {

    // Rank: 2–10 = number cards
    // 11 = Jack, 12 = Queen, 13 = King, 14 = Ace
    private int rank;

    public Card(int rank) {
        if (rank < 2 || rank > 14) {
            throw new IllegalArgumentException("Card rank must be between 2 and 14");
        }
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    /**
     * Returns the base value of the card.
     * Number cards (2–10): face value
     * Face cards (11–13): 10
     * Ace (14): 11 (base value, optimisation handled elsewhere)
     */
    public int getBaseValue() {
        if (rank >= 2 && rank <= 10) {
            return rank;
        } else if (rank >= 11 && rank <= 13) {
            return 10;
        } else { // Ace
            return 11;
        }
    }

    @Override
    public String toString() {
        switch (rank) {
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            case 14: return "A";
            default: return String.valueOf(rank);
        }
    }
}
