package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.util.adapter.ListSelectionAdapter;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.ListView.ItemClickListener;
import com.kotcrab.vis.ui.widget.ListView.UpdatePolicy;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

import java.util.Comparator;

public class TestListView extends VisWindow {
    private static final Drawable white = VisUI.getSkin().getDrawable("white");

    private ColorPicker picker;

    public TestListView () {
        super("listview");

        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        addCloseButton();
        closeOnEscape();

        Array<Model> array = new Array<Model>();
        for (int i = 1; i <= 3; i++) {
            array.add(new Model("Windows" + i, VisUI.getSkin().getColor("vis-red")));
            array.add(new Model("Linux" + i, Color.GREEN));
            array.add(new Model("OSX" + i, Color.WHITE));
        }

        final TestAdapter adapter = new TestAdapter(array);
        ListView<Model> view = new ListView<Model>(adapter);
        view.setUpdatePolicy(UpdatePolicy.ON_DRAW);

        VisTable footerTable = new VisTable();
        footerTable.addSeparator();
        footerTable.add("Table Footer");
        view.setFooter(footerTable);

        final Image image = new Image(white);
        picker = new ColorPicker("color picker", new ColorPickerAdapter() {
            @Override
            public void finished (Color newColor) {
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
            public void changed (ChangeEvent event, Actor actor) {
                //by changing array using adapter view will be invalidated automatically
                adapter.add(new Model(nameField.getText(), newColorFromPicker));
                nameField.setText("");
            }
        });
        picker = new ColorPicker("color picker", new ColorPickerAdapter() {
            @Override
            public void finished (Color newColor) {
                image.setColor(newColor);
                newColorFromPicker.set(newColor);
            }
        });


        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                //by changing array using adapter view will be invalidated automatically

                adapter.removeValue(adapter.getSelection().get(adapter.getSelection().size - 1), false);
            }
        });
        showPickerButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                getStage().addActor(picker.fadeIn());
            }
        });

        adapter.setSelectionMode(AbstractListAdapter.SelectionMode.SINGLE);
        view.setItemClickListener(new ItemClickListener<Model>() {
            @Override
            public void clicked (Model item) {
                System.out.println("Clicked: " + item.name);
            }
        });
        adapter.getSelectionManager().setListener(new ListSelectionAdapter<Model, VisTable>() {
            @Override
            public void selected (Model item, VisTable view) {
                System.out.println("ListSelection Selected: " + item.name);
            }

            @Override
            public void deselected (Model item, VisTable view) {
                System.out.println("ListSelection Deselected: " + item.name);
            }
        });

        setSize(500, 300);
        setPosition(458, 245);
    }

    private static class Model {
        public String name;
        public Color color;

        public Model (String name, Color color) {
            this.name = name;
            this.color = color;
        }
    }

    private static class TestAdapter extends ArrayAdapter<Model, VisTable> {
        private final Drawable bg = VisUI.getSkin().getDrawable("window-bg");
        private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");

        public TestAdapter (Array<Model> array) {
            super(array);
            setSelectionMode(SelectionMode.SINGLE);

            setItemsSorter(new Comparator<Model>() {
                @Override
                public int compare (Model o1, Model o2) {
                    return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
                }
            });
        }

        @Override
        protected VisTable createView (Model item) {
            VisCheckBox visCheckBox = new VisCheckBox("");
            visCheckBox.setColor(item.color);

            VisCheckBox visCheckBoxSecond = new VisCheckBox("");
            visCheckBoxSecond.setColor(item.color);

            VisLabel label = new VisLabel(item.name);
            label.setColor(item.color);

            VisTable table = new VisTable();
            table.left();
            table.add(label);
            table.add(visCheckBox).right().expandX();
            table.add(visCheckBoxSecond).right();


            return table;
        }

        @Override
        protected void updateView (VisTable view, Model item) {
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
}
