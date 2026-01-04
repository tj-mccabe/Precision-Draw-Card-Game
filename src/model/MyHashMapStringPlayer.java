package model;

public class MyHashMapStringPlayer {

    private static class Entry {
        String key;
        PlayerRecord value;
        boolean deleted;

        Entry(String key, PlayerRecord value) {
            this.key = key;
            this.value = value;
            this.deleted = false;
        }
    }

    private Entry[] table;
    private int size; // active (non-deleted) entries
    private int used; // includes deleted slots

    public MyHashMapStringPlayer() {
        table = new Entry[16];
        size = 0;
        used = 0;
    }

    public int size() { return size; }

    public PlayerRecord get(String key) {
        int idx = findSlot(key);
        if (idx == -1) return null;
        Entry e = table[idx];
        return (e != null && !e.deleted) ? e.value : null;
    }

    public void put(String key, PlayerRecord value) {
        if (key == null) throw new IllegalArgumentException("key cannot be null");

        if ((used + 1) * 100 / table.length >= 70) resize(table.length * 2);

        int hash = hash(key);
        int firstTombstone = -1;

        for (int i = 0; i < table.length; i++) {
            int idx = (hash + i) % table.length;
            Entry e = table[idx];

            if (e == null) {
                if (firstTombstone != -1) idx = firstTombstone;
                table[idx] = new Entry(key, value);
                size++;
                used++;
                return;
            }

            if (e.deleted) {
                if (firstTombstone == -1) firstTombstone = idx;
            } else if (e.key.equals(key)) {
                e.value = value;
                return;
            }
        }

        // Should never happen due to resize policy
        throw new IllegalStateException("HashMap full");
    }

    public PlayerRecord getOrCreate(String key) {
        PlayerRecord existing = get(key);
        if (existing != null) return existing;

        PlayerRecord created = new PlayerRecord(new Player(key));
        put(key, created);
        return created;
    }

    public MyArrayList<PlayerRecord> values() {
        MyArrayList<PlayerRecord> out = new MyArrayList<>();
        for (int i = 0; i < table.length; i++) {
            Entry e = table[i];
            if (e != null && !e.deleted) out.add(e.value);
        }
        return out;
    }

    private int findSlot(String key) {
        int hash = hash(key);
        for (int i = 0; i < table.length; i++) {
            int idx = (hash + i) % table.length;
            Entry e = table[idx];
            if (e == null) return -1;
            if (!e.deleted && e.key.equals(key)) return idx;
        }
        return -1;
    }

    private void resize(int newCap) {
        Entry[] old = table;
        table = new Entry[newCap];
        size = 0;
        used = 0;

        for (int i = 0; i < old.length; i++) {
            Entry e = old[i];
            if (e != null && !e.deleted) {
                put(e.key, e.value);
            }
        }
    }

    private int hash(String s) {
        // Simple stable hash (no Collections)
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = 31 * h + s.charAt(i);
        }
        if (h < 0) h = -h;
        return h;
    }
}
