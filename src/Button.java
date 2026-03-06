public class Button {
    int x, y, width, height;
    boolean hovered;
    String label;
    Runnable onClick;

    public Button(int x, int y, int width, int height, String label, Runnable onClick) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.onClick = onClick;
    }

    public void checkHover() {
        if (Display.sketch.mouseX >= x && Display.sketch.mouseX <= x + width && Display.sketch.mouseY >= y && Display.sketch.mouseY <= y + height) {
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
        if(hovered) {
            Display.sketch.fill(200);
        } else {
            Display.sketch.fill(255);
        }
        Display.sketch.rect(x, y, width, height);
        Display.sketch.fill(0);
        Display.sketch.textAlign(Display.sketch.CENTER, Display.sketch.CENTER);
        Display.sketch.text(label, x + width / 2, y + height / 2);
    }
}
