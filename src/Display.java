import java.util.*;
import processing.core.PApplet;
import processing.core.PImage;

public class Display extends PApplet {
    public static PApplet sketch;
    public static int tickSpeed, genTime;
    public static Environment env;
    public static UI ui;
    public static int frame;
    public static void main(String[] args) {
        tickSpeed = 1;
        genTime = 50;
        env = new Environment(80, 60);
        PApplet.main("Display");
    }

    @Override
    public void settings() {
        frame = 0;
        fullScreen();
    }

    @Override
    public void setup() {
        sketch = this;
        background(255);
        // surface.setResizable(true);
    }

    @Override
    public void draw() {
        background(255);
        frame++;
        if (frame % tickSpeed == 0 && env != null) {
            env.update();
            if (frame % (tickSpeed * genTime) == 0) {
                env.nextGeneration();
            }
        }
        if (env != null) {
            env.display();
        }
    }
}
