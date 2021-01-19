package com.betmansmall.game.gameInterface.mapeditor;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;


public class TestTabbedPane extends VisWindow {


    TiledMap tiledMap;
    public TestTabbedPane (TiledMap tiledMap, boolean vertical) {
        super("tabbed pane");

        TableUtils.setSpacingDefaults(this);

        this.tiledMap = tiledMap;//afk

        setResizable(true);
//        addCloseButton();
        closeOnEscape();

        final VisTable container = new VisTable();

        TabbedPane.TabbedPaneStyle style = VisUI.getSkin().get(vertical ? "vertical" : "default", TabbedPane.TabbedPaneStyle.class);
        TabbedPane tabbedPane = new TabbedPane(style);
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab (Tab tab) {
                container.clearChildren();
                container.add(tab.getContentTable()).expand().fill();
            }
        });

        if (style.vertical) {
            top();
            defaults().top();
            add(tabbedPane.getTable()).growY();
            add(container).expand().fill();
        } else {
            add(tabbedPane.getTable()).expandX().fillX();
            row();
            add(container).expand().fill();
        }


        for (TiledMapTileSet tiledMapTileSet : tiledMap.getTileSets()){
            tabbedPane.add(new TestTab(tiledMapTileSet.getName()));
        }

        Tab tab = new TestTab("huita");
        tabbedPane.add(tab);
        tabbedPane.disableTab(tab, true);

//		debugAll();
        setSize(300, 200);
        centerWindow();
    }

    private class TestTab extends Tab {
        private String title;
        private Table content;

        public TestTab (String title) {
            super(false, true);
            this.title = title;

            content = new VisTable();
            content.add(new VisLabel(title));
        }

        @Override
        public String getTabTitle () {
            return title;
        }

        @Override
        public Table getContentTable () {
            return content;
        }
    }
}
