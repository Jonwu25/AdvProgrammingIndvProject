import java.util.*;

public class Environment {
    int[][] tiles;
    int[][] colors;
    int tick;
    ArrayList<Creature> creatures;

    public Environment(int width, int height) {
        tiles = new int[width][height];
        tick = 0;
        creatures = new ArrayList<Creature>();
        colors = new int[][] {{255, 0, 0}, {0, 255, 0}, {0, 0, 255}};
    }

    public void update() {
        tick++;
        // Update logic for environment and creatures goes here
    }

    public void display() {
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                Display.sketch.fill(colors[tiles[x][y]][0], colors[tiles[x][y]][1], colors[tiles[x][y]][2]);
                Display.sketch.rect(x * 10, y * 10, 10, 10);
            }
        }
    }
}