import java.util.ArrayList;

public class Creature {
    float speed;
    ArrayList<Float> aiParrameters;
    float energy;
    int x, y;
    int width, height;

    public Creature(float speed, ArrayList<Float> aiParrameters, float energy, int x, int y, int width, int height) {
        this.speed = speed;
        this.aiParrameters = aiParrameters;
        this.energy = energy;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move() {
        // Placeholder
    }

    public void ai() {
        // Placeholder
    }

    public void display() {
        // Placeholder
    }

    public void update() {
        // Placeholder
    }
}
