package com.mygdx.game.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.controllers.actors.ActorsFactory;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.view.stages.MainGameStage;
import com.mygdx.game.view.stages.MenuStage;
import com.mygdx.game.db.DBController;
import com.mygdx.game.db.GameDatabase;
import com.mygdx.game.model.GamingProcess;
import com.mygdx.game.model.maps.Border;
import com.mygdx.game.model.players.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtofWar extends Game {

    public SpriteBatch batch;
    public MainGameStage mainGameStage;
    public MenuStage menuStage;
    public ActorsFactory factory;
    public GameDatabase gameDatabase;

    public ArtofWar() {
        super();
        try {
            DBController dbController = new DBController("artofwar.db");
            dbController.openConnection();
            gameDatabase = new GameDatabase(dbController.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void create() {
        //Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        batch = new SpriteBatch();
        factory = new ActorsFactory();
        menuStage = new MenuStage(this);
        factory.setMenuStage(menuStage);
        this.setScreen(menuStage);
    }
    public void newGame(int width, int height, List<String> playersNames) throws SQLException {
        GameMap gameMap = new GameMap(width,height);
        List<Player> players = new ArrayList<>();
        for(int i = 0; i<playersNames.size();++i){
            int id = gameDatabase.insertPlayerAndGetId(playersNames.get(i));
            Player player = new Player(id, playersNames.get(i),Border.get(i));
            players.add(player);
        }
        int gameId = gameDatabase.insertGame(players.size(), gameMap.getMapCreator().getSeed(), width, height);
        GamingProcess gamingProcess = new GamingProcess(gameMap, gameDatabase, players, gameId);
        mainGameStage = new MainGameStage(gameMap, gamingProcess, this);
        setScreen(mainGameStage);
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
    }

}