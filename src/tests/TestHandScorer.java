package tests;

import model.Card;
import model.HandScorer;

public class TestHandScorer {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        testBaseTotalNoAces();        // T04
        testOvershootPenalty();       // T05
        testPerfectHit();             // T06
        testSingleAceSafe();           // T07
        testMultipleAcesPerfect();    // T08
        testAcePreventsOvershoot();   // T09 (FAIL)
        testScoreUsesOptimisedTotal();// T10 (FAIL)

        System.out.println();
        System.out.println("Tests passed: " + testsPassed + " / " + testsRun);
    }

    private static void check(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("[PASS] " + testName);
        } else {
            System.out.println("[FAIL] " + testName);
        }
    }

    // T04
    private static void testBaseTotalNoAces() {
        Card[] hand = { new Card(10), new Card(9), new Card(8) };
        int score = HandScorer.calculateScore(hand, 3, 40);
        check("Base total without aces", score == 13);
    }

    // T05
    private static void testOvershootPenalty() {
        Card[] hand = { new Card(10), new Card(10), new Card(10), new Card(10), new Card(5) };
        int score = HandScorer.calculateScore(hand, 5, 40);
        check("Overshoot penalty applied", score == 10);
    }

    // T06
    private static void testPerfectHit() {
        Card[] hand = { new Card(10), new Card(10), new Card(10), new Card(10) };
        int score = HandScorer.calculateScore(hand, 4, 40);
        check("Perfect hit gives -5 score", score == -5);
    }

    // T07
    private static void testSingleAceSafe() {
        Card[] hand = { new Card(14), new Card(9), new Card(9), new Card(9) };
        int score = HandScorer.calculateScore(hand, 4, 40);
        check("Single ace remains 11 when safe", score == 2);
    }

    // T08
    private static void testMultipleAcesPerfect() {
        Card[] hand = { new Card(14), new Card(14), new Card(9), new Card(9) };
        int score = HandScorer.calculateScore(hand, 4, 40);
        check("Multiple aces perfect hit", score == -5);
    }

    // T09
    private static void testAcePreventsOvershoot() {
        Card[] hand = { new Card(14), new Card(10), new Card(10), new Card(10) };
        int score = HandScorer.calculateScore(hand, 4, 40);
        check("Ace reduced to prevent overshoot", score == 9);
    }

    // T10
    private static void testScoreUsesOptimisedTotal() {
        Card[] hand = { new Card(14), new Card(10), new Card(10), new Card(10) };
        int score = HandScorer.calculateScore(hand, 4, 40);
        check("Score uses optimised total", score == 9);
    }
}
