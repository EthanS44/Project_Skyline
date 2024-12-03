package org.Skyline;

import javafx.stage.Stage;

public class StateContext {
    private State currentState;
    private Stage primaryStage;
    private String currentUser;
    private String selectedModel;


    public StateContext(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setState(State state) {
        this.currentState = state;
        currentState.showUI();
    }

    public void setUser(String username) {
        this.currentUser = username;
    }

    public String getCurrentUser(){
        return currentUser;
    }

    public void setSelectedModel(String selectedModel){
        this.selectedModel = selectedModel;
    }

    public String getSelectedModel(){
        return selectedModel;
    }

    public void executeAction(String action) {
        if (currentState != null) {
            currentState.handleAction(action);
        }
    }
}