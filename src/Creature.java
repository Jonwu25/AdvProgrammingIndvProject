import java.util.ArrayList;

public class Creature implements Comparable<Creature> {
    float speed;
    float[][] aiParrameters;
    float energy;
    int x, y;
    int width, height;
    int startX, startY;

    public Creature(float speed, float[][] aiParrameters, float energy, int x, int y, int width, int height) {
        this.speed = speed;
        this.aiParrameters = aiParrameters;
        this.energy = energy;
        this.startX = x;
        this.startY = y;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(int dx, int dy, Environment e) {
        x = (x+dx + e.tiles.length) % e.tiles.length;
        y = (y+dy + e.tiles[0].length) % e.tiles[0].length;
    }

    public int transform(int a) {
        if (a!=2) {
            return a;
        }
        return -1;
    }

    public void ai(float[] inputs, Environment e) {
        float[] result = new float[aiParrameters.length];
        for (int i = 0; i < aiParrameters.length; i++) {
            result[i] = 0;
            for (int j = 0; j < inputs.length; j++) {
                result[i] += transform((int) inputs[j]) * aiParrameters[i][j];
            }
        }
        if (result[0] == Math.max(Math.max(result[0], result[1]), Math.max(result[2], result[3]))) {
            move(1, 0, e);
        } else if (result[1] == Math.max(result[0], Math.max(result[1], Math.max(result[2], result[3])))) {
            move(-1, 0, e);
        } else if (result[2] == Math.max(result[0], Math.max(result[1], Math.max(result[2], result[3])))) {
            move(0, 1, e);
        } else if (result[3] == Math.max(result[0], Math.max(result[1], Math.max(result[2], result[3])))) {
            move(0, -1, e);
        }
    }

    public void display() {
        Display.sketch.fill(0, 0, 0);
        Display.sketch.ellipse(x * Display.sketch.width/204 + Display.sketch.width/204/2, y * Display.sketch.height/115 + Display.sketch.height/115/2, Display.sketch.width/204*4/5, Display.sketch.height/115*4/5);
    }

    public void mutate() {
        for (int i = 0; i < aiParrameters.length; i++) {
            for (int j = 0; j < aiParrameters[i].length; j++) {
                if (Math.random() < 0.5) {
                    aiParrameters[i][j] += (Math.random() - 0.5) * 0.2;
                }
            }
        }
    }
    
    public static Creature deepCopy(Creature original) {
        float[][] newAIParams = new float[original.aiParrameters.length][original.aiParrameters[0].length];
        for (int i = 0; i < original.aiParrameters.length; i++) {
            System.arraycopy(original.aiParrameters[i], 0, newAIParams[i], 0, original.aiParrameters[i].length);
        }
        return new Creature(original.speed, newAIParams, original.energy, original.x, original.y, original.width, original.height);
    }

    public void resetEnergy() {
        energy = 0;
    }

    public int compareTo(Creature other) {
        return Float.compare(this.energy+1f*(float)(Math.abs(this.x - this.startX) + Math.abs(this.y - this.startY)), other.energy+1f*(float)(Math.abs(other.x - other.startX) + Math.abs(other.y - other.startY)));
    }

    public void update(Environment e) {
        ai(new float[]{e.tiles[(x-1+e.tiles.length)%e.tiles.length][y], e.tiles[(x+1)%e.tiles.length][y], e.tiles[x][(y-1+e.tiles[0].length)%e.tiles[0].length], e.tiles[x][(y+1)%e.tiles[0].length]}, e);
        if (e.tiles[x][y] == 1) {
            energy += 1;
        } else if (e.tiles[x][y] == 2) {
            energy -= 1;
        } else if (e.tiles[x][y] == 0) {
            energy -= 0.1;
        }
    }
}
