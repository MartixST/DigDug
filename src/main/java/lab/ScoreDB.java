package lab;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDB {

    public ScoreDB() {
        initializeDatabase();
        insertNewScore(new ScoreRecord("PlayerName", 200));
        retrieveAllScores();
    }

    public List<ScoreRecord> retrieveAllScores() {
        List<ScoreRecord> scores = new ArrayList<>();
        String sqlQuery = "SELECT name, score_value FROM score";

        try (Connection conn = createConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {

            while (resultSet.next()) {
                scores.add(new ScoreRecord(resultSet.getString("name"), resultSet.getInt("score_value")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scores;
    }

    public void insertNewScore(ScoreRecord score) {
        String sqlInsert = "INSERT INTO score (name, score_value) VALUES (?, ?)";

        try (Connection conn = createConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sqlInsert)) {

            preparedStatement.setString(1, score.getPlayerName());
            preparedStatement.setInt(2, score.getPlayerScore());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        String sqlCreateTable = "CREATE TABLE score (" +
                "id INT NOT NULL GENERATED ALWAYS AS IDENTITY," +
                "score_value INT NOT NULL," +
                "name VARCHAR(255) NOT NULL," +
                "PRIMARY KEY (id)" +
                ")";

        try (Connection conn = createConnection()) {
            try (Statement st = conn.createStatement()) {
                st.execute(sqlCreateTable);
            } catch (SQLException e) {
                if(!e.getSQLState().equals("X0Y32")) {
                    throw e;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:derby:memory:scoreDB;create=true");
    }
}

