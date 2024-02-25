package lab;

import javafx.scene.image.Image;

public class SandBlock {
    private Image texture;
    private double posX, posY;

    public SandBlock(double x, double y, Image texture) {
        this.posX = x;
        this.posY = y;
        this.texture = texture;
    }

    public Image getTexture() {
        return texture;
    }

    public double getX() {
        return posX;
    }

    public double getY() {
        return posY;
    }
}
