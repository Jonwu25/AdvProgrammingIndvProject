import java.util.*;

public class Environment {
    int[][] tiles;
    int[][] colors;
    int tick;
    ArrayList<Creature> creatures;
    ArrayList<Float> avgEnergy;

    public Environment(int width, int height) {
        tiles = new int[width][height];
        Random rand = new Random();
        // Create random environment
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float r = rand.nextFloat();
                if (r < 1 && x < 40) {
                    tiles[x][y] = 1;
                } else if (x < 40) {
                    tiles[x][y] = 0;
                }
                if (r < 1 && x >= 40) {
                    tiles[x][y] = 2;
                } else if (x >= 40) {
                    tiles[x][y] = 0;
                }
            }
        }
        tick = 0;
        creatures = new ArrayList<Creature>();
        colors = new int[][] {{0, 0, 255}, {0, 255, 0}, {255, 0, 0}};
        // Place initial creatures
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
        avgEnergy = new ArrayList<Float>();
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
        float en = 0;
        for (Creature c : creatures) {
            en += c.energy;
        }
        avgEnergy.add(en / creatures.size());
        ArrayList<Creature> survivors = new ArrayList<Creature>();
        for (int i = 0; i < creatures.size() / 10; i++) {
            for (int j = 0; j < 10; j++) {
                Creature offspring = Creature.deepCopy(creatures.get(creatures.size() - 1 - i));
                if (rand.nextFloat() < 0.5) {
                    offspring.mutate();
                }
                offspring.x = rand.nextInt(tiles.length);
                offspring.y = rand.nextInt(tiles[0].length);
                offspring.resetEnergy();
                survivors.add(offspring);
            }
        }
        creatures = survivors;
    }

    public void display() {
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                Display.sketch.fill(colors[tiles[x][y]][0], colors[tiles[x][y]][1], colors[tiles[x][y]][2]);
                Display.sketch.rect(x * Display.sketch.width/204, y * Display.sketch.height/115, Display.sketch.width/204, Display.sketch.height/115);
            }
        }
        for (Creature c : creatures) {
            c.display();
        }
        for (int i = 0; i < avgEnergy.size() - 1; i++) {
            Display.sketch.stroke(0);
            Display.sketch.strokeWeight(2);
            float minEnergy = Collections.min(avgEnergy);
            float maxEnergy = Collections.max(avgEnergy);
            Display.sketch.line(i * Display.sketch.width/(avgEnergy.size() - 1), Display.sketch.height - (avgEnergy.get(i)-minEnergy) * (Display.sketch.height/2)/(maxEnergy-minEnergy),
                            (i + 1) * Display.sketch.width/(avgEnergy.size() - 1), Display.sketch.height - (avgEnergy.get(i + 1)-minEnergy) * (Display.sketch.height/2)/(maxEnergy-minEnergy));
            Display.sketch.strokeWeight(1);
        }
    }
}