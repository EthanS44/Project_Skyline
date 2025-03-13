package org.Skyline;
import javafx.scene.input.TouchEvent;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.Group;

public class VirtualJoystick {
    private Circle base, knob;
    private double centerX, centerY;
    private boolean active = false;
    private double moveX = 0, moveZ = 0;

    public VirtualJoystick(Group root) {
        // Joystick base (static circle)
        base = new Circle(80, Color.GRAY);
        base.setOpacity(0.5);
        base.setTranslateX(100);
        base.setTranslateY(500);

        // Joystick knob (movable circle)
        knob = new Circle(40, Color.WHITE);
        knob.setOpacity(0.8);
        knob.setTranslateX(100);
        knob.setTranslateY(500);

        // Store center position
        centerX = base.getTranslateX();
        centerY = base.getTranslateY();

        // Add to the Group
        root.getChildren().addAll(base, knob);

        // Handle touch events
        knob.setOnTouchPressed(this::onTouchPressed);
        knob.setOnTouchMoved(this::onTouchMoved);
        knob.setOnTouchReleased(this::onTouchReleased);
    }

    private void onTouchPressed(TouchEvent event) {
        active = true;
    }

    private void onTouchMoved(TouchEvent event) {
        if (!active) return;

        // Get touch position
        double dx = event.getTouchPoint().getX() - centerX;
        double dy = event.getTouchPoint().getY() - centerY;

        // Limit movement within base circle
        double distance = Math.sqrt(dx * dx + dy * dy);
        double maxDistance = base.getRadius();
        if (distance > maxDistance) {
            dx = (dx / distance) * maxDistance;
            dy = (dy / distance) * maxDistance;
        }

        knob.setTranslateX(centerX + dx);
        knob.setTranslateY(centerY + dy);

        // Convert to movement values
        moveX = dx / maxDistance;
        moveZ = dy / maxDistance;
    }

    private void onTouchReleased(TouchEvent event) {
        // Reset joystick
        knob.setTranslateX(centerX);
        knob.setTranslateY(centerY);
        moveX = 0;
        moveZ = 0;
        active = false;
    }

    public double getMoveX() { return moveX; }
    public double getMoveZ() { return moveZ; }
}
