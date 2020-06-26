package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.betmansmall.screens.menu.MapEditorScreen;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter;
import com.kotcrab.vis.ui.util.adapter.ListSelectionAdapter;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.ListView.ItemClickListener;
import com.kotcrab.vis.ui.widget.ListView.UpdatePolicy;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;


public class TestListView extends VisWindow {
    private static final Drawable white = VisUI.getSkin().getDrawable("white");

    private ColorPicker picker;
    public TestAdapter adapter;
    private Array<MapLayer> array;

    public TestListView(MapEditorScreen mapEditorScreen, Camera camera) {
        super("listview");

        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        addCloseButton();
        closeOnEscape();

        array = new Array<MapLayer>();
        updateLayersList(mapEditorScreen);

        adapter = new TestAdapter(array, mapEditorScreen, this);
        ListView<MapLayer> view = new ListView<MapLayer>(adapter);
        view.setUpdatePolicy(UpdatePolicy.ON_DRAW);

        VisTable footerTable = new VisTable();
        footerTable.addSeparator();
        footerTable.add("Table Footer");
        view.setFooter(footerTable);

        final Image image = new Image(white);
        picker = new ColorPicker("color picker", new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                image.setColor(newColor);
            }
        });

        final Color c = new Color(27 / 255.0f, 161 / 255.0f, 226 / 255.0f, 1);
        final Color newColorFromPicker = new Color(27 / 255.0f, 161 / 255.0f, 226 / 255.0f, 1);
        final VisValidatableTextField nameField = new VisValidatableTextField();
        VisTextButton addButton = new VisTextButton("Add");
        VisTextButton deleteButton = new VisTextButton("Delete");
        VisTextButton showPickerButton = new VisTextButton("show color picker");
        picker.setColor(c);
        image.setColor(c);

        SimpleFormValidator validator = new SimpleFormValidator(addButton);
        validator.notEmpty(nameField, "");

        add(new VisLabel("New Name:"));
        add(nameField);
        add(addButton);
        row();
        add(showPickerButton);
        add(image).size(32).pad(3);
        row();
        add(new VisLabel("Delete Name:"));
        add(deleteButton);
        row();
        add(view.getMainTable()).colspan(3).grow();

        addButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //by changing array using adapter view will be invalidated automatically
                MapLayer ml = new MapLayer();
                ml.setName(nameField.getText());
                adapter.add(ml);
                nameField.setText("");
            }
        });
        picker = new ColorPicker("color picker", new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                image.setColor(newColor);
                newColorFromPicker.set(newColor);
            }
        });


        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //by changing array using adapter view will be invalidated automatically
                if (adapter.getSelection().size > 0) {
                    adapter.removeValue(adapter.getSelection().get(adapter.getSelection().size - 1), false);
                    System.out.println("SiiiiiiiiiiiizzzzzzzzzzzzEEEEE" + adapter.getSelection().size);
                }
            }
        });
        showPickerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().addActor(picker.fadeIn());
            }
        });

        adapter.setSelectionMode(AbstractListAdapter.SelectionMode.SINGLE);
        view.setItemClickListener(new ItemClickListener<MapLayer>() {
            @Override
            public void clicked(MapLayer item) {
                System.out.println("Clicked: " + item.getName());
            }
        });
        adapter.getSelectionManager().setListener(new ListSelectionAdapter<MapLayer, VisTable>() {
            @Override
            public void selected(MapLayer item, VisTable view) {
                System.out.println("ListSelection Selected: " + item.getName());
            }

            @Override
            public void deselected(MapLayer item, VisTable view) {
                System.out.println("ListSelection Deselected: " + item.getName());
            }
        });

        setSize(500, 300);
        setPosition(458, 245);
    }

    public void updateLayersList(MapEditorScreen mapEditorScreen) {
        array.clear();
        for (MapLayer mapLayer : mapEditorScreen.map.getLayers()) {
            array.add(mapLayer);
        }
    }
}
