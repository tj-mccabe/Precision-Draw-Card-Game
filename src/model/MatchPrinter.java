package model;

public class MatchPrinter {

    public static void printSummary(MatchOutcome out, int winnerWinsTally) {
        System.out.println(out.player1.getName() + "'s round scores were: " + formatArray(out.p1Totals));
        System.out.println(out.player2.getName() + "'s round scores were: " + formatArray(out.p2Totals));
        System.out.println(out.winner.getName() + " wins with a wins tally of: " + winnerWinsTally);
    }

    private static String formatArray(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
