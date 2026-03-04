import java.util.*;
import processing.core.PApplet;
import processing.core.PImage;
import java.lang.reflect.Field;

public class Display extends PApplet {
    public static PApplet sketch;
    public static int tickSpeed;
    public static int genSpeed;
    public static Environment env;
    public static UI ui;
    public static int frame;
    public static String state;
    public static void main(String[] args) {
        tickSpeed = 4;
        genSpeed = 50;
        state = "tutorial";
        env = new Environment(80, 60);
        try {
            Field tickField = Display.class.getDeclaredField("tickSpeed");
            tickField.setAccessible(true);
            Field stateField = Display.class.getDeclaredField("state");
            stateField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        frameRate(10);
    }

    @Override
    public void draw() {
        background(255);
        if (state.equals("running")) {
            frame++;
            if (frame % tickSpeed == 0 && env != null) {
                env.update();
                if (frame % (tickSpeed * genSpeed) == 0) {
                    env.nextGeneration();
                }
            }
            env.moveUpdates(frame, tickSpeed);
        }
        if (env != null) {
            env.display();
        }
        if (state.equals("tutorial")) {
            fill(220);
            rect(width/4, height/4, width/2, height/2);
            fill(0);
            textSize(20);
            textAlign(CENTER);
            text("Welcome to the Evolution Simulation!", width/2, height/2 - 30);
            text("This tutorial text will be updated after more features are added.", width/2, height/2);
            text("(Click to continue)", width/2, height/2 + 30);
            if (mousePressed) {
                state = "running";
            }
        }
    }
}
