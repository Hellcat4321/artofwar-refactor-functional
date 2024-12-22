package com.mygdx.game.db;

import com.mygdx.game.model.players.Player;
import com.mygdx.game.model.players.PlayerStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameDatabase {
    private final Connection connection;

    public GameDatabase(Connection connection) throws SQLException {
        this.connection = connection;
        createSchema();
    }

    /**
     * Creates the schema for game
     *
     * @throws SQLException
     */
    public void createSchema() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS player" +
                        "(" +
                        "    id   INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "    name VARCHAR(63) NOT NULL UNIQUE" +
                        ");"
        );
        statement.execute(
                "CREATE TABLE IF NOT EXISTS game" +
                        "(" +
                        "    id              INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "    players_qty     INTEGER NOT NULL," +
                        "    start_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "    end_timestamp   DATETIME," +
                        "    map_seed        INTEGER NOT NULL," +
                        "    map_width       INTEGER NOT NULL," +
                        "    map_height      INTEGER NOT NULL" +
                        ");"
        );
        statement.execute(
                "CREATE TABLE IF NOT EXISTS turn" +
                        "(" +
                        "    id                INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "    current_player_id INTEGER NOT NULL," +
                        "    game_id           INTEGER NOT NULL," +
                        "    round             INTEGER NOT NULL ," +
                        "    gold              INTEGER NOT NULL," +
                        "    territories       INTEGER NOT NULL," +
                        "    timestamp         DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "    FOREIGN KEY (current_player_id) REFERENCES player (id)," +
                        "    FOREIGN KEY (game_id) REFERENCES game (id) ON DELETE CASCADE" +
                        ");"
        );
        connection.commit();
        statement.close();
    }

//    /**
//     * Adding players to the player table.
//     * <p>
//     * Each player receive id.
//     * </p>
//     *
//     * @param players List of players
//     * @throws SQLException
//     */
//    public void insertPlayers(List<Player> players) throws SQLException {
//        PreparedStatement statement = connection.prepareStatement(
//                "INSERT OR IGNORE INTO player (name) VALUES (?) RETURNING id");
//        PreparedStatement getIdStatement = connection.prepareStatement(
//                "SELECT id FROM player WHERE name = ?"
//        );
//        for (Player player : players) {
//            statement.setString(1, player.name);
//            ResultSet rs = statement.executeQuery();
//            if (!rs.next()) {
//                getIdStatement.setString(1, player.name);
//                rs = getIdStatement.executeQuery();
//            }
//            player.setId(rs.getInt(1));
//        }
//        statement.close();
//        getIdStatement.close();
//        connection.commit();
//    }

    public int insertPlayerAndGetId(String name) throws SQLException {
        int id;
        PreparedStatement statement = connection.prepareStatement(
                "INSERT OR IGNORE INTO player (name) VALUES (?) RETURNING id");
        PreparedStatement getIdStatement = connection.prepareStatement(
                "SELECT id FROM player WHERE name = ?"
        );
        statement.setString(1, name);
        ResultSet rs = statement.executeQuery();
        if (!rs.next()) {
            getIdStatement.setString(1, name);
            rs = getIdStatement.executeQuery();
        }
        id = rs.getInt(1);
        statement.close();
        getIdStatement.close();
        connection.commit();
        return id;
    }

    /**
     * Adding game to the player table.
     *
     * @param playerQty number of players in the game
     * @param seed      map seed
     * @param mapWidth  map width
     * @param mapHeight map height
     * @return id of the game
     * @throws SQLException
     */
    public int insertGame(int playerQty, long seed, int mapWidth, int mapHeight) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO game (players_qty, map_seed, map_width, map_height) VALUES (?, ?, ?, ?) RETURNING id"
        );
        statement.setInt(1, playerQty);
        statement.setInt(2, (int) seed);
        statement.setInt(3, mapWidth);
        statement.setInt(4, mapHeight);
        ResultSet rs = statement.executeQuery();
        int gameId = rs.getInt(1);
        statement.close();
        connection.commit();
        return gameId;
    }

    /**
     * Adding turn to the turn table.
     *
     * @param playerId    current playerId
     * @param gameId      game id
     * @param round       current round in the game
     * @param gold        amount of gold at the start of the turn
     * @param territories amount of territories at the start of the turn
     * @return id of the new turn
     * @throws SQLException
     */
    public int insertTurn(int playerId, int gameId, int round, int gold, int territories) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO turn (current_player_id, game_id, round, gold, territories) VALUES (?, ?, ?, ?, ?) RETURNING id"
        );
        statement.setInt(1, playerId);
        statement.setInt(2, gameId);
        statement.setInt(3, round);
        statement.setInt(4, gold);
        statement.setInt(5, territories);
        ResultSet rs = statement.executeQuery();
        int turnId = rs.getInt(1);
        statement.close();
        connection.commit();
        return turnId;
    }

    /**
     * Ends the game. Write endTimestamp to the game table.
     *
     * @param gameId game id
     * @throws SQLException
     */
    public void finishGame(int gameId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE game SET end_timestamp = CURRENT_TIMESTAMP WHERE id = ?"
        );
        statement.setInt(1, gameId);
        statement.executeUpdate();
        connection.commit();
    }

    /**
     * Get number of the round where player finished the game
     *
     * @param playerId player id
     * @return Number of the round; 0 if player wins the game.
     * @throws SQLException
     */
    public int getFinishRound(int playerId, int gameId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT round FROM turn WHERE game_id = ? AND current_player_id = ? AND territories = 0"
        );
        statement.setInt(1, playerId);
        statement.setInt(2, gameId);
        ResultSet rs = statement.executeQuery();
        return rs.getInt(1);
    }

    /**
     * Get gameDuration
     *
     * @param gameId game id
     * @return time formatted "mm:ss"
     * @throws SQLException
     */
    public String getGameDuration(int gameId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT start_timestamp, end_timestamp FROM game WHERE id = ?"
        );

        statement.setInt(1, gameId);

        ResultSet rs = statement.executeQuery();

        Timestamp startGameTimestamp = rs.getTimestamp(1);
        Timestamp endGameTimestamp = rs.getTimestamp(2);

        long timedelta = endGameTimestamp.getTime() - startGameTimestamp.getTime();

        LocalTime gameTime = new Time(timedelta).toLocalTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
        return gameTime.format(formatter);
    }

    /**
     * Get game over statistics of players
     *
     * <p>Players ordered from winner to losers</p>
     *
     * @param gameId game id
     * @return List of player records.
     * @throws SQLException
     */
    public ArrayList<PlayerStats> getGameOverPlayerStats(int gameId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT name, max(territories) as 'max terrs', sum(gold) as 'total gold', max(round) - 1 as 'last round' " +
                        "FROM turn " +
                        "         JOIN player p on p.id = turn.current_player_id " +
                        "WHERE game_id = ? " +
                        "GROUP BY current_player_id " +
                        "ORDER BY \"last round\" DESC;"
        );
        statement.setInt(1, gameId);
        ResultSet rs = statement.executeQuery();
        ArrayList<PlayerStats> playersStats = new ArrayList<>();
        while (rs.next()) {
            playersStats.add(new PlayerStats(
                    rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4))
            );
        }
        return playersStats;
    }
}
