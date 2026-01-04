package model;

public interface GuessProvider {
    int getGuess(Player player, int roundNumber, int target);
}
