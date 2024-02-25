package lab;

import javafx.geometry.Rectangle2D;

public class Hose {
    private Rectangle2D area;
    private static final int HOSE_WIDTH = 40;
    private static final int HOSE_HEIGHT = 40;
    private int x, y;

    public Hose(int x, int y, String minerDirection, int hoseLength) {
        this.x = x;
        this.y = y;
        initializeArea(minerDirection, hoseLength);
    }

    private void initializeArea(String minerDirection, int hoseLength) {
        switch (minerDirection) {
            case "Left":
                area = new Rectangle2D(x - hoseLength, y, hoseLength, HOSE_HEIGHT);
                break;
            case "Right":
                area = new Rectangle2D(x + HOSE_WIDTH, y, hoseLength, HOSE_HEIGHT);
                break;
            case "Up":
                area = new Rectangle2D(x, y - hoseLength, HOSE_WIDTH, hoseLength);
                break;
            case "Down":
                area = new Rectangle2D(x, y + HOSE_WIDTH, HOSE_WIDTH, hoseLength);
                break;
            default:
                area = new Rectangle2D(x, y, HOSE_WIDTH, HOSE_HEIGHT);
                break;
        }
    }

    public Rectangle2D getArea() {
        return area;
    }
}
