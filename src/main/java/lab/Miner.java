package lab;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;

public class Miner {
    private Image idleImageUp, idleImageDown, idleImageLeft, idleImageRight;
    private Image moveImageUp, moveImageDown, moveImageLeft, moveImageRight;
    private Image pumpImageDown, pumpImageLeft, pumpImageRight, pumpImageUp;
    private Image pumpPushImageDown, pumpPushImageRight, pumpPushImageUp, pumpPushImageLeft;
    private Image hoseImageDown, hoseImageLeft, hoseImageRight, hoseImageUp;
    private Image currentSprite;
    private String currentSpriteId;
    private Image hoseSprite;
    private Hose hose;
    private Battlefield battlefield;
    private GameControl controlManager;

    private boolean movingLeft, movingRight, movingUp, movingDown;
    private long lastMovementTimestamp = 0;
    private static final long MOVEMENT_INTERVAL = 170;
    private static final int MOVEMENT_SPEED = 10;
    private int positionX, positionY;
    private static final int SIZE = Battlefield.BLOCK_SIZE;
    private boolean spriteToggle = false;
    private boolean isPumping = false;
    private long lastPumpTimestamp = 0;
    private static final long PUMP_ANIMATION_INTERVAL = 200;
    private int hoseX, hoseY;
    private static final int HOSE_WIDTH = 30;
    private static final int HOSE_HEIGHT = 30;
    private int lifeCount = 3;
    private boolean alive = true;
    private int hoseWidth, hoseHeight;

    public Miner(int x, int y, boolean movingLeft, boolean movingRight, boolean movingUp, boolean movingDown, Battlefield battlefield, GameControl controlManager) {
        this.positionX = x;
        this.positionY = y;
        this.movingLeft = movingLeft;
        this.movingRight = movingRight;
        this.movingUp = movingUp;
        this.movingDown = movingDown;
        this.battlefield = battlefield;
        this.controlManager = controlManager;

        idleImageUp = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle_up.png")));
        idleImageDown = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle_down.png")));
        idleImageLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle_left.png")));
        idleImageRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle_right.png")));
        moveImageUp = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/run_up.png")));
        moveImageDown = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/run_down.png")));
        moveImageLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/run_left.png")));
        moveImageRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/run_right.png")));
        hoseImageDown = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/hose_down.png")));
        hoseImageLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/hose_left.png")));
        hoseImageRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/hose_right.png")));
        hoseImageUp = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/hose_up.png")));
        pumpImageDown = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/pump_down.png")));
        pumpImageLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/pump_left.png")));
        pumpImageRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/pump_right.png")));
        pumpImageUp = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/pump_up.png")));
        pumpPushImageDown = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/pump_push_down.png")));
        pumpPushImageRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/pump_push_right.png")));
        pumpPushImageUp = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/pump_push_up.png")));
        pumpPushImageLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/pump_push_left.png")));

        currentSprite = idleImageRight;
        currentSpriteId = "idleRight";
    }

    public void moveLeft() {
        if (isPumping) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMovementTimestamp > MOVEMENT_INTERVAL) {
            int maxLeftMove = Math.min(MOVEMENT_SPEED, positionX);
            positionX -= maxLeftMove;

            updateMovementSprite("left");
            lastMovementTimestamp = currentTime;
        }
    }

    public void moveRight() {
        if (isPumping) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMovementTimestamp > MOVEMENT_INTERVAL) {
            int maxRightMove = Math.min(MOVEMENT_SPEED, 600 - SIZE - positionX);
            positionX += maxRightMove;

            updateMovementSprite("right");
            lastMovementTimestamp = currentTime;
        }
    }

    public void moveUp() {
        if (isPumping) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMovementTimestamp > MOVEMENT_INTERVAL) {
            if (positionY - MOVEMENT_SPEED >= (Battlefield.SKY_LIMIT * Battlefield.BLOCK_SIZE) - SIZE) {
                positionY -= MOVEMENT_SPEED;
            }

            updateMovementSprite("up");
            lastMovementTimestamp = currentTime;
        }
    }

    public void moveDown() {
        if (isPumping) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMovementTimestamp > MOVEMENT_INTERVAL) {
            if (positionY + MOVEMENT_SPEED + SIZE <= 600) {
                positionY += MOVEMENT_SPEED;
            }

            updateMovementSprite("down");
            lastMovementTimestamp = currentTime;
        }
    }

    private void updateMovementSprite(String direction) {
        spriteToggle = !spriteToggle;
        switch (direction) {
            case "left":
                currentSprite = spriteToggle ? idleImageLeft : moveImageLeft;
                currentSpriteId = spriteToggle ? "idleLeft" : "moveLeft";
                break;
            case "right":
                currentSprite = spriteToggle ? idleImageRight : moveImageRight;
                currentSpriteId = spriteToggle ? "idleRight" : "moveRight";
                break;
            case "up":
                currentSprite = spriteToggle ? idleImageUp : moveImageUp;
                currentSpriteId = spriteToggle ? "idleUp" : "moveUp";
                break;
            case "down":
                currentSprite = spriteToggle ? idleImageDown : moveImageDown;
                currentSpriteId = spriteToggle ? "idleDown" : "moveDown";
                break;
        }
    }

    public void halt() {
        if (isPumping) {
            return;
        }
        if (!movingLeft && !movingRight && !movingUp && !movingDown) {
            switch (currentSpriteId) {
                case "moveRight":
                    currentSprite = idleImageRight;
                    currentSpriteId = "idleRight";
                    break;
                case "moveLeft":
                    currentSprite = idleImageLeft;
                    currentSpriteId = "idleLeft";
                    break;
                case "moveUp":
                    currentSprite = idleImageUp;
                    currentSpriteId = "idleUp";
                    break;
                case "moveDown":
                    currentSprite = idleImageDown;
                    currentSpriteId = "idleDown";
                    break;
            }
        }
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(currentSprite, positionX, positionY, SIZE, SIZE);
        if (isPumping && hoseSprite != null) {
            gc.drawImage(hoseSprite, hoseX, hoseY, hoseWidth, hoseHeight);
        }
    }

    public void update(Battlefield battlefield, List<Adversary> adversaries) {
        if (!alive) {
            return;
        }
        if (isPumping) {
            movingLeft = movingRight = movingUp = movingDown = false;
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPumpTimestamp > PUMP_ANIMATION_INTERVAL) {
                togglePumpSprite();
                lastPumpTimestamp = currentTime;
            }
        } else {
            battlefield.removeSandBlockAt(this.positionX, this.positionY);
        }

        checkCollisionsWithAdversaries(adversaries);
    }

    private void togglePumpSprite() {
        switch (currentSpriteId) {
            case "idleUp":
            case "moveUp":
                currentSprite = (currentSprite == pumpImageUp) ? pumpPushImageUp : pumpImageUp;
                break;
            case "idleDown":
            case "moveDown":
                currentSprite = (currentSprite == pumpImageDown) ? pumpPushImageDown : pumpImageDown;
                break;
            case "idleLeft":
            case "moveLeft":
                currentSprite = (currentSprite == pumpImageLeft) ? pumpPushImageLeft : pumpImageLeft;
                break;
            case "idleRight":
            case "moveRight":
                currentSprite = (currentSprite == pumpImageRight) ? pumpPushImageRight : pumpImageRight;
                break;
        }
    }

    private void checkCollisionsWithAdversaries(List<Adversary> adversaries) {
        Rectangle2D minerArea = new Rectangle2D(positionX, positionY, SIZE, SIZE);
        for (Adversary adversary : adversaries) {
            if (adversary.collidesWith(minerArea)) {
                loseLife();
                return;
            }
        }
    }

    private void loseLife() {
        lifeCount--;
        if (lifeCount <= 0) {
            alive = false;
        } else {
            respawn();
        }
    }

    private void respawn() {
        positionX = 280;
        positionY = 350;
        isPumping = false;
        movingLeft = movingRight = movingUp = movingDown = false;
    }

    public void initiatePump() {
        if (!isPumping && !movingLeft && !movingRight && !movingUp && !movingDown) {
            isPumping = true;
            lastPumpTimestamp = System.currentTimeMillis();
            String currentDirection = getCurrentDirection();
            int maxPumpLength = 80;
            hose = new Hose(positionX, positionY, currentDirection, maxPumpLength);
            updateHoseSprite(currentDirection, maxPumpLength);
        }
    }

    private void updateHoseSprite(String currentDirection, int pumpLength) {
        switch (currentDirection) {
            case "Left":
                hoseSprite = hoseImageLeft;
                hoseX = positionX - pumpLength;
                hoseY = positionY;
                hoseWidth = pumpLength;
                hoseHeight = HOSE_HEIGHT;
                break;
            case "Right":
                hoseSprite = hoseImageRight;
                hoseX = positionX + SIZE;
                hoseY = positionY;
                hoseWidth = pumpLength;
                hoseHeight = HOSE_HEIGHT;
                break;
            case "Up":
                hoseSprite = hoseImageUp;
                hoseX = positionX;
                hoseY = positionY - pumpLength;
                hoseWidth = HOSE_WIDTH;
                hoseHeight = pumpLength;
                break;
            case "Down":
                hoseSprite = hoseImageDown;
                hoseX = positionX;
                hoseY = positionY + SIZE;
                hoseWidth = HOSE_WIDTH;
                hoseHeight = pumpLength;
                break;
        }
    }

    public void stopPump() {
        if (isPumping) {
            isPumping = false;
            hose = null;
            updateIdleSprite();
        }
    }

    private void updateIdleSprite() {
        switch (currentSpriteId) {
            // Проверяем текущее направление движения или позицию и устанавливаем соответствующий спрайт
            case "pumpUp":
            case "pumpPushUp":
                currentSprite = idleImageUp;
                currentSpriteId = "idleUp";
                break;
            case "pumpDown":
            case "pumpPushDown":
                currentSprite = idleImageDown;
                currentSpriteId = "idleDown";
                break;
            case "pumpLeft":
            case "pumpPushLeft":
                currentSprite = idleImageLeft;
                currentSpriteId = "idleLeft";
                break;
            case "pumpRight":
            case "pumpPushRight":
                currentSprite = idleImageRight;
                currentSpriteId = "idleRight";
                break;
        }
    }

    private String getCurrentDirection() {
        return currentSpriteId.replaceAll("idle|move|pumpPush|pump", "");
    }

    public Hose getPump() {
        return hose;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getLives() {
        return lifeCount;
    }

    public boolean isPumping() {
        return isPumping;
    }
}

