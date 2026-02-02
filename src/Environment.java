import java.util.*;

public class Environment {
    int[][] tiles;
    int[][] colors;
    int tick;
    ArrayList<Creature> creatures;

    public Environment(int width, int height) {
        tiles = new int[width][height];
        Random rand = new Random();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float r = rand.nextFloat();
                if (r < 0.7 && x < 40) {
                    tiles[x][y] = 1;
                } else if (x < 40) {
                    tiles[x][y] = 0;
                }
                if (r < 0.3 && x >= 40) {
                    tiles[x][y] = 2;
                } else if (x >= 40) {
                    tiles[x][y] = 0;
                }
            }
        }
        tick = 0;
        creatures = new ArrayList<Creature>();
        colors = new int[][] {{0, 0, 255}, {0, 255, 0}, {255, 0, 0}};
        for (int i = 0; i < 300; i++) {
            float[][] aiParams = new float[4][4];
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    aiParams[j][k] = (float)(Math.random() * 4 - 2);
                }
            }
            Creature c = new Creature(1.0f, aiParams, 10.0f, rand.nextInt(width), rand.nextInt(height), 4, 4);
            creatures.add(c);
        }
    }

    public void update() {
        tick++;
        // Update logic for environment and creatures goes here
        for (Creature c : creatures) {
            c.update(this);
        }
    }

    public void nextGeneration() {
        Random rand = new Random();
        creatures.sort(null);
        ArrayList<Creature> survivors = new ArrayList<Creature>();
        for (int i = 0; i < creatures.size() / 10; i++) {
            for (int j = 0; j < 10; j++) {
                Creature offspring = Creature.deepCopy(creatures.get(creatures.size() - 1 - i));
                if (rand.nextFloat() < 0.5) {
                    offspring.mutate();
                }
                offspring.x = rand.nextInt(tiles.length);
                offspring.y = rand.nextInt(tiles[0].length);
                survivors.add(offspring);
            }
        }
        creatures = survivors;
    }

    public void display() {
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                Display.sketch.fill(colors[tiles[x][y]][0], colors[tiles[x][y]][1], colors[tiles[x][y]][2]);
                Display.sketch.rect(x * 10, y * 10, 10, 10);
            }
        }
        for (Creature c : creatures) {
            c.display();
        }
    }
}