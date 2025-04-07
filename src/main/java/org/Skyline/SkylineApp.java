
package org.Skyline;

import javafx.application.Application;
import javafx.stage.Stage;


public class SkylineApp extends Application {
    private StateContext context;

    @Override
    public void start(Stage primaryStage) {
        // Initialize StateContext with primaryStage
        context = new StateContext(primaryStage);

        // Set the initial state (LoginState)
        context.setState(new LoginState(context));
    }

    public static void main(String[] args) {
        launch(args);
    }
}