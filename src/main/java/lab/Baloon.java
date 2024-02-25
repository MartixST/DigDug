package lab;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Baloon implements Adversary {
    private Image idleLeft, idleRight;
    private Image moveLeft, moveRight;
    private Image perishStartLeft, perishStartMidLeft, perishEndMidLeft, perishEndLeft;
    private Image perishStartRight, perishStartMidRight, perishEndMidRight, perishEndRight;
    private Image currentSprite;
    private Image scoreImage;
    private GameControl gameControl;
    private Direction lastHorizontalDirection = Direction.RIGHT;
    private enum Direction { LEFT, RIGHT, UP, DOWN }

    private boolean spriteToggle = false;
    private long lastMoveTimestamp = 0;
    private static final long MOVE_INTERVAL = 170;
    private static final int SPEED = 9;
    private int positionX, positionY;
    private static final int SIZE = Battlefield.BLOCK_SIZE;
    private boolean expiring = false;
    private int perishStage = 0;
    private long lastPerishTime = 0;
    private static final long PERISH_ANIMATION_INTERVAL = 200;
    private boolean reviving = false;
    private long lastRevivalTime = 0;
    private boolean displayScore = false;
    private int scorePosX, scorePosY;
    private boolean showingScore = false;

    private Direction moveDirection;

    public Baloon(int x, int y, GameControl gameControl) {
        this.positionX = x;
        this.positionY = y;

        idleLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_left_idle.png")));
        idleRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_right_idle.png")));
        moveLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_left_move.png")));
        moveRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_right_move.png")));
        perishStartLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_perish_start_left.png")));
        perishStartMidLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_perish_start_mid_left.png")));
        perishEndMidLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_perish_end_mid_left.png")));
        perishEndLeft = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_perish_end_left.png")));
        perishStartRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_perish_start_right.png")));
        perishStartMidRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_perish_start_mid_right.png")));
        perishEndMidRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_perish_end_mid_right.png")));
        perishEndRight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/enemies/baloon_perish_end_right.png")));

        currentSprite = idleRight;
        moveDirection = Direction.RIGHT;

        this.gameControl = gameControl;
    }

    @Override
    public void performUpdate(Battlefield battlefield) {
        long currentTime = System.currentTimeMillis();
        if (expiring) {
            handleExpiring(currentTime);
        } else if (reviving) {
            handleReviving(currentTime);
        } else {
            handleMovement(currentTime, battlefield);
        }
    }

    @Override
    public void handleExpiring(long currentTime) {
        if (currentTime - lastPerishTime > PERISH_ANIMATION_INTERVAL) {
            if (perishStage < 4) {
                perishStage++;
                updatePerishSprite();
            }
            lastPerishTime = currentTime;
        }
    }

    @Override
    public void handleReviving(long currentTime) {
        if (currentTime - lastRevivalTime > PERISH_ANIMATION_INTERVAL) {
            if (perishStage > 0) {
                perishStage--;
                updateRevivalSprite();
            }
            lastRevivalTime = currentTime;
            if (perishStage == 0) {
                reviving = false;
            }
        }
    }

    @Override
    public void handleMovement(long currentTime, Battlefield battlefield) {
        if (currentTime - lastMoveTimestamp > MOVE_INTERVAL) {
            if (moveDirection == Direction.LEFT || moveDirection == Direction.RIGHT) {
                lastHorizontalDirection = moveDirection;
            }

            currentSprite = determineMoveSprite();
            spriteToggle = !spriteToggle;
            lastMoveTimestamp = currentTime;

            executeMovement(battlefield);
        }
    }

    @Override
    public Image determineMoveSprite() {
        if (moveDirection == Direction.UP || moveDirection == Direction.DOWN) {
            return lastHorizontalDirection == Direction.LEFT ?
                    (spriteToggle ? idleLeft : moveLeft) : (spriteToggle ? idleRight : moveRight);
        } else {
            return moveDirection == Direction.LEFT ?
                    (spriteToggle ? idleLeft : moveLeft) : (spriteToggle ? idleRight : moveRight);
        }
    }

    @Override
    public void executeMovement(Battlefield battlefield) {
        int newX = positionX, newY = positionY;
        switch (moveDirection) {
            case LEFT: newX -= SPEED; break;
            case RIGHT: newX += SPEED; break;
            case UP: newY -= SPEED; break;
            case DOWN: newY += SPEED; break;
        }

        if (canMove(newX, newY, battlefield)) {
            positionX = newX;
            positionY = newY;
        } else {
            chooseNewDirection(battlefield);
        }
    }

    @Override
    public boolean canMove(int newX, int newY, Battlefield battlefield) {
        return withinBounds(newX, newY, battlefield) && battlefield.isPathOpen(newX, newY);
    }

    @Override
    public boolean withinBounds(int x, int y, Battlefield battlefield) {
        return x >= 0 && x + SIZE <= battlefield.getFieldWidth() &&
                y >= (Battlefield.SKY_LIMIT * Battlefield.BLOCK_SIZE) - SIZE &&
                y + SIZE <= battlefield.getFieldHeight() + 4;
    }

    @Override
    public void chooseNewDirection(Battlefield battlefield) {
        List<Direction> possibleDirections = new ArrayList<>();
        addPossibleDirections(possibleDirections);

        possibleDirections.removeIf(dir -> !canMoveInDirection(dir, battlefield));

        if (possibleDirections.isEmpty()) {
            moveDirection = getOppositeDirection(moveDirection);
        } else {
            Random random = new Random();
            moveDirection = possibleDirections.get(random.nextInt(possibleDirections.size()));
        }
    }

    private void addPossibleDirections(List<Direction> directions) {
        if (moveDirection != Direction.LEFT) directions.add(Direction.RIGHT);
        if (moveDirection != Direction.RIGHT) directions.add(Direction.LEFT);
        if (moveDirection != Direction.UP) directions.add(Direction.DOWN);
        if (moveDirection != Direction.DOWN) directions.add(Direction.UP);
    }

    private Direction getOppositeDirection(Direction currentDirection) {
        switch (currentDirection) {
            case LEFT: return Direction.RIGHT;
            case RIGHT: return Direction.LEFT;
            case UP: return Direction.DOWN;
            case DOWN: return Direction.UP;
            default: return currentDirection;
        }
    }

    private boolean canMoveInDirection(Direction direction, Battlefield battlefield) {
        int testX = positionX, testY = positionY;
        switch (direction) {
            case LEFT: testX -= SPEED; break;
            case RIGHT: testX += SPEED; break;
            case UP: testY -= SPEED; break;
            case DOWN: testY += SPEED; break;
        }
        return canMove(testX, testY, battlefield);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!showingScore) {
            gc.drawImage(currentSprite, positionX - 5, positionY - 5, SIZE, SIZE);
        } else {
            gc.drawImage(scoreImage, scorePosX, scorePosY, SIZE, SIZE);
        }
    }

    @Override
    public void initiateDyingSequence() {
        if (!expiring && !reviving) {
            expiring = true;
            perishStage = 0;
            lastPerishTime = System.currentTimeMillis();
        }
    }

    private void updatePerishSprite() {
        switch (perishStage) {
            case 1: currentSprite = perishStartLeft; break;
            case 2: currentSprite = perishStartMidLeft; break;
            case 3: currentSprite = perishEndMidLeft;
                scorePosX = positionX;
                scorePosY = positionY;
                showingScore = true;
                calculateScore(); break;
            case 4: currentSprite = perishEndLeft; break;
            default: currentSprite = idleLeft; break;
        }
    }

    private void updateRevivalSprite() {
        switch (perishStage) {
            case 3: currentSprite = perishEndMidLeft; break;
            case 2: currentSprite = perishStartMidLeft; break;
            case 1: currentSprite = perishStartLeft; break;
            case 0: currentSprite = idleLeft; break;
            default: currentSprite = idleLeft; break;
        }
    }

    private void calculateScore() {
        if (positionY <= Battlefield.SKY_LIMIT * Battlefield.BLOCK_SIZE) {
            scoreImage = gameControl.getScore200Texture();
            gameControl.addScore(200);
        } else if (positionY <= (Battlefield.SKY_LIMIT + Battlefield.MID_LIMIT) * Battlefield.BLOCK_SIZE) {
            scoreImage = gameControl.getScore300Texture();
            gameControl.addScore(300);
        } else {
            scoreImage = gameControl.getScore500Texture();
            gameControl.addScore(500);
        }
    }


    @Override
    public boolean collidesWith(Rectangle2D area) {
        return area.intersects(positionX, positionY, SIZE, SIZE);
    }

    @Override
    public boolean isEndOfDeathAnimation() {
        return expiring && perishStage >= 4;
    }
}
