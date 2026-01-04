package model;

import java.sql.SQLOutput;
import java.util.Random;
import java.util.Scanner;

public class GameMenu {

    private final Scanner sc;
    private final Random rng;

    private final MyHashMapStringPlayer players;
    private final MatchEngine engine;

    public GameMenu() {
        sc = new Scanner(System.in);
        rng = new Random();

        players = new MyHashMapStringPlayer();
        engine = new MatchEngine();

        seedDummyPlayers();
    }

    public static void main(String[] args) {
        new GameMenu().run();
    }

    public void run() {
        while (true) {
            printMenu();
            int choice = readIntInRange("Choose option: ", 1, 7);

            if (choice == 1) playMatch();
            else if (choice == 2) viewLeaderboard();
            else if (choice == 3) runSimulation();
            else if (choice == 4) compareTwoPlayers();
            else if (choice == 5) searchPlayerHistory();
            else if (choice == 6) listPlayersWithMoreThanXWins();
            else {
                System.out.println("Exiting...");
                return;
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("=== Precision Draw ===");
        System.out.println("1. Play Match");
        System.out.println("2. View Leaderboard");
        System.out.println("3. Run a Simulation");
        System.out.println("4. Compare Two Players");
        System.out.println("5. Search Player History");
        System.out.println("6. List Players with > x Match Wins");
        System.out.println("7. Exit");
    }

    private void playMatch() {
        System.out.println();
        String name1 = readValidName("Enter Player 1 name: ");
        String name2 = readValidName("Enter Player 2 name: ");

        while (name2.equalsIgnoreCase(name1)) {
            System.out.println("Names must be unique. Enter a different Player 2 name.");
            name2 = readValidName("Enter Player 2 name: ");
        }

        PlayerRecord pr1 = players.getOrCreate(name1);
        PlayerRecord pr2 = players.getOrCreate(name2);

        Player p1 = pr1.getPlayer();
        Player p2 = pr2.getPlayer();

        DeckLike deck = new Deck();
        GuessProvider gp = new ConsoleGuessProvider(sc);

        MatchObserver observer = new ConsoleMatchObserver();
        MatchOutcome out = engine.playMatch(p1, p2, deck, gp, rng, observer);

        boolean p1Won = out.winner.getName().equals(p1.getName());
        pr1.recordMatch(out, p1Won);
        pr2.recordMatch(out, !p1Won);

        PlayerRecord winnerRecord = p1Won ? pr1 : pr2;
        MatchPrinter.printSummary(out, winnerRecord.getWins());
    }

    private void viewLeaderboard() {
        System.out.println();
        System.out.println("Leaderboard:");

        MyArrayList<PlayerRecord> sorted = LeaderboardService.getSortedByWinsDesc(players);

        if (sorted.size() == 0) {
            System.out.println("No players yet.");
            return;
        }

        for (int i = 0; i < sorted.size(); i++) {
            PlayerRecord pr = sorted.get(i);
            System.out.println((i + 1) + ". " + pr.getName()
                    + " - Wins: " + pr.getWins());
        }
    }

    private void searchPlayerHistory() {
        System.out.println();
        String name = readValidName("Enter the player name to view history: ");
        PlayerRecord pr = players.get(name);

        if (pr == null) {
            System.out.println("Player not found.");
            return;
        }
        System.out.println();
        System.out.println("-------------------------- Match History --------------------------");

        MyArrayList<PlayerRecord.HistoryEntry> hist = pr.getMatchHistory();
        if (hist.size() == 0) {
            System.out.println("No match history recorded yet.");
            System.out.println();
            return;
        }

        for (int i = 0; i < hist.size(); i++) {
            PlayerRecord.HistoryEntry entry = hist.get(i);
            MatchOutcome out = entry.outcome;

            boolean isP1 = out.player1.getName().equalsIgnoreCase(pr.getName());
            int[] roundTotals = isP1 ? out.p1Totals : out.p2Totals;
            int matchScore = isP1 ? out.p1MatchScore : out.p2MatchScore;

            String result = entry.isWin ? "win" : "loss";

            System.out.println("Match #" + (i + 1)
                    + ": Round Totals = " + formatArray(roundTotals)
                    + ", Match Score = " + matchScore
                    + " - " + result);
        }

        System.out.println();
        printAnalytics(pr);
    }

    private String formatArray(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private void compareTwoPlayers() {
        System.out.println();

        while (true) {
            String nameA = readValidName("Enter first player name: ");
            String nameB = readValidName("Enter second player name: ");

            while (nameA.equalsIgnoreCase(nameB)) {
                System.out.println("Please enter two different player names.");
                nameB = readValidName("Enter second player name: ");
            }

            PlayerRecord a = players.get(nameA);
            PlayerRecord b = players.get(nameB);

            if (a == null || b == null) {
                if (a == null && b == null) {
                    System.out.println("Players not found: " + nameA + ", " + nameB);
                } else if (a == null) {
                    System.out.println("Player not found: " + nameA);
                } else {
                    System.out.println("Player not found: " + nameB);
                }
                System.out.println("Please try again.\n");
                continue;
            }

            System.out.println();

            // ===== Player Comparison (overall stats) =====
            int aMatchesPlayed = a.getMatchesPlayed();
            int bMatchesPlayed = b.getMatchesPlayed();

            int aWins = a.getWins();
            int bWins = b.getWins();

            double aOverallPct = (aMatchesPlayed == 0) ? 0.0 : (aWins * 100.0 / aMatchesPlayed);
            double bOverallPct = (bMatchesPlayed == 0) ? 0.0 : (bWins * 100.0 / bMatchesPlayed);

            System.out.println("-------- Player Comparison --------");
            System.out.println(String.format("%-12s %-12s %-8s %-8s", "Player", "Match Count", "Wins", "Wins%"));
            System.out.println(String.format("%-12s %-12d %-8d %-8s", a.getName(), aMatchesPlayed, aWins, formatPct1dp(aOverallPct)));
            System.out.println(String.format("%-12s %-12d %-8d %-8s", b.getName(), bMatchesPlayed, bWins, formatPct1dp(bOverallPct)));
            System.out.println("-----------------------------------");
            System.out.println();

            // ===== Head-to-head =====
            int aVsBMatches = 0;
            int aVsBWins = 0;

            MyArrayList<PlayerRecord.HistoryEntry> histA = a.getMatchHistory();
            for (int i = 0; i < histA.size(); i++) {
                PlayerRecord.HistoryEntry e = histA.get(i);
                if (e.opponentName.equalsIgnoreCase(b.getName())) {
                    aVsBMatches++;
                    if (e.isWin) aVsBWins++;
                }
            }

            int bVsAWins = aVsBMatches - aVsBWins;

            double aWinPct = (aVsBMatches == 0) ? 0.0 : (aVsBWins * 100.0 / aVsBMatches);
            double bWinPct = (aVsBMatches == 0) ? 0.0 : (bVsAWins * 100.0 / aVsBMatches);

            System.out.println("--- Head-to-Head ---");
            System.out.println(a.getName() + " vs " + b.getName());
            System.out.println();
            System.out.println("Matches played against each other: " + aVsBMatches);
            System.out.println(a.getName() + " wins: " + aVsBWins + " (" + formatPct(aWinPct) + ")");
            System.out.println(b.getName() + " wins: " + bVsAWins + " (" + formatPct(bWinPct) + ")");

            return;
        }
    }

    private void listPlayersWithMoreThanXWins() {
        System.out.println();
        int x = readNonNegativeInt("Enter win threshold x: ");

        System.out.println();
        System.out.println("Leaderboard (wins > " + x + "):");

        MyArrayList<PlayerRecord> sorted = LeaderboardService.getSortedByWinsDesc(players);

        int rank = 1;
        for (int i = 0; i < sorted.size(); i++) {
            PlayerRecord pr = sorted.get(i);
            if (pr.getWins() > x) {
                System.out.println(rank + ". " + pr.getName() + " - Wins: " + pr.getWins());
                rank++;
            }
        }

        if (rank == 1) {
            System.out.println("No players found.");
        }
    }

    private void runSimulation() {
        System.out.println();
        int x = readIntInRange("Enter number of matches to simulate: ", 1, 5000);

        System.out.println("Running simulation...");

        for (int i = 0; i < x; i++) {

            String p1Name = "SimPlayer" + (1 + rng.nextInt(10));
            String p2Name = "SimPlayer" + (1 + rng.nextInt(10));

            while (p2Name.equalsIgnoreCase(p1Name)) {
                p2Name = "SimPlayer" + (1 + rng.nextInt(10));
            }

            PlayerRecord pr1 = players.getOrCreate(p1Name);
            PlayerRecord pr2 = players.getOrCreate(p2Name);

            Player p1 = pr1.getPlayer();
            Player p2 = pr2.getPlayer();

            DeckLike deck = new Deck();
            GuessProvider gp = new SimulationGuessProvider(rng);

            MatchOutcome out = engine.playMatch(p1, p2, deck, gp, rng, null);

            boolean p1Won = out.winner.getName().equals(p1.getName());
            pr1.recordMatch(out, p1Won);
            pr2.recordMatch(out, !p1Won);
        }

        System.out.println("Simulation complete.");
    }

    // ---------- input helpers ----------

    private void printAnalytics(PlayerRecord pr) {
        MyArrayList<PlayerRecord.HistoryEntry> hist = pr.getMatchHistory();

        int matches = hist.size();
        int rounds = matches * 4;

        int overshoots = 0;
        int undershoots = 0;
        int perfectHits = 0;

        int sumGuesses = 0;
        int sumTotals = 0;

        int bestMatchScore = Integer.MAX_VALUE;
        int worstMatchScore = Integer.MIN_VALUE;

        for (int i = 0; i < hist.size(); i++) {
            PlayerRecord.HistoryEntry entry = hist.get(i);
            MatchOutcome out = entry.outcome;

            boolean isP1 = out.player1.getName().equalsIgnoreCase(pr.getName());

            int[] totals = isP1 ? out.p1Totals : out.p2Totals;
            int[] scores = isP1 ? out.p1Scores : out.p2Scores;
            int[] guesses = isP1 ? out.p1Guesses : out.p2Guesses;

            int matchScore = isP1 ? out.p1MatchScore : out.p2MatchScore;

            if (matchScore < bestMatchScore) bestMatchScore = matchScore;
            if (matchScore > worstMatchScore) worstMatchScore = matchScore;

            for (int r = 0; r < 4; r++) {
                int target = out.targetBeforeRound[r];

                sumTotals += totals[r];
                sumGuesses += guesses[r];

                if (totals[r] > target) overshoots++;
                else if (totals[r] < target) undershoots++;

                if (scores[r] == -5) perfectHits++;
            }
        }

        int matchesPlayed = pr.getMatchesPlayed();
        int wins = pr.getWins();

        double winRate = (matchesPlayed == 0) ? 0.0 : (wins * 100.0 / matchesPlayed);
        double overshootRate = (rounds == 0) ? 0.0 : (overshoots * 100.0 / rounds);
        double undershootRate = (rounds == 0) ? 0.0 : (undershoots * 100.0 / rounds);
        double avgGuesses = (rounds == 0) ? 0.0 : (sumGuesses * 1.0 / rounds);
        double avgTotal = (rounds == 0) ? 0.0 : (sumTotals * 1.0 / rounds);

        System.out.println("----- Analytics -----");
        System.out.println("Matches played: " + matchesPlayed);
        System.out.println("Wins: " + wins + " (" + formatPct1dp(winRate) + ")");
        System.out.println("Overshoot rate: " + formatPct1dp(overshootRate));
        System.out.println("Undershoot rate: " + formatPct1dp(undershootRate));
        System.out.println("Perfect hits: " + perfectHits);
        System.out.println("Average cards guessed: " + format1dp(avgGuesses));
        System.out.println("Average round total: " + format1dp(avgTotal));

        if (matches > 0) {
            System.out.println("Best match score: " + bestMatchScore);
            System.out.println("Worst match score: " + worstMatchScore);
        }

        System.out.println("---------------------");
    }

    private String format1dp(double v) {
        double rounded = Math.round(v * 10.0) / 10.0;
        return String.valueOf(rounded);
    }

    private String formatPct1dp(double pct) {
        double rounded = Math.round(pct * 10.0) / 10.0;
        return rounded + "%";
    }

    private int readNonNegativeInt(String prompt) {
        while (true) {
            int v = readInt(prompt);
            if (v < 0) {
                System.out.println("Please enter a positive number.");
                continue;
            }
            return v;
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int v = readInt(prompt);
            if (v < min || v > max) {
                System.out.println("Please enter a number between " + min + " and " + max + ".");
                continue;
            }
            return v;
        }
    }

    private String formatPct(double pct) {
        int rounded = (int) Math.round(pct);
        return rounded + "%";
    }

    private String readValidName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();

            if (s.isEmpty()) {
                System.out.println("Input cannot be empty.");
                continue;
            }

            if (s.length() < 3 || s.length() > 15) {
                System.out.println("Name must be between 3 and 15 characters.");
                continue;
            }

            if (!isAlphaNumeric(s)) {
                System.out.println("Name must contain only letters and numbers.");
                continue;
            }

            return s;
        }
    }

    private boolean isAlphaNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean ok = (c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9');
            if (!ok) return false;
        }
        return true;
    }

    // ---------- demo helpers ----------

    private void seedDummyPlayers() {
        String[] names = {"Mark44", "Alex89", "Sam12", "Nina77"};
        for (int i = 0; i < names.length; i++) {
            players.getOrCreate(names[i]);
        }
    }
}
