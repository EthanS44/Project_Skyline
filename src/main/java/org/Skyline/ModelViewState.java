package org.Skyline;
import javafx.scene.*;
import javafx.stage.Stage;

public class ModelViewState implements State {

    private StateContext context;
    private Stage primaryStage;
    private Scene scene;
    private Group root;

    public ModelViewState(StateContext context) {
        this.context = context;
        primaryStage = new Stage();
    }

    @Override
    public void showUI() {
        System.out.println("Displaying Model View with interactive controls");

        // JavaFX must be launched properly
        //new Thread(() -> Application.launch(ModelViewState.class)).start();

        start(primaryStage);
    }

    @Override
    public void handleAction(String action) {
        if (action.equals("rotateModel")) {
            System.out.println("Rotating Model...");
        } else if (action.equals("panModel")) {
            System.out.println("Panning Model...");
        }
    }
}


