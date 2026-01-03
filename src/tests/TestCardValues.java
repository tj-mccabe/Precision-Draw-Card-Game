package tests;

import model.Card;

public class TestCardValues {

    private static int testsPassed = 0;
    private static int testsRun = 0;

    public static void main(String[] args) {
        testNumberCards();
        testFaceCards();
        testAceValue();

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

    // T01
    private static void testNumberCards() {
        for (int rank = 2; rank <= 10; rank++) {
            Card card = new Card(rank);
            assertEquals("Number card " + rank, rank, card.getBaseValue());
        }
    }

    // T02
    private static void testFaceCards() {
        assertEquals("Jack value", 10, new Card(11).getBaseValue());
        assertEquals("Queen value", 10, new Card(12).getBaseValue());
        assertEquals("King value", 10, new Card(13).getBaseValue());
    }

    // T03
    private static void testAceValue() {
        assertEquals("Ace base value", 11, new Card(14).getBaseValue());
    }
}
