package org.Skyline;

import javafx.stage.Stage;

public class StateContext {
    private State currentState;
    private Stage primaryStage;
    private String currentUser;
    private Model selectedModel;
    private String xParameter;
    private String yParameter;
    private String zParameter;


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

    public void setSelectedModel(Model selectedModel){
        this.selectedModel = selectedModel;
    }

    public Model getSelectedModel(){
        return selectedModel;
    }

    public void executeAction(String action) {
        if (currentState != null) {
            currentState.handleAction(action);
        }
    }

    public String getxParameter() {
        return xParameter;
    }

    public String getyParameter() {
        return yParameter;
    }

    public String getzParameter() {
        return zParameter;
    }
}