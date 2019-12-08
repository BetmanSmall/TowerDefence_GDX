package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.game.Player;
import com.betmansmall.game.PlayersManager;

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
        scrollPane.setVisible(visible);
        if (visible) {
            updateView();
        }
    }

    public void updateView() {
        clear();
        Array<Player> players = playersManager.getPlayers();
        for (int p = 0; p < players.size; p++) {
            Player player = players.get(p);
//        for (Player player : playersManager.getPlayers()) {
            if (player != null) {
                Table table = new Table();

                if (player.connection != null) {
                    table.add(new Label("connection:" + player.connection.getSocketIP(), getSkin())).row();
                }

                table.add(new Label("type:" + player.type, getSkin())).row();
                table.add(new Label("accountID:" + player.accountID, getSkin())).row();
                table.add(new Label("playerID:" + player.playerID, getSkin())).row();
                table.add(new Label("name:" + player.name, getSkin())).row();

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
