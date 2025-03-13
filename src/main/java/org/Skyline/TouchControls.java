package org.Skyline;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class TouchControls {
    private Text rotateLeft, rotateRight, moveUp, moveDown;
    private boolean rotateLeftPressed = false;
    private boolean rotateRightPressed = false;
    private boolean moveUpPressed = false;
    private boolean moveDownPressed = false;


    public TouchControls(Group root) {
        // Create rotation and movement buttons with event handling
        rotateLeft = createButton("⟲", 800, 500, "up");
        rotateRight = createButton("⟳", 900, 500, "down");
        moveUp = createButton("↑", 850, 400, "left");
        moveDown = createButton("↓", 850, 600, "right");

        // Add buttons to the root group
        root.getChildren().addAll(rotateLeft, rotateRight, moveUp, moveDown);

        // Start listening for button presses
    }

    private Text createButton(String label, double x, double y, String press) {
        Text button = new Text(label);
        button.setFill(Color.WHITE);
        button.setStyle("-fx-font-size: 30px;");
        button.setX(x);
        button.setY(y);

        // Handle touch start
        button.setOnMousePressed(event -> {
            button.setFill(Color.GRAY); // Visual feedback
            if (press.equals("up")) {
                moveUpPressed = true;
            } else if (press.equals("down")) {
                moveDownPressed = true;
            } else if (press.equals("left")) {
                rotateLeftPressed = true;
            } else if (press.equals("right")) {
                rotateRightPressed = true;
            }
        });

        // Handle touch release
        button.setOnMouseReleased(event -> {
            button.setFill(Color.WHITE);
            if (press.equals("up")) {
                moveUpPressed = false;
            } else if (press.equals("down")) {
                moveDownPressed = false;
            } else if (press.equals("left")) {
                rotateLeftPressed = false;
            } else if (press.equals("right")) {
                rotateRightPressed = false;
            }
        });

        return button;
    }

    // Getter methods for checking button states
    public boolean isRotateLeftPressed() { return rotateLeftPressed; }
    public boolean isRotateRightPressed() { return rotateRightPressed; }
    public boolean isMoveUpPressed() { return moveUpPressed; }
    public boolean isMoveDownPressed() { return moveDownPressed; }
}


