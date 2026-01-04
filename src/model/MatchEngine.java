package model;

import java.util.Random;

public class MatchEngine {

    private static final int ROUNDS = 4;
    private static final int DECK_SIZE = 52;

    public MatchOutcome playMatch(Player p1,
                                  Player p2,
                                  DeckLike deck,
                                  GuessProvider guessProvider,
                                  Random rng,
                                  MatchObserver observer) {

        int target = 40;

        int[] targetBefore = new int[ROUNDS];
        int[] targetAfter = new int[ROUNDS];
        boolean[] p1StartsRound = new boolean[ROUNDS];

        int[] p1Totals = new int[ROUNDS];
        int[] p2Totals = new int[ROUNDS];
        int[] p1Scores = new int[ROUNDS];
        int[] p2Scores = new int[ROUNDS];

        int[] p1Guesses = new int[ROUNDS];
        int[] p2Guesses = new int[ROUNDS];

        boolean p1StartsRound1 = rng.nextBoolean();

        int p1MatchScore = 0;
        int p2MatchScore = 0;

        for (int round = 1; round <= ROUNDS; round++) {

            boolean p1StartsThisRound = (round % 2 == 1) ? p1StartsRound1 : !p1StartsRound1;
            p1StartsRound[round - 1] = p1StartsThisRound;

            while (true) {
                deck.resetAndShuffle(rng);
                targetBefore[round - 1] = target;

                Player first = p1StartsThisRound ? p1 : p2;
                if (observer != null) observer.onRoundStart(round, target, first);

                if (p1StartsThisRound) {
                    int g1 = guessProvider.getGuess(p1, round, target);
                    if (!isValidSingleGuess(g1)) {
                        if (observer != null) observer.onRoundRestart(round,
                                "Invalid guess: not enough cards in the deck.");
                        continue;
                    }

                    TurnResult r1 = playTurnWithGuess(p1, g1, target, deck, observer);

                    if (observer != null) {
                        observer.onSecondPlayerHint(p2, p1, target, r1.guess, r1.total, r1.score);
                    }

                    int g2 = guessProvider.getGuess(p2, round, target);
                    if (!isValidSingleGuess(g2)) {
                        if (observer != null) observer.onRoundRestart(round,
                                "Invalid guess: not enough cards in the deck.");
                        continue;
                    }

                    if (g1 + g2 > DECK_SIZE) {
                        if (observer != null) observer.onRoundRestart(round,
                                "Invalid guesses: total cards requested exceeds 52.");
                        continue;
                    }

                    TurnResult r2 = playTurnWithGuess(p2, g2, target, deck, observer);

                    p1Totals[round - 1] = r1.total;
                    p1Scores[round - 1] = r1.score;
                    p1Guesses[round - 1] = r1.guess;

                    p2Totals[round - 1] = r2.total;
                    p2Scores[round - 1] = r2.score;
                    p2Guesses[round - 1] = r2.guess;

                } else {
                    int g2 = guessProvider.getGuess(p2, round, target);
                    if (!isValidSingleGuess(g2)) {
                        if (observer != null) observer.onRoundRestart(round,
                                "Invalid guess: not enough cards in the deck.");
                        continue;
                    }

                    TurnResult r2 = playTurnWithGuess(p2, g2, target, deck, observer);

                    if (observer != null) {
                        observer.onSecondPlayerHint(p1, p2, target, r2.guess, r2.total, r2.score);
                    }

                    int g1 = guessProvider.getGuess(p1, round, target);
                    if (!isValidSingleGuess(g1)) {
                        if (observer != null) observer.onRoundRestart(round,
                                "Invalid guess: not enough cards in the deck.");
                        continue;
                    }

                    if (g1 + g2 > DECK_SIZE) {
                        if (observer != null) observer.onRoundRestart(round,
                                "Invalid guesses: total cards requested exceeds 52.");
                        continue;
                    }

                    TurnResult r1 = playTurnWithGuess(p1, g1, target, deck, observer);

                    p1Totals[round - 1] = r1.total;
                    p1Scores[round - 1] = r1.score;
                    p1Guesses[round - 1] = r1.guess;

                    p2Totals[round - 1] = r2.total;
                    p2Scores[round - 1] = r2.score;
                    p2Guesses[round - 1] = r2.guess;
                }

                break;
            }

            p1MatchScore += p1Scores[round - 1];
            p2MatchScore += p2Scores[round - 1];

            // Update target for next round
            int targetAfterThisRound = target;

            if (round != ROUNDS) {
                boolean p1Under = p1Totals[round - 1] < target;
                boolean p2Under = p2Totals[round - 1] < target;

                boolean p1Over = p1Totals[round - 1] > target;
                boolean p2Over = p2Totals[round - 1] > target;

                if (p1Under && p2Under) targetAfterThisRound += 5;
                else if (p1Over && p2Over) targetAfterThisRound -= 5;
            }

            targetAfter[round - 1] = targetAfterThisRound;

            if (observer != null) {
                observer.onRoundEnd(
                        round,
                        targetBefore[round - 1],
                        targetAfterThisRound,
                        p1Scores[round - 1],
                        p2Scores[round - 1],
                        p1MatchScore,
                        p2MatchScore
                );
            }

            target = targetAfterThisRound;
        }

        Player winner = (p1MatchScore <= p2MatchScore) ? p1 : p2;

        return new MatchOutcome(
                p1, p2,
                targetBefore, targetAfter,
                p1StartsRound,
                p1Totals, p2Totals,
                p1Scores, p2Scores,
                p1Guesses, p2Guesses,
                p1MatchScore, p2MatchScore,
                winner
        );
    }

    private boolean isValidSingleGuess(int guess) {
        return guess >= 1 && guess <= DECK_SIZE;
    }

    private TurnResult playTurnWithGuess(Player player,
                                         int guess,
                                         int target,
                                         DeckLike deck,
                                         MatchObserver observer) {

        Card[] hand = new Card[guess];
        for (int i = 0; i < guess; i++) hand[i] = deck.deal();

        int total = HandScorer.calculateOptimisedTotal(hand, guess, target);
        int score = HandScorer.calculateScore(hand, guess, target);

        if (observer != null) observer.onPlayerTurn(player, guess, hand, guess, total, score, target);

        return new TurnResult(guess, total, score);
    }

    private static class TurnResult {
        final int guess;
        final int total;
        final int score;

        TurnResult(int guess, int total, int score) {
            this.guess = guess;
            this.total = total;
            this.score = score;
        }
    }
}
