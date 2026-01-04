package model;

public class Card {
    private final int rank; // 2-14
    private final int suit; // 0-3

    // Used by Deck
    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    // Used by tests
    public Card(int rank) {
        this(rank, 0);
    }

    public int getRank() {
        return rank;
    }

    public int getBaseValue() {
        if (rank >= 2 && rank <= 10) return rank;
        if (rank >= 11 && rank <= 13) return 10;
        return 11; // Ace base
    }

    private String rankToString() {
        if (rank >= 2 && rank <= 10) return String.valueOf(rank);
        if (rank == 11) return "J";
        if (rank == 12) return "Q";
        if (rank == 13) return "K";
        return "A";
    }

    private String suitToSymbol() {
        switch (suit) {
            case 0: return "♠";
            case 1: return "♥";
            case 2: return "♦";
            default: return "♣";
        }
    }

    @Override
    public String toString() {
        return rankToString() + suitToSymbol();
    }
}
