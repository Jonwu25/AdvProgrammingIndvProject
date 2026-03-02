import java.lang.reflect.Field;

public class Slider {
    int x, y, width, height;
    int min, max;
    Field value;
    boolean dragging;
    
    public Slider(int x, int y, int width, int height, int min, int max, Field value) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.min = min;
        this.max = max;
        this.value = value;
    }

    public void checkDragging() {
        if (Display.sketch.mousePressed) {
            if (Display.sketch.mouseX >= x && Display.sketch.mouseX <= x + width && Display.sketch.mouseY >= y && Display.sketch.mouseY <= y + height) {
                dragging = true;
            }
        } else {
            dragging = false;
        }
    }

    public void update() {
        checkDragging();
        if (dragging) {
            if (Display.sketch.mouseX < x) {
                try {
                    value.set(null, min);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Display.sketch.mouseX > x + width) {
                try {
                    value.set(null, max);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    value.set(null, min+(max-min)*(Display.sketch.mouseX-x)/width);
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
        
        Display.sketch.rect(x, y, ratio*width, height, height/2, 0, 0, height/2);
        Display.sketch.fill(255);
        Display.sketch.rect(x+ratio*width, y, (1-ratio)*width, height, 0, height/2, height/2, 0);
        Display.sketch.circle(x+ratio*width, y+height/2, height);
    }
}
