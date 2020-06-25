package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.maps.TmxMap;
import com.betmansmall.screens.menu.MapEditorScreen;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.Comparator;

class TestAdapter extends ArrayAdapter<MapLayer, VisTable> {
    private final Drawable bg = VisUI.getSkin().getDrawable("window-bg");
    private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");
    MapEditorScreen mapEditorScreen;
    TestListView testListView;

    public TestAdapter (Array<MapLayer> array, MapEditorScreen mapEditorScreen, TestListView testListView) {
        super(array);
        this.testListView = testListView;
        setSelectionMode(SelectionMode.SINGLE);
        this.mapEditorScreen = mapEditorScreen;

        setItemsSorter(new Comparator<MapLayer>() {
            @Override
            public int compare (MapLayer o1, MapLayer o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
    }

    @Override
    protected VisTable createView (MapLayer item) {
        VisCheckBox visCheckBox = new VisCheckBox("");

        visCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println(mapEditorScreen.map.getLayers().get(0).isVisible());
                mapEditorScreen.map.getLayers().get(testListView.adapter.getSelection().get(0).getName()).setVisible(visCheckBox.isChecked());
                //map.getLayers().get(map.getLayers().getCount()-1).setVisible(false);
                System.out.println(mapEditorScreen.map.getLayers().get(0).isVisible());
            }
        });


        VisCheckBox visCheckBoxSecond = new VisCheckBox("");

        VisLabel label = new VisLabel(item.getName());

        VisTable table = new VisTable();
        table.left();
        table.add(label);
        table.add(visCheckBox).right().expandX();
        table.add(visCheckBoxSecond).right();


        return table;
    }

    @Override
    protected void updateView (VisTable view, MapLayer item) {
        super.updateView(view, item);
    }

    @Override
    protected void selectView (VisTable view) {
        view.setBackground(selection);
    }

    @Override
    protected void deselectView (VisTable view) {
        view.setBackground(bg);
    }
}
