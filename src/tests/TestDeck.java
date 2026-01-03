package tests;

import java.util.Random;
import model.Deck;

public class TestDeck {

    private static int testsPassed = 0;
    private static int testsRun = 0;

    public static void main(String[] args) {
        testDeckSize();               // T11
        testDealAllCards();           // T12
        testResetRestoresDeck();      // T13
        testShuffleChangesOrder();    // T14 (basic sanity)

        System.out.println();
        System.out.println("Tests passed: " + testsPassed + " / " + testsRun);
    }

    private static void assertEquals(String testName, int expected, int actual) {
        testsRun++;
        if (expected == actual) {
            testsPassed++;
            System.out.println("[PASS] " + testName);
        } else {
            System.out.println("[FAIL] " + testName +
                    " | expected: " + expected + ", actual: " + actual);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("[PASS] " + testName);
        } else {
            System.out.println("[FAIL] " + testName);
        }
    }

    // T11
    private static void testDeckSize() {
        Deck deck = new Deck();
        assertEquals("Deck size is 52", 52, deck.size());
        assertEquals("Deck remainingCards initially 52", 52, deck.remainingCards());
    }

    // T12
    private static void testDealAllCards() {
        Deck deck = new Deck();
        deck.shuffle(new Random(123)); // deterministic shuffle
        for (int i = 0; i < 52; i++) {
            deck.deal();
        }
        assertEquals("Remaining cards after dealing 52", 0, deck.remainingCards());

        // Optional sanity: dealing one more should throw
        boolean threw = false;
        try {
            deck.deal();
        } catch (IllegalStateException ex) {
            threw = true;
        }
        assertTrue("Dealing from empty deck throws", threw);
    }

    // T13
    private static void testResetRestoresDeck() {
        Deck deck = new Deck();
        deck.shuffle(new Random(1));
        for (int i = 0; i < 52; i++) deck.deal();
        assertEquals("Remaining cards after 52 dealt", 0, deck.remainingCards());

        deck.resetAndShuffle(new Random(2));
        assertEquals("Remaining cards after reset", 52, deck.remainingCards());

        // Deal one after reset to confirm it works
        deck.deal();
        assertEquals("Remaining cards after 1 deal post-reset", 51, deck.remainingCards());
    }

    // T14 (basic sanity, not proving randomness)
    private static void testShuffleChangesOrder() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();

        // Without shuffling, both decks are built in the same order.
        // After shuffling with different seeds, first few ranks should differ (very likely).
        deck1.shuffle(new Random(100));
        deck2.shuffle(new Random(200));

        int sameCount = 0;
        for (int i = 0; i < 10; i++) {
            if (deck1.peekRankAt(i) == deck2.peekRankAt(i)) {
                sameCount++;
            }
        }

        // If all 10 were identical, something is suspicious.
        assertTrue("Shuffle changes order (sanity check)", sameCount < 10);
    }
}
