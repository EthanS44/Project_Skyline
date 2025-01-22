package org.Skyline;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.collections.ObservableList;

import java.util.*;

@Component
public class StateContext {
    private State currentState;
    private Stage primaryStage;
    private String currentUser;
    private Model selectedModel;
    private ArrayList<Model> modelList;
    private String xParameter = "NUMBEROFFIELDS";
    private String yParameter = "LINESOFCODE";
    private String zParameter = "NUMBEROFMETHODS";

    @Autowired
    private ModelRepository modelRepository;

    public StateContext(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.modelList = new ArrayList<Model>();

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

    public ModelRepository getModelRepository(){return this.modelRepository;}

    public void setSelectedModel(Model selectedModel){
        this.selectedModel = selectedModel;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public ArrayList<Model> getModelList(){
        return modelList;
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