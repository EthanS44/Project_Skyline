package org.Skyline;

public class ModelViewState implements State {
    private StateContext context;

    public ModelViewState(StateContext context) {
        this.context = context;
        primaryStage = new Stage();
    }

    @Override
    public void showUI() {
        // Display the selected model's visual representation
        System.out.println("Displaying Model View with interactive controls");
        // You can implement JavaFX model view UI here
    }

    @Override
    public void handleAction(String action) {
        if (action.equals("rotateModel")) {
            // Implement rotation logic
        } else if (action.equals("panModel")) {
            // Implement pan logic
        }
    }
}
