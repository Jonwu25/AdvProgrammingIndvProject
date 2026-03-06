import java.lang.reflect.Field;

public class Slider {
    float x, y, width, height; // All relative to screen size (0-1)
    int min, max;
    Field value;
    boolean dragging;
    String label;
    
    public Slider(float x, float y, float width, float height, int min, int max, Field value, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.min = min;
        this.max = max;
        this.value = value;
        this.label = label;
    }

    public void checkDragging() {
        int WIDTH = Display.sketch.width;
        int HEIGHT = Display.sketch.height;

        if (Display.clicked || (dragging && Display.clicking)) {
            if (Display.sketch.mouseX >= x*WIDTH && Display.sketch.mouseX <= (x + width)*WIDTH && Display.sketch.mouseY >= y*HEIGHT && Display.sketch.mouseY <= (y + height)*HEIGHT) {
                dragging = true;
            }
        } else {
            dragging = false;
        }
    }

    public void update() {
        checkDragging();
        int WIDTH = Display.sketch.width;
        int HEIGHT = Display.sketch.height;
        if (dragging) {
            if (Display.sketch.mouseX < x*WIDTH) {
                try {
                    value.set(null, min);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Display.sketch.mouseX > (x + width)*WIDTH) {
                try {
                    value.set(null, max);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    value.set(null, (int) (min+(max-min)*(Display.sketch.mouseX-x*WIDTH)/(width*WIDTH)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void display() {
        Display.sketch.fill(124, 167, 235);
        float ratio;
        try {
            ratio = (value.getInt(null) - min) / (float)(max - min);
        } catch (Exception e) {
            e.printStackTrace();
            ratio = 0;
        }
        
        int WIDTH = Display.sketch.width;
        int HEIGHT = Display.sketch.height;

        Display.sketch.rect(x*WIDTH, y*HEIGHT, ratio*width*WIDTH, height*HEIGHT, height*HEIGHT/2, 0, 0, height*HEIGHT/2);
        Display.sketch.fill(255);
        Display.sketch.rect((x+ratio*width)*WIDTH, y*HEIGHT, (1-ratio)*width*WIDTH, height*HEIGHT, 0, height*HEIGHT/2, height*HEIGHT/2, 0);
        Display.sketch.circle((x+ratio*width)*WIDTH, (y+height/2)*HEIGHT, height*HEIGHT);
        Display.sketch.fill(0);
        Display.sketch.textSize(12);
        try {
            Display.sketch.text(label + ": " + value.getInt(null), x*WIDTH + width*WIDTH/2, y*HEIGHT - height*HEIGHT/2);
        } catch (Exception e) {
            Display.sketch.text(label, x*WIDTH + width*WIDTH/2, y*HEIGHT - height*HEIGHT/2);
            e.printStackTrace();
        }
        Display.sketch.text(min, x*WIDTH, y*HEIGHT + height*HEIGHT*3/2);
        Display.sketch.text(max, (x + width)*WIDTH, (y + height*3/2)*HEIGHT);
    }
}
