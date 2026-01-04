package model;

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
            int choice = readInt("Choose option: ");

            if (choice == 1) playMatch();
            else if (choice == 2) viewLeaderboard();
            else if (choice == 3) runSimulation();
            else if (choice == 4) compareTwoPlayers();
            else if (choice == 5) searchPlayerHistory();
            else if (choice == 6) listPlayersWithMoreThanXWins();
            else if (choice == 7) {
                System.out.println("Goodbye.");
                return;
            } else {
                System.out.println("Option not available yet. (Level 6+ later)");
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
        String name1 = readNonEmpty("Enter Player 1 name: ");
        String name2 = readNonEmpty("Enter Player 2 name: ");

        while (name2.equalsIgnoreCase(name1)) {
            System.out.println("Names must be unique. Enter a different Player 2 name.");
            name2 = readNonEmpty("Enter Player 2 name: ");
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
        String name = readNonEmpty("Enter the player name to view history: ");
        PlayerRecord pr = players.get(name);

        if (pr == null) {
            System.out.println("Player not found.");
            return;
        }

        System.out.println("--- Match History ---");

        MyArrayList<PlayerRecord.HistoryEntry> hist = pr.getMatchHistory();
        if (hist.size() == 0) {
            System.out.println("No match history recorded yet.");
            System.out.println();
            System.out.println("Total Wins: " + pr.getWins());
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
        System.out.println("Total Wins: " + pr.getWins());
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
        String nameA = readNonEmpty("Enter first player name: ");
        String nameB = readNonEmpty("Enter second player name: ");

        if (nameA.equalsIgnoreCase(nameB)) {
            System.out.println("Please enter two different player names.");
            return;
        }

        PlayerRecord a = players.get(nameA);
        PlayerRecord b = players.get(nameB);

        if (a == null || b == null) {
            System.out.println("One or both players not found.");
            return;
        }

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

        System.out.println();
        System.out.println("--- Head-to-Head ---");
        System.out.println(a.getName() + " vs " + b.getName());
        System.out.println("Matches played: " + aVsBMatches);
        System.out.println(a.getName() + " wins: " + aVsBWins + " (" + formatPct(aWinPct) + ")");
        System.out.println(b.getName() + " wins: " + bVsAWins + " (" + formatPct(bWinPct) + ")");
    }

    private void listPlayersWithMoreThanXWins() {
        System.out.println();
        int x = readInt("Enter win threshold x: ");

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
        int x = readInt("Enter number of matches to simulate: ");

        if (x <= 0) {
            System.out.println("Please enter a positive number.");
            return;
        }

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

    private String formatPct(double pct) {
        int rounded = (int) Math.round(pct);
        return rounded + "%";
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Input cannot be empty.");
        }
    }

    // ---------- demo helpers ----------

    private void seedDummyPlayers() {
        String[] names = {"Mark44", "Alex89", "Sam12", "Nina77"};
        for (int i = 0; i < names.length; i++) {
            players.getOrCreate(names[i]);
        }
    }
}
