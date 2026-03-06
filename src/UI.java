public class UI {
    // Should contain all UI elements and handle their updates and display
    Button[] tutorialButtons;
    Button[] simulationButtons;
    Slider[] sliders;

    public UI(Button[] tutorialButtons, Button[] simulationButtons, Slider[] sliders) {
        this.tutorialButtons = tutorialButtons;
        this.simulationButtons = simulationButtons;
        this.sliders = sliders;
    }

    public void display() {
        if (Display.state.contains("tutorial")) {
            for (Button b : tutorialButtons) {
                b.display();
            }
        }
        for (Button b : simulationButtons) {
            b.display();
        }
        for (Slider s : sliders) {
            s.display();
        }
    }

    public void update() {
        if (Display.state.contains("tutorial")) {
            Button b = tutorialButtons[Display.state.charAt(0) - '0'];
            b.update();
        } else {
            for (Button b : simulationButtons) {
                b.update();
            }
            for (Slider s : sliders) {
                s.update();
            }
        }
    }
}
