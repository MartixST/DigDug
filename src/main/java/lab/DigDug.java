package lab;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class DigDug extends Application {
    private Canvas gameCanvas;
    private GameControl gameControl;
    private Miner miner;
    private TextField nameInputField;
    private boolean goingLeft, goingRight, ascending, descending;
    private Button startButton;

    @Override
    public void start(Stage gameStage) {
        try {
            Group sceneRoot = new Group();
            ImageView background = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/background/splash_screen.png"))));
            background.setFitWidth(600);
            background.setFitHeight(600);
            sceneRoot.getChildren().add(background);

            gameCanvas = new Canvas(600, 600);
            gameCanvas.setMouseTransparent(true);
            sceneRoot.getChildren().add(gameCanvas);

            nameInputField = new TextField();
            nameInputField.setLayoutX(220);
            nameInputField.setLayoutY(340);
            sceneRoot.getChildren().add(nameInputField);

            startButton = new Button("Start");
            startButton.setLayoutX(275);
            startButton.setLayoutY(370);
            sceneRoot.getChildren().add(startButton);

            startButton.setOnAction(event -> {
                String playerName = nameInputField.getText();
                startGame(playerName);
                sceneRoot.getChildren().remove(startButton);
                sceneRoot.getChildren().remove(nameInputField);
                sceneRoot.getChildren().remove(background);
            });

            Scene gameScene = new Scene(sceneRoot, 600, 600);
            gameStage.setScene(gameScene);
            gameStage.setTitle("Dig Dug");
            gameStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGame(String playerName) {
        Battlefield battlefield = new Battlefield();
        gameControl = new GameControl(gameCanvas, playerName);

        miner = new Miner(280, 350, goingLeft, goingRight, ascending, descending, battlefield, gameControl);
        gameControl.setMiner(miner);

        gameCanvas.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                miner.initiatePump();
            }
            processKeyPressed(e.getCode());
        });

        gameCanvas.getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                miner.stopPump();
            }
            processKeyReleased(e.getCode());
        });

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                refreshGame();
                gameControl.gameUpdate();
                gameControl.render(gameCanvas.getGraphicsContext2D());
            }
        }.start();
    }

    private void processKeyPressed(KeyCode code) {
        if (miner.isPumping()) return;
        switch (code) {
            case LEFT:
                goingLeft = true;
                break;
            case RIGHT:
                goingRight = true;
                break;
            case UP:
                ascending = true;
                break;
            case DOWN:
                descending = true;
                break;
            case SPACE:
                miner.initiatePump();
                goingLeft = goingRight = ascending = descending = false;
                break;
            default:
                break;
        }
    }

    private void processKeyReleased(KeyCode code) {
        if (miner.isPumping() && code != KeyCode.SPACE) return;
        switch (code) {
            case LEFT:
                goingLeft = false;
                break;
            case RIGHT:
                goingRight = false;
                break;
            case UP:
                ascending = false;
                break;
            case DOWN:
                descending = false;
                break;
            case SPACE:
                miner.stopPump();
                break;
            default:
                break;
        }
        miner.halt();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void refreshGame() {
        Miner activeMiner = gameControl.getMiner();
        if (activeMiner != null) {
            if (goingLeft) activeMiner.moveLeft();
            if (goingRight) activeMiner.moveRight();
            if (ascending) activeMiner.moveUp();
            if (descending) activeMiner.moveDown();
        }
    }
}
