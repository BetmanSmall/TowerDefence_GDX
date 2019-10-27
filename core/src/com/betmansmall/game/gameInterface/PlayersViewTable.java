package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.betmansmall.game.Player;
import com.betmansmall.game.PlayersManager;
import com.betmansmall.util.logging.Logger;

public class PlayersViewTable extends Table {
    public PlayersManager playersManager;
    public ScrollPane scrollPane; // pizdec

    public PlayersViewTable(PlayersManager playersManager, Skin skin) {
        super(skin);
        this.playersManager = playersManager;
        this.scrollPane = new ScrollPane(this);
        scrollPane.setFillParent(true);
        setDebug(true, true);
        updateView();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateView();
        }
    }

    public void updateView() {
        Logger.logFuncStart(playersManager.getPlayers().toString());
        clear();
        for (Player player : playersManager.getPlayers()) {
            if (player != null) {
                Logger.logDebug("player:" + player);
                Table table = new Table();

                table.add(new Label("connection:" + ((player.connection != null) ? player.connection.getSocketIP() : null), getSkin())).row();

                Label typeLabel = new Label("type:" + player.type, getSkin());
                table.add(typeLabel).row();
                Label playerIDLabel = new Label("playerID:" + player.playerID, getSkin());
                table.add(playerIDLabel).row();
                Label nameLabel = new Label("name:" + player.name, getSkin());
                table.add(nameLabel).row();

                table.add(new Label("factionName:" + ((player.faction != null) ? player.faction.getName() : null), getSkin())).row();
                table.add(new Label("gold:" + player.gold, getSkin())).row();

                table.add(new Label("cellSpawnHero:" + player.cellSpawnHero, getSkin())).row();
                table.add(new Label("cellExitHero:" + player.cellExitHero, getSkin())).row();
                table.add(new Label("maxOfMissedUnits:" + player.maxOfMissedUnits, getSkin())).row();
                table.add(new Label("missedUnits:" + player.missedUnits, getSkin())).row();
                add(table);
            }
        }
    }
}
