public class UI {
    // Should contain all UI elements and handle their updates and display
    Button[] buttons;
    Slider[] sliders;

    public UI(Button[] buttons, Slider[] sliders) {
        this.buttons = buttons;
        this.sliders = sliders;
    }

    public void display() {
        for (Button b : buttons) {
            b.display();
        }
        for (Slider s : sliders) {
            s.display();
        }
    }

    public void update() {
        for (Button b : buttons) {
            b.update();
        }
        for (Slider s : sliders) {
            s.update();
        }
    }
}
