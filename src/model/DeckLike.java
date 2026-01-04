package model;

import java.util.Random;

public interface DeckLike {
    void resetAndShuffle(Random rng);
    Card deal();
}
