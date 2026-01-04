package model;

public class LeaderboardService {

    public static MyArrayList<PlayerRecord> getSortedByWinsDesc(MyHashMapStringPlayer map) {
        MyArrayList<PlayerRecord> list = map.values();

        // Insertion sort by wins DESC, then name ASC
        for (int i = 1; i < list.size(); i++) {
            PlayerRecord key = list.get(i);
            int j = i - 1;

            while (j >= 0 && compare(key, list.get(j)) < 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
        return list;
    }

    // return <0 if a should come before b
    private static int compare(PlayerRecord a, PlayerRecord b) {
        if (a.getWins() != b.getWins()) return (b.getWins() - a.getWins()); // wins desc
        return a.getName().compareToIgnoreCase(b.getName()); // name asc
    }
}
