package model;

import java.util.Random;

public class SimulationGuessProvider implements GuessProvider {

    private final Random rng;

    private int currentRound = -1;
    private int callsThisRound = 0;
    private int firstGuess = 5;

    public SimulationGuessProvider(Random rng) {
        this.rng = rng;
    }

    @Override
    public int getGuess(Player player, int roundNumber, int target) {
        if (roundNumber != currentRound) {
            currentRound = roundNumber;
            callsThisRound = 0;
        }

        callsThisRound++;

        if (callsThisRound == 1) {
            firstGuess = 3 + rng.nextInt(5); // 3..7
            return firstGuess;
        } else {
            int g = firstGuess + 2;
            if (g > 52) g = 52;
            return g;
        }
    }
}
