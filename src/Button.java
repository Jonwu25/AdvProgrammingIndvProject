public class Button {
    float x, y, width, height; // All relative to screen size (0-1)
    boolean hovered;
    String label;
    Runnable onClick;

    public Button(float x, float y, float width, float height, String label, Runnable onClick) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.onClick = onClick;
    }

    public void checkHover() {
        int WIDTH = Display.sketch.width;
        int HEIGHT = Display.sketch.height;
        if (Display.sketch.mouseX >= x*WIDTH && Display.sketch.mouseX <= (x + width)*WIDTH && Display.sketch.mouseY >= y*HEIGHT && Display.sketch.mouseY <= (y + height)*HEIGHT) {
            hovered = true;
        } else {
            hovered = false;
        }
    }

    public void update() {
        checkHover();
        if (hovered && Display.clicked) {
            onClick.run();
        }
    }

    public void display() {
        int WIDTH = Display.sketch.width;
        int HEIGHT = Display.sketch.height;
        if(hovered) {
            Display.sketch.fill(200);
        } else {
            Display.sketch.fill(255);
        }
        Display.sketch.rect(x*WIDTH, y*HEIGHT, width*WIDTH, height*HEIGHT);
        Display.sketch.fill(0);
        //Display.sketch.textAlign(Display.sketch.CENTER, Display.sketch.CENTER);
        Display.goodText(label, (x + width / 2)*WIDTH, (y + height / 2)*HEIGHT, width*WIDTH, height*HEIGHT, "cc");
        Display.sketch.text(label, (x + width / 2)*WIDTH, (y + height / 2)*HEIGHT);
    }
}
