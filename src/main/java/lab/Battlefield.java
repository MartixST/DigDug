package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;

public class Battlefield {
    private Image skyTexture;
    private Image undergroundTexture;
    private Image textureLightSand, textureLightBrownSand, textureDarkBrownSand, textureDarkSand;
    private Image flowerDecoration;
    private ArrayList<SandBlock> sandBlocks;

    private boolean[][] excavatedSandBlocks;
    private static final int WIDTH = 15;
    private static final int HEIGHT = 15;
    public static final int BLOCK_SIZE = 40;
    public static final int SKY_LIMIT = HEIGHT / 5;
    public static final int MID_LIMIT = HEIGHT / 3;
    private double pixelWidth = 9;
    private double pixelHeight = 9;

    public Battlefield() {
        skyTexture = new Image(getClass().getResourceAsStream("/background/sky.png"));
        undergroundTexture = new Image(getClass().getResourceAsStream("/background/underground.png"));
        textureLightSand = new Image(getClass().getResourceAsStream("/sand/light_sand.png"));
        textureLightBrownSand = new Image(getClass().getResourceAsStream("/sand/light_brown_sand.png"));
        textureDarkBrownSand = new Image(getClass().getResourceAsStream("/sand/dark_brown_sand.png"));
        textureDarkSand = new Image(getClass().getResourceAsStream("/sand/dark_sand.png"));
        flowerDecoration = new Image(getClass().getResourceAsStream("/background/flower.png"));
        setupBattlefieldTiles();

        excavatedSandBlocks = new boolean[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                excavatedSandBlocks[i][j] = false;
            }
        }
    }

    private void setupBattlefieldTiles() {
        sandBlocks = new ArrayList<>();

        for (int posX = 0; posX < WIDTH * BLOCK_SIZE; posX += pixelWidth) {
            for (int posY = SKY_LIMIT * BLOCK_SIZE; posY < HEIGHT * BLOCK_SIZE; posY += pixelHeight) {
                Image selectedTexture;
                boolean addTile = true;

                if (isSpecialTile(posX, posY)) {
                    addTile = false;
                }

                if (addTile) {
                    if (posY <= SKY_LIMIT * BLOCK_SIZE + 85) {
                        selectedTexture = textureLightSand;
                    } else if (posY <= MID_LIMIT * BLOCK_SIZE + 120) {
                        selectedTexture = textureLightBrownSand;
                    } else if (posY <= (MID_LIMIT + SKY_LIMIT) * BLOCK_SIZE + 120) {
                        selectedTexture = textureDarkBrownSand;
                    } else {
                        selectedTexture = textureDarkSand;
                    }

                    sandBlocks.add(new SandBlock(posX, posY, selectedTexture));
                }
            }
        }
    }

    private boolean isSpecialTile(int posX, int posY) {
        boolean[] specialAreas = {
                posX <= 160 && posX >= 40 && posY <= 280 && posY >= 240,   //left top pooka
                posX <= 560 && posX >= 440 && posY <= 200 && posY >= 160, // right top fygar
                posX <= 360 && posX >= 210 && posY <= 385 && posY >= 340, // player
                posX <= 315 && posX >= 260 && posY <= 385 && posY >= 0,   // player column
                posX <= 480 && posX >= 360 && posY <= 520 && posY >= 480, // right bottom fygar
                posX <= 400 && posX >= 360 && posY <= 320 && posY >= 200, // right top pooka
                posX <= 240 && posX >= 200 && posY <= 520 && posY >= 400  // right top pooka
        };

        for (boolean area : specialAreas) {
            if (area) return true;
        }

        return false;
    }

    public void drawBackground(GraphicsContext gc) {
        ImagePattern skyPattern = new ImagePattern(skyTexture, 0, 0, skyTexture.getWidth(), skyTexture.getHeight(), false);
        gc.setFill(skyPattern);
        gc.fillRect(0, 0, WIDTH * BLOCK_SIZE, SKY_LIMIT * BLOCK_SIZE);

        ImagePattern undergroundPattern = new ImagePattern(undergroundTexture, 0, 0, undergroundTexture.getWidth(), undergroundTexture.getHeight(), true);
        gc.setFill(undergroundPattern);
        gc.fillRect(0, SKY_LIMIT * BLOCK_SIZE, WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);

        gc.drawImage(flowerDecoration, 550, 90, 30, 30);
    }

    public void draw(GraphicsContext gc) {
        drawBackground(gc);
        for (SandBlock block : sandBlocks) {
            gc.drawImage(block.getTexture(), block.getX(), block.getY(), pixelWidth, pixelHeight);
        }
    }

    public boolean isPathOpen(int x, int y) {
        int centerX = x + 30;
        int centerY = y + 30;

        for (SandBlock block : sandBlocks) {
            if (centerX > block.getX() && centerX < block.getX() + BLOCK_SIZE &&
                    centerY > block.getY() && centerY < block.getY() + BLOCK_SIZE) {
                return false;
            }
        }
        return true;
    }

    public void removeSandBlockAt(int minerX, int minerY) {
        int minerLeft = minerX;
        int minerRight = minerX + BLOCK_SIZE;
        int minerTop = minerY;
        int minerBottom = minerY + BLOCK_SIZE;

        sandBlocks.removeIf(block ->
                minerRight > block.getX() &&
                        minerLeft < block.getX() + pixelWidth &&
                        minerBottom > block.getY() &&
                        minerTop < block.getY() + pixelHeight
        );

        int blockX = (int) Math.floor(minerX / BLOCK_SIZE);
        int blockY = (int) Math.floor(minerY / BLOCK_SIZE);

        if (blockX >= 0 && blockX < WIDTH && blockY >= 0 && blockY < HEIGHT) {
            excavatedSandBlocks[blockX][blockY] = true;
        }
    }

    public int getFieldWidth() {
        return WIDTH * BLOCK_SIZE;
    }

    public int getFieldHeight() {
        return HEIGHT * BLOCK_SIZE;
    }
}
