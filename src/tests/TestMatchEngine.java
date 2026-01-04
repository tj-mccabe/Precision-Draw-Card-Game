package tests;

import model.*;

import java.util.Random;

public class TestMatchEngine {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        testFourRoundsAndAlternationAndTargetUpdates(); // T15–T20

        System.out.println();
        System.out.println("Tests passed: " + testsPassed + " / " + testsRun);
    }

    private static void check(String name, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("[PASS] " + name);
        } else {
            System.out.println("[FAIL] " + name);
        }
    }

    // T15–T20 (single deterministic scenario)
    private static void testFourRoundsAndAlternationAndTargetUpdates() {
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");

        // Fixed guesses by round:
        // Round1: 4 each, Round2: 5 each, Round3: 4 each, Round4: 4 each
        GuessProvider gp = new ScriptedGuessProvider(
                new int[]{0, 4, 5, 4, 4}, // P1 guesses by round (index 1..4)
                new int[]{0, 4, 5, 5, 4}  // P2 guesses by round
        );

        // FakeDeck ranks per round, in deal order:
        // Each round deals: first player's hand, then second player's hand.
        // Totals crafted to force target update rules:
        //
        // Round1 target 40: both under -> +5 (target becomes 45)
        //   hand A total 30, hand B total 35
        //
        // Round2 target 45: both over -> -5 (target becomes 40)
        //   hand A total 46, hand B total 47
        //
        // Round3 target 40: mixed -> no change (target stays 40)
        //   hand A total 38, hand B total 42
        //
        // Round4 target 40: doesn't matter for testing target rule; set both under
        //   hand A total 28, hand B total 29
        DeckLike deck = new FakeDeck(new int[][]{
                concat(new int[]{10, 9, 8, 3},       new int[]{10, 10, 9, 6}),              // R1
                concat(new int[]{10, 10, 10, 10, 6}, new int[]{10, 10, 10, 10, 7}),         // R2

                // R3 (mixed: P1 under, P2 over)
                concat(new int[]{5, 5, 5, 5},        new int[]{10, 10, 10, 10, 5}),         // R3

                concat(new int[]{7, 7, 7, 7},        new int[]{10, 9, 5, 5})                // R4
        });


        Random rng = new Random(123);
        boolean expectedP1StartsR1 = rng.nextBoolean(); // mirror engine behaviour

        // Recreate rng with same seed for engine (so starter matches expectation)
        rng = new Random(123);

        MatchEngine engine = new MatchEngine();
        MatchOutcome out = engine.playMatch(p1, p2, deck, gp, rng, null);

        // T15: exactly 4 rounds occur
        check("T15 exactly 4 rounds recorded", out.targetBeforeRound.length == 4 && out.p1Totals.length == 4);

        // T16: starter alternation works
        check("T16 round1 starter seeded", out.p1StartsRound[0] == expectedP1StartsR1);
        check("T16 alternation round2", out.p1StartsRound[1] == !out.p1StartsRound[0]);
        check("T16 alternation round3", out.p1StartsRound[2] == out.p1StartsRound[0]);
        check("T16 alternation round4", out.p1StartsRound[3] == out.p1StartsRound[1]);

        // T17–T19: target update rules
        // Round1: both under -> +5 (40 -> 45)
        check("T17 both under => +5", out.targetBeforeRound[0] == 40 && out.targetAfterRound[0] == 45);

        // Round2: both over -> -5 (45 -> 40)
        check("T18 both over => -5", out.targetBeforeRound[1] == 45 && out.targetAfterRound[1] == 40);

        // Round3: mixed -> unchanged (40 -> 40)
        check("T19 mixed => no change", out.targetBeforeRound[2] == 40 && out.targetAfterRound[2] == 40);

        // T20: sanity check that totals exist (non-zero)
        check("T20 round totals recorded", out.p1Totals[0] > 0 && out.p2Totals[0] > 0);
    }

    private static int[] concat(int[] a, int[] b) {
        int[] out = new int[a.length + b.length];
        for (int i = 0; i < a.length; i++) out[i] = a[i];
        for (int i = 0; i < b.length; i++) out[a.length + i] = b[i];
        return out;
    }

    // --- Helpers (no Collections) ---

    private static class ScriptedGuessProvider implements GuessProvider {
        private final int[] p1ByRound;
        private final int[] p2ByRound;

        ScriptedGuessProvider(int[] p1ByRound, int[] p2ByRound) {
            this.p1ByRound = p1ByRound;
            this.p2ByRound = p2ByRound;
        }

        @Override
        public int getGuess(Player player, int roundNumber, int target) {
            if (player.getName().equals("P1")) return p1ByRound[roundNumber];
            return p2ByRound[roundNumber];
        }
    }

    private static class FakeDeck implements DeckLike {
        private final int[][] rounds;
        private int roundIndex = -1;
        private int pos = 0;

        FakeDeck(int[][] rounds) {
            this.rounds = rounds;
        }

        @Override
        public void resetAndShuffle(Random rng) {
            roundIndex++;
            pos = 0;
        }

        @Override
        public Card deal() {
            int rank = rounds[roundIndex][pos];
            pos++;
            return new Card(rank);
        }
    }
}
