package lab;

public class ScoreRecord {
    private String playerName;
    private int playerScore;

    public ScoreRecord(String name, int score) {
        this.playerName = name;
        this.playerScore = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerScore() {
        return playerScore;
    }
}
