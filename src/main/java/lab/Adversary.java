    package lab;
    
    import javafx.geometry.Rectangle2D;
    import javafx.scene.canvas.GraphicsContext;
    import javafx.scene.image.Image;

    import java.util.List;

    public interface Adversary {
        void performUpdate(Battlefield battlefield);
        boolean isEndOfDeathAnimation();
        void initiateDyingSequence();
        boolean collidesWith(Rectangle2D region);
        void render(GraphicsContext gc);
        void handleExpiring(long currentTime);
        void handleReviving(long currentTime);
        void handleMovement(long currentTime, Battlefield battlefield);
        Image determineMoveSprite();
        void executeMovement(Battlefield battlefield);
        boolean canMove(int newX, int newY, Battlefield battlefield);
        boolean withinBounds(int x, int y, Battlefield battlefield);
        void chooseNewDirection(Battlefield battlefield);
    }
