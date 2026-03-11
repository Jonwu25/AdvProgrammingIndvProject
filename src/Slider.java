import java.lang.reflect.Field;

public class Slider {
    float x, y, width, height; // All relative to screen size (0-1)
    float min, max;
    Field value;
    boolean dragging;
    String label;
    
    public Slider(float x, float y, float width, float height, float min, float max, Field value, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.min = min;
        this.max = max;
        this.value = value;
        this.label = label;
    }

    public boolean checkHover() {
        int WIDTH = Display.sketch.width;
        int HEIGHT = Display.sketch.height;
        if (Display.sketch.mouseX >= x*WIDTH && Display.sketch.mouseX <= (x + width)*WIDTH && Display.sketch.mouseY >= y*HEIGHT && Display.sketch.mouseY <= (y + height)*HEIGHT) {
            return true;
        } else if (Math.sqrt(Math.pow(Display.sketch.mouseX - (x)*WIDTH, 2) + Math.pow(Display.sketch.mouseY - (y+height/2)*HEIGHT, 2)) <= height*HEIGHT/2) {
            return true;
        } else if (Math.sqrt(Math.pow(Display.sketch.mouseX - (x+width)*WIDTH, 2) + Math.pow(Display.sketch.mouseY - (y+height/2)*HEIGHT, 2)) <= height*HEIGHT/2) {
            return true;
        } else {
            return false;
        }
    }

    public void checkDragging() {
        int WIDTH = Display.sketch.width;
        int HEIGHT = Display.sketch.height;

        if (Display.clicked || (dragging && Display.clicking)) {
            if (checkHover()) {
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
                    if (value.getGenericType() == int.class) {
                        value.setInt(null, (int)min);
                    } else if (value.getGenericType() == float.class) {
                        value.setFloat(null, min);
                    } else {
                        value.set(null, value.getType().cast(min));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Display.sketch.mouseX > (x + width)*WIDTH) {
                try {
                    if (value.getGenericType() == int.class) {
                        value.setInt(null, (int)max);
                    } else if (value.getGenericType() == float.class) {
                        value.setFloat(null, max);
                    } else {
                        value.set(null, value.getType().cast(max));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (value.getGenericType() == int.class) {
                        value.setInt(null, (int)(min+(max-min)*(Display.sketch.mouseX-x*WIDTH)/(width*WIDTH)));
                    } else if (value.getGenericType() == float.class) {
                        value.setFloat(null, min+(max-min)*(Display.sketch.mouseX-x*WIDTH)/(width*WIDTH));
                    } else {
                        value.set(null, value.getType().cast(min+(max-min)*(Display.sketch.mouseX-x*WIDTH)/(width*WIDTH)));
                    }
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
            if (value.getGenericType() == int.class) {
                ratio = (value.getInt(null) - min) / (float)(max - min);
            } else if (value.getGenericType() == float.class) {
                ratio = (value.getFloat(null) - min) / (float)(max - min);
            } else {
                ratio = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ratio = 0;
        }
        
        int WIDTH = Display.sketch.width;
        int HEIGHT = Display.sketch.height;

        Display.sketch.noStroke();
        Display.sketch.rect(x*WIDTH, y*HEIGHT, ratio*width*WIDTH, height*HEIGHT);
        Display.sketch.fill(255);
        Display.sketch.rect((x+ratio*width)*WIDTH, y*HEIGHT, (1-ratio)*width*WIDTH, height*HEIGHT);
        Display.sketch.fill(124, 167, 235);
        Display.sketch.stroke(0);
        Display.sketch.arc(x*WIDTH, (y+height/2)*HEIGHT, height*HEIGHT, height*HEIGHT, Display.sketch.PI/2, Display.sketch.PI*3/2);
        Display.sketch.fill(255);
        Display.sketch.arc((x+width)*WIDTH, (y+height/2)*HEIGHT, height*HEIGHT, height*HEIGHT, -Display.sketch.PI*1/2, Display.sketch.PI/2);
        Display.sketch.line((x)*WIDTH, y*HEIGHT, (x+width)*WIDTH, (y)*HEIGHT);
        Display.sketch.line((x)*WIDTH, (y+height)*HEIGHT, (x+width)*WIDTH, (y+height)*HEIGHT);
        Display.sketch.fill(255);
        Display.sketch.circle((x+ratio*width)*WIDTH, (y+height/2)*HEIGHT, height*HEIGHT);
        Display.sketch.fill(0);
        Display.sketch.textSize(12);
        try {
            Display.sketch.text(label + ": " + value.get(null), x*WIDTH + width*WIDTH/2, y*HEIGHT - height*HEIGHT/2);
        } catch (Exception e) {
            Display.sketch.text(label, x*WIDTH + width*WIDTH/2, y*HEIGHT - height*HEIGHT/2);
            e.printStackTrace();
        }
        Display.sketch.text(min, x*WIDTH, y*HEIGHT + height*HEIGHT*3/2);
        Display.sketch.text(max, (x + width)*WIDTH, (y + height*3/2)*HEIGHT);
    }
}
