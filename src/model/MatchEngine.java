package model;

import java.util.Random;

public class MatchEngine {

    private static final int ROUNDS = 4;

    public MatchOutcome playMatch(Player p1,
                                  Player p2,
                                  DeckLike deck,
                                  GuessProvider guessProvider,
                                  Random rng) {

        int target = 40;

        int[] targetBefore = new int[ROUNDS];
        int[] targetAfter = new int[ROUNDS];
        boolean[] p1StartsRound = new boolean[ROUNDS];

        int[] p1Totals = new int[ROUNDS];
        int[] p2Totals = new int[ROUNDS];
        int[] p1Scores = new int[ROUNDS];
        int[] p2Scores = new int[ROUNDS];

        boolean p1StartsRound1 = rng.nextBoolean();

        int p1MatchScore = 0;
        int p2MatchScore = 0;

        for (int round = 1; round <= ROUNDS; round++) {
            deck.resetAndShuffle(rng);

            targetBefore[round - 1] = target;

            boolean p1StartsThisRound = (round % 2 == 1) ? p1StartsRound1 : !p1StartsRound1;
            p1StartsRound[round - 1] = p1StartsThisRound;

            if (p1StartsThisRound) {
                TurnResult r1 = takeTurn(p1, round, target, deck, guessProvider);
                TurnResult r2 = takeTurn(p2, round, target, deck, guessProvider);

                p1Totals[round - 1] = r1.total;
                p1Scores[round - 1] = r1.score;

                p2Totals[round - 1] = r2.total;
                p2Scores[round - 1] = r2.score;
            } else {
                TurnResult r2 = takeTurn(p2, round, target, deck, guessProvider);
                TurnResult r1 = takeTurn(p1, round, target, deck, guessProvider);

                p1Totals[round - 1] = r1.total;
                p1Scores[round - 1] = r1.score;

                p2Totals[round - 1] = r2.total;
                p2Scores[round - 1] = r2.score;
            }

            p1MatchScore += p1Scores[round - 1];
            p2MatchScore += p2Scores[round - 1];

            // Target update rule for next round
            boolean p1Under = p1Totals[round - 1] < target;
            boolean p2Under = p2Totals[round - 1] < target;

            boolean p1Over = p1Totals[round - 1] > target;
            boolean p2Over = p2Totals[round - 1] > target;

            if (p1Under && p2Under) {
                target += 5;
            } else if (p1Over && p2Over) {
                target -= 5;
            }

            targetAfter[round - 1] = target;
        }

        Player winner = (p1MatchScore <= p2MatchScore) ? p1 : p2;

        return new MatchOutcome(
                p1, p2,
                targetBefore, targetAfter,
                p1StartsRound,
                p1Totals, p2Totals,
                p1Scores, p2Scores,
                p1MatchScore, p2MatchScore,
                winner
        );
    }

    private TurnResult takeTurn(Player player,
                                int round,
                                int target,
                                DeckLike deck,
                                GuessProvider guessProvider) {

        int guess = guessProvider.getGuess(player, round, target);

        if (guess < 1 || guess > 52) {
            throw new IllegalArgumentException("Guess must be between 1 and 52");
        }

        Card[] hand = new Card[guess];
        for (int i = 0; i < guess; i++) {
            hand[i] = deck.deal();
        }

        int total = HandScorer.calculateOptimisedTotal(hand, guess, target);
        int score = HandScorer.calculateScore(hand, guess, target);

        return new TurnResult(total, score);
    }

    private static class TurnResult {
        final int total;
        final int score;

        TurnResult(int total, int score) {
            this.total = total;
            this.score = score;
        }
    }
}
