package lab;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameControl {
    private Battlefield battlefield;
    private Image score200, score300, score500;
    private Miner miner;
    private Canvas canvas;
    private List<Adversary> adversaries;
    private Image lifeIcon;
    private ScoreDB scoreDB;
    Button saveScoresButton = new Button("Save Scores");

    private int totalScore = 0;
    private boolean gameEnded = false;
    private String playerName;

    public GameControl() {}

    public GameControl(Canvas canvas, String playerName) {
        this.canvas = canvas;
        this.playerName = playerName;
        this.battlefield = new Battlefield();
        this.adversaries = new ArrayList<>();

        adversaries.add(new Baloon(80, 240, this));
        adversaries.add(new Dragon(480, 160, this));
        adversaries.add(new Dragon(400, 480, this));
        adversaries.add(new Baloon(360, 240, this));
        adversaries.add(new Baloon(200, 440, this));

        lifeIcon = new Image(getClass().getResourceAsStream("/character/idle_right.png"));
        score200 = new Image(getClass().getResourceAsStream("/background/score_200.png"));
        score300 = new Image(getClass().getResourceAsStream("/background/score_300.png"));
        score500 = new Image(getClass().getResourceAsStream("/background/score_500.png"));

        scoreDB = new ScoreDB();
    }

    public void gameUpdate() {
        if (!miner.isAlive()) {
            return;
        }

        miner.update(battlefield, adversaries);

        Iterator<Adversary> adversaryIterator = adversaries.iterator();
        while (adversaryIterator.hasNext()) {
            Adversary adversary = adversaryIterator.next();
            adversary.performUpdate(battlefield);

            if (adversary.isEndOfDeathAnimation()) {
                adversaryIterator.remove();
            }
        }

        if (miner.isPumping()) {
            checkPumpCollision();
        }
    }

    private void checkPumpCollision() {
        if (miner.getPump() != null) {
            Rectangle2D pumpArea = miner.getPump().getArea();

            for (Adversary adversary : adversaries) {
                if (adversary.collidesWith(pumpArea)) {
                    adversary.initiateDyingSequence();
                }
            }
        }
    }



    public void render(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        battlefield.draw(gc);
        for (int i = 0; i < miner.getLives(); i++) {
            gc.drawImage(lifeIcon, 500 + (i * 25), 10, 40, 40);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 50));
        gc.fillText("Score: " + totalScore, (canvas.getWidth() - 170) / 2, 50);

        if (miner.isAlive()) {
            miner.render(gc);
            for (Adversary adversary : adversaries) {
                adversary.render(gc);
            }
        } else {
            displayGameOver(gc);
        }
    }

    private void savePlayerScore() {
        scoreDB.insertNewScore(new ScoreRecord(playerName, totalScore));
        displayScoreTable();
        Platform.runLater(() -> ((Group) canvas.getScene().getRoot()).getChildren().remove(saveScoresButton));
    }

    private void displayGameOver(GraphicsContext gc) {
        if (!gameEnded) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 50));
            gc.fillText("Game Over", (canvas.getWidth() + 50) / 4, (canvas.getHeight() + 50) / 2);

            Platform.runLater(() -> {
                Group root = (Group) canvas.getScene().getRoot();
                if (!root.getChildren().contains(saveScoresButton)) {
                    createSaveScoresButton();
                }
            });

            gameEnded = true;
        }
    }

    private void createSaveScoresButton() {
        saveScoresButton.setLayoutX((canvas.getWidth() - saveScoresButton.getWidth()) / 2 - 30);
        saveScoresButton.setLayoutY((canvas.getHeight() + 50) / 2 + 50);
        saveScoresButton.setOnAction(event -> savePlayerScore());

        Platform.runLater(() -> ((Group) canvas.getScene().getRoot()).getChildren().add(saveScoresButton));
    }

    private void displayScoreTable() {
        TableView<ScoreRecord> scoreTable = new TableView<>();

        TableColumn<ScoreRecord, String> nameColumn = new TableColumn<>("Player Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));

        TableColumn<ScoreRecord, Integer> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("playerScore"));

        scoreTable.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue() / 2;
            nameColumn.setPrefWidth(width);
            scoreColumn.setPrefWidth(width);
        });

        scoreTable.getColumns().addAll(nameColumn, scoreColumn);

        scoreTable.setPrefWidth(canvas.getWidth());
        scoreTable.setPrefHeight(canvas.getHeight());

        scoreTable.getItems().addAll(scoreDB.retrieveAllScores());

        Platform.runLater(() -> {
            Group root = (Group) canvas.getScene().getRoot();
            root.getChildren().add(scoreTable);
        });
    }

    public void addScore(int score) {
        totalScore += score;
    }

    public Miner getMiner() {
        return miner;
    }

    public void setMiner(Miner miner) {
        this.miner = miner;
    }

    public Image getScore200Texture() {
        return score200;
    }

    public Image getScore300Texture() {
        return score300;
    }

    public Image getScore500Texture() {
        return score500;
    }
}
