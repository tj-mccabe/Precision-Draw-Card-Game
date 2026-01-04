package model;

public class Player {
    private final String name;

    public Player(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }
}
