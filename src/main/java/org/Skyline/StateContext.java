package org.Skyline;

import javafx.stage.Stage;

import java.util.ArrayList;

public class StateContext {
    private State currentState;
    private Stage primaryStage;
    private String currentUser;
    private Model selectedModel;
    private ArrayList<Model> modelList;
    private DatabaseManager databaseManager;
    private String xParameter = "NUMBER OF FIELDS";
    private String yParameter = "LINES OF CODE";
    private String zParameter = "NUMBER OF METHODS";
    private int xParameterThreshold = 500;
    private int yParameterThreshold = 500;
    private int zParameterThreshold = 500;

    // Added to track if threshold is a maximum or minimum
    private boolean xThresholdMaximum = true;  // Default: upper limit
    private boolean yThresholdMaximum = true;  // Default: upper limit
    private boolean zThresholdMaximum = true;  // Default: upper limit

    public StateContext(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.modelList = new ArrayList<Model>();
        this.databaseManager = new DatabaseManager();
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

    public String getCurrentUser() {
        return currentUser;
    }

    public void setSelectedModel(Model selectedModel) {
        this.selectedModel = selectedModel;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setXParameter(String xParameter) {
        this.xParameter = xParameter;
    }

    public void setYParameter(String yParameter) {
        this.yParameter = yParameter;
    }

    public void setZParameter(String zParameter) {
        this.zParameter = zParameter;
    }

    public void setXParameterThreshold(int xParameterThreshold) {
        this.xParameterThreshold = xParameterThreshold;
    }

    public void setYParameterThreshold(int yParameterThreshold) {
        this.yParameterThreshold = yParameterThreshold;
    }

    public void setZParameterThreshold(int zParameterThreshold) {
        this.zParameterThreshold = zParameterThreshold;
    }

    // Getter and Setter for Upper/Lower limit tracking
    public boolean isXThresholdMaximum() {
        return xThresholdMaximum;
    }

    public void setXThresholdMaximum(boolean xThresholdUpperLimit) {
        this.xThresholdMaximum = xThresholdUpperLimit;
    }

    public boolean isYThresholdMaximum() {
        return yThresholdMaximum;
    }

    public void setYThresholdMaximum(boolean yThresholdUpperLimit) {
        this.yThresholdMaximum = yThresholdUpperLimit;
    }

    public boolean isZThresholdMaximum() {
        return zThresholdMaximum;
    }

    public void setZThresholdMaximum(boolean zThresholdUpperLimit) {
        this.zThresholdMaximum = zThresholdUpperLimit;
    }

    public ArrayList<Model> getModelList() {
        return modelList;
    }

    public void setModelList(ArrayList<Model> modelList) {
        this.modelList = modelList;
    }

    public Model getSelectedModel() {
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

    public int getxParameterThreshold() {
        return xParameterThreshold;
    }

    public int getyParameterThreshold() {
        return yParameterThreshold;
    }

    public int getzParameterThreshold() {
        return zParameterThreshold;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}