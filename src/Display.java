import java.util.*;
import processing.core.PApplet;
import processing.core.PImage;

public class Display extends PApplet {
    public static PApplet sketch;
    public int tickSpeed;
    public Environment env;
    public UI ui;
    public int frame;
    public static void main(String[] args) {
        PApplet.main("Display");
    }

    @Override
    public void settings() {
        frame = 0;
        fullScreen();
    }

    @Override
    public void setup() {
        background(255);
    }

    @Override
    public void draw() {
        background(255);
        frame++;
        if (frame % tickSpeed == 0 && env != null) {
            env.update();
        }
        if (env != null) {
            env.display();
        }
    }
}
