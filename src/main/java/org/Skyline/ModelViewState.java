package org.Skyline;

import javafx.stage.Stage;

public class ModelViewState implements State {

    private StateContext context;
    private Stage primaryStage;

    public ModelViewState(StateContext context) {
        this.context = context;
        primaryStage = new Stage();
    }

    @Override
    public void showUI() {
        System.out.println("Displaying Model View with interactive controls");
        Renderer renderer = new Renderer(context);
        renderer.start(primaryStage);
    }

    @Override
    public void handleAction(String action) {
    }
}


