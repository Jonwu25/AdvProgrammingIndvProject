import java.util.*;
import processing.core.PApplet;
import processing.core.PImage;
import java.lang.reflect.Field;

public class Display extends PApplet {
    public static PApplet sketch;
    public static int tickSpeed;
    public static int oldTickSpeed;
    public static int genSpeed;
    public static float mutationRate;
    public static float mutationAmount;
    public static Environment env;
    public static UI ui;
    public static int frame;
    public static int genFrame;
    public static String state;
    public static boolean clicked;
    public static boolean clicking;
    public static void main(String[] args) {
        tickSpeed = 1;
        oldTickSpeed = tickSpeed;
        genSpeed = 1;
        state = "0tutorial";
        mutationRate = 0.1f;
        mutationAmount = 0.1f;
        env = new Environment(80, 60);
        PApplet.main("Display");
    }

    @Override
    public void settings() {
        frame = 0;
        genFrame = 0;
        fullScreen();
    }

    @Override
    public void setup() {
        sketch = this;
        background(255);
        // surface.setResizable(true);
        // frameRate(10);
        Slider[] sliders;
        try {
            Field tickField = Display.class.getDeclaredField("tickSpeed");
            tickField.setAccessible(true);
            Slider tickSlider = new Slider(1 - 1f/5 - 1f/10, 1f/10, 1f/5, 1f/25, 1, 10, tickField, "Tick Speed");
            Field stateField = Display.class.getDeclaredField("state");
            stateField.setAccessible(true);
            Field mutationRateField = Display.class.getDeclaredField("mutationRate");
            mutationRateField.setAccessible(true);
            Slider mutationRateSlider = new Slider(1 - 1f/5 - 1f/10, 1f/10 + 2f/25, 1f/5, 1f/25, 0, 1, mutationRateField, "Mutation Rate");
            Field mutationAmountField = Display.class.getDeclaredField("mutationAmount");
            mutationAmountField.setAccessible(true);
            Slider mutationAmountSlider = new Slider(1 - 1f/5 - 1f/10, 1f/10 + 4f/25, 1f/5, 1f/25, 0, 1, mutationAmountField, "Mutation Amount");
            sliders = new Slider[]{tickSlider, mutationRateSlider, mutationAmountSlider};
        } catch (Exception e) {
            sliders = new Slider[0];
            e.printStackTrace();
        }
        Button[] tutorialButtons = new Button[]{new Button(1f/2 - 1f/60, 1f/2 + 1f/10 - 1f/60, 1f/30, 1f/30, "Next", () -> state = "running")};
        Button[] simulationButtons = new Button[]{new Button(1f - 1f/100 - 1f/30, 1f/100, 1f/30, 1f/30, "Pause", () -> {
            if (state.equals("running")) {
                state = "paused";
            } else {
                state = "running";
            }
        })};
        ui = new UI(tutorialButtons, simulationButtons, sliders);
    }

    @Override
    public void draw() {
        if (Display.sketch.mousePressed) {
            clicked = !clicking;
            clicking = true;
        } else {
            clicked = false;
            clicking = false;
        }
        background(255);
        ui.update();
        if (tickSpeed != oldTickSpeed) {
            frame = (int) ((frame / (float)oldTickSpeed) * tickSpeed);
            oldTickSpeed = tickSpeed;
        }
        if (state.equals("running")) {
            frame++;
            genFrame++;
            if (frame % tickSpeed == 0 && env != null) {
                env.update();
                if (genFrame % genSpeed == 0) {
                    env.nextGeneration(mutationAmount, mutationRate, genSpeed);
                    genFrame = 0;
                    // genSpeed = 10 + env.avgEnergy.size()/5;
                    genSpeed = 50;
                }
            }
            env.moveUpdates(frame, tickSpeed);
        }
        if (env != null) {
            env.display();
        }
        ui.display(true);
        if (state.equals("0tutorial")) {
            fill(220);
            rect(width/4, height/4, width/2, height/2);
            fill(0);
            textSize(20);
            textAlign(CENTER);
            text("Welcome to the Evolution Simulation!", width/2, height/2 - 30);
            text("This tutorial text will be updated after more features are added.", width/2, height/2);
            ui.display(false);
        }
    }

    public static void goodText(String s, float x, float y, float textWidth, float textHeight) {
        float left = 8;
        float right = 100;
        float tempSize = 54;
        int numTimes = 100;
        sketch.textSize(tempSize);
        while ((sketch.textWidth(s) < 9*textWidth/10) || ((sketch.textAscent() + sketch.textDescent())*0.8 < 9*textHeight/10)
                || (sketch.textWidth(s) > textWidth) || (sketch.textAscent() + sketch.textDescent())*0.8 > textHeight || (numTimes < 10)) {
            if (sketch.textWidth(s) > textWidth || (sketch.textAscent() + sketch.textDescent())*0.8 > textHeight) {
                // too big
                right = tempSize;
            } else {
                // too small
                left = tempSize;
            }
            tempSize = (left + right) / 2;
            sketch.textSize(tempSize);
            numTimes++;
        }
        sketch.text(s, x, y);
    }
}
