import java.util.*;

public class Environment {
    float[][] tiles;
    HashMap<Integer, int[]> colors = new HashMap<Integer, int[]>();
    int[][] in;
    int[][] moves;
    int width, height;
    ArrayList<Creature> creatures;
    ArrayList<Float> avgEnergy;

    public Environment(int width, int height) {
        tiles = new float[width][height];
        Random rand = new Random();
        // Create random environment
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float r = rand.nextFloat();
                if (x < width/3) {
                    tiles[x][y] = 1;
                } else if (x < 2*width/3) {
                    tiles[x][y] = 0;
                } else {
                    tiles[x][y] = -1;
                }
            }
        }
        this.width = width;
        this.height = height;
        creatures = new ArrayList<Creature>();
        colors.put(0, new int[]{0, 0, 255});
        colors.put(1, new int[]{0, 255, 0});
        colors.put(-1, new int[]{255, 0, 0});
        in = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {0, 0}};
        moves = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        // Place initial creatures
        for (int i = 0; i < 300; i++) {
            Creature c = new Creature(1.0f, new int[]{4, 4}, in, moves, 0, rand.nextInt(width), rand.nextInt(height), 4, 4);
            c.setMethods();
            creatures.add(c);
        }
        avgEnergy = new ArrayList<Float>();
    }

    public void update() {
        // Update logic for environment and creatures goes here
        for (Creature c : creatures) {
            c.update(this);
        }
    }

    public float getTile(int x, int y) {
        return tiles[(x + tiles.length) % tiles.length][(y + tiles[0].length) % tiles[0].length];
    }

    public void moveUpdates(int frame, int tickSpeed) {
        for (Creature c : creatures) {
            c.moveUpdate(frame, tickSpeed);
        }
    }

    public void nextGeneration(float mutationAmount, float mutationRate, int genSpeed) {
        Random rand = new Random();
        creatures.sort(null);
        float en = 0;
        for (Creature c : creatures) {
            en += c.energy;
        }
        avgEnergy.add(en / creatures.size() / genSpeed);
        ArrayList<Creature> survivors = new ArrayList<Creature>();
        for (int i = 0; i < creatures.size() / 10; i++) {
            for (int j = 0; j < 10; j++) {
                Creature offspring = Creature.deepCopy(creatures.get(creatures.size() - 1 - i));
                if (rand.nextFloat() < 0.5) {
                    offspring.mutate(mutationAmount, mutationRate);
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
        // tiles

        float displayWidth = Display.sketch.width/2 - Display.sketch.width/30;
        float displayHeight = Display.sketch.height - Display.sketch.height/30;
        float sidelength = Math.min(displayWidth/tiles.length, displayHeight/tiles[0].length);
        float xOffset = Display.sketch.width/4 - (float) tiles.length/2 * sidelength;
        float yOffset = Display.sketch.height/2 - (float) tiles[0].length/2 * sidelength;
        Display.sketch.strokeWeight(sidelength/50);
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                int[] color = new int[3];
                ArrayList<Integer> keys = new ArrayList<>(colors.keySet());
                Collections.sort(keys);
                if (tiles[x][y] % 1==0f && keys.contains((int) tiles[x][y])) {
                    color = colors.get((int) tiles[x][y]);
                } else {
                    // finding desired key (Binary search)

                    int desiredKey = 0;
                    int leftIndex = 0;
                    int rightIndex = keys.size();
                    while (leftIndex+1!=rightIndex) {
                        desiredKey = (leftIndex+rightIndex)/2;
                        if (keys.get(desiredKey) < tiles[x][y]) {
                            leftIndex = desiredKey;
                        } else {
                            rightIndex = desiredKey;
                        }
                    }
                    desiredKey = leftIndex;
                    if (tiles[x][y] < keys.get(0)) {
                        desiredKey = -1;
                    } else if (tiles[x][y] > keys.get(keys.size()-1)) {
                        desiredKey = keys.size() - 1;
                    }

                    // using desired key

                    if (desiredKey == -1) {
                        color = colors.get(keys.get(0));
                    } else if (desiredKey == keys.size() - 1) {
                        color = colors.get(keys.get(keys.size() - 1));
                    } else {
                        int left = keys.get(desiredKey);
                        int right = keys.get(desiredKey+1);
                        int[] colorLeft = colors.get(left);
                        int[] colorRight = colors.get(right);
                        float ratio = (tiles[x][y] - left)/((float) right-left);
                        color = new int[] {(int) ((1f-ratio)*colorLeft[0]+ratio*colorRight[0]),
                                        (int) ((1f-ratio)*colorLeft[1]+ratio*colorRight[1]),
                                        (int) ((1f-ratio)*colorLeft[2]+ratio*colorRight[2])};

                    }
                }
                Display.sketch.fill(color[0], color[1], color[2]);
                Display.sketch.rect(x * sidelength + xOffset, y * sidelength + yOffset, sidelength, sidelength);
            }
        }

        // creatures

        for (Creature c : creatures) {
            c.display(xOffset, yOffset, sidelength, tiles.length, tiles[0].length);
        }

        // energy graph

       displayEnergyGraph();
    }

    public void reset() {
        Random rand = new Random();
        creatures = new ArrayList<Creature>();
        for (int i = 0; i < 300; i++) {
            Creature c = new Creature(1.0f, new int[]{4, 4}, in, moves, 0, rand.nextInt(width), rand.nextInt(height), 4, 4);
            c.setMethods();
            creatures.add(c);
        }
        avgEnergy = new ArrayList<Float>();
    }

    public void displayEnergyGraph() {
        float displayWidth = Display.sketch.width/2 - Display.sketch.width/30;
        float displayHeight = Display.sketch.height/2 - Display.sketch.height/10;
        float xOffset = Display.sketch.width/2 + Display.sketch.width/60;
        float yOffset = Display.sketch.height/2 + 4*Display.sketch.height/60;
        Display.sketch.fill(0);
        Display.goodText("Graph of Average Energy Per Step Over Generations", xOffset+displayWidth/2, yOffset-Display.sketch.height/60, displayWidth, Display.sketch.height/30, "cb");
        Display.goodText("Generations", xOffset+displayWidth/2, yOffset+displayHeight+Display.sketch.height/30, displayWidth, Display.sketch.height/60, "cb");
        Display.sketch.stroke(0);
        Display.sketch.strokeWeight(2);
        Display.sketch.fill(255);
        Display.sketch.rect(xOffset, yOffset, displayWidth, displayHeight);
        if (avgEnergy.size() < 2) {
            Display.sketch.strokeWeight(1);
            return;
        }
        float minEnergy = Collections.min(avgEnergy);
        float maxEnergy = Collections.max(avgEnergy);
        for (int i = 0; i < avgEnergy.size() - 1; i++) {
            Display.sketch.line(xOffset + i * displayWidth/(avgEnergy.size() - 1), yOffset + displayHeight - (avgEnergy.get(i)-minEnergy) * (displayHeight)/(maxEnergy-minEnergy),
                            xOffset + (i + 1) * displayWidth/(avgEnergy.size() - 1), yOffset + displayHeight - (avgEnergy.get(i + 1)-minEnergy) * (displayHeight)/(maxEnergy-minEnergy));
        }
        int b = 0;
        for (int i = 0; i < avgEnergy.size(); i++) {
            Display.sketch.fill(0);
            if (30*i/avgEnergy.size() >= b) {
                Display.goodText(String.valueOf(i+1), xOffset + i * displayWidth/(avgEnergy.size() - 1), yOffset + displayHeight + Display.sketch.height/60, displayWidth/(avgEnergy.size() - 1), Display.sketch.height/60, "cb");
                b++;
            }
        }
        Display.goodText(String.format("%.2f", minEnergy), xOffset - Display.sketch.width/60, yOffset + displayHeight, Display.sketch.width/60, Display.sketch.height/60, "cl");
        Display.goodText(String.format("%.2f", maxEnergy), xOffset - Display.sketch.width/60, yOffset, Display.sketch.width/60, Display.sketch.height/60, "cl");
        Display.sketch.strokeWeight(1);
    }

    public void setDefault(int version) {
        if (version == 1) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (x < width/3) {
                        tiles[x][y] = 1;
                    } else if (x < 2*width/3) {
                        tiles[x][y] = 0;
                    } else {
                        tiles[x][y] = -1;
                    }
                }
            }
        } else if (version == 2) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    tiles[x][y] = (float) Math.sin((x-width/2f)/width * 2f*Math.PI);
                }
            }
        }
    }
}