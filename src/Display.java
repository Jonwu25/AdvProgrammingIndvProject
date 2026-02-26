import java.util.*;
import processing.core.PApplet;
import processing.core.PImage;

public class Display extends PApplet {
    public static PApplet sketch;
    public static int tickSpeed, genTime;
    public static Environment env;
    public static UI ui;
    public static int frame;
    public static boolean paused, tutorial;
    public static void main(String[] args) {
        tickSpeed = 1;
        genTime = 50;
        paused = true;
        tutorial = true;
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
        if (!paused) {
            frame++;
            if (frame % tickSpeed == 0 && env != null) {
                env.update();
                if (frame % (tickSpeed * genTime) == 0) {
                    env.nextGeneration();
                }
            }
        }
        if (env != null) {
            env.display();
        }
        if (tutorial) {
            fill(220);
            rect(width/4, height/4, width/2, height/2);
            fill(0);
            textSize(20);
            textAlign(CENTER);
            text("Welcome to the Evolution Simulation!", width/2, height/2 - 30);
            text("This tutorial text will be updated after more features are added.", width/2, height/2);
            text("(Click to continue)", width/2, height/2 + 30);
            if (mousePressed) {
                tutorial = false;
                paused = false;
            }
        }
    }
}
