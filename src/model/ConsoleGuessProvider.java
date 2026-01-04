package model;

import java.util.Scanner;

public class ConsoleGuessProvider implements GuessProvider {

    private final Scanner sc;

    public ConsoleGuessProvider(Scanner sc) {
        this.sc = sc;
    }

    @Override
    public int getGuess(Player player, int roundNumber, int target) {
        while (true) {
            System.out.print(player.getName() + ", please enter your guess: ");
            String line = sc.nextLine().trim();

            try {
                int guess = Integer.parseInt(line);
                if (guess < 1 || guess > 52) {
                    System.out.println("Please enter a number between 1 and 52.");
                    continue;
                }
                return guess;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }
}
