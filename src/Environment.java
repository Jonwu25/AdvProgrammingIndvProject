import java.util.*;

public class Environment {
    int[][] tiles;
    int tick;
    ArrayList<Creature> creatures;

    public Environment(int width, int height) {
        tiles = new int[width][height];
        tick = 0;
        creatures = new ArrayList<Creature>();
    }

    public void update() {
        tick++;
        // Update logic for environment and creatures goes here
    }

    public void display() {
        // Display logic for environment and creatures goes here
    }
}