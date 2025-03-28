package org.Skyline;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;

public class Tree extends Group {
    private final double height;

    public Tree(double trunkHeight, double trunkRadius, double foliageRadius) {
        this.height = trunkHeight + (foliageRadius * 2); // Total height of tree

        // Trunk (Cylinder)
        Cylinder trunk = new Cylinder(trunkRadius, trunkHeight);
        PhongMaterial trunkMaterial = new PhongMaterial();
        trunkMaterial.setDiffuseColor(Color.rgb(75,50,25));
        trunkMaterial.setSpecularColor(Color.rgb(75,50,25));
        trunk.setMaterial(trunkMaterial);
        trunk.setTranslateY(-trunkHeight / 2); // Position it properly

        // Foliage (Sphere)
        Sphere foliage = new Sphere(foliageRadius);
        PhongMaterial foliageMaterial = new PhongMaterial();
        foliageMaterial.setDiffuseColor(Color.rgb(0, 40, 0));
        foliageMaterial.setSpecularColor(Color.rgb(0, 40, 0));
        foliage.setMaterial(foliageMaterial);
        foliage.setTranslateY(-trunkHeight - foliageRadius); // Sit above trunk

        // Add to group
        this.getChildren().addAll(trunk, foliage);
    }

    public double getHeight() {
        return height;
    }
}

