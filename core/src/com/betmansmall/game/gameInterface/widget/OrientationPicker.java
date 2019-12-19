package com.betmansmall.game.gameInterface.widget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.betmansmall.util.OrientationEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.betmansmall.util.OrientationEnum.*;

/**
 * Used for selecting one of {@link OrientationEnum} from UI.
 * Selected value is handled with acceptor.
 *
 * @author Alexander on 25.11.2019.
 */
public class OrientationPicker extends Table {
    private ButtonGroup<CheckBox> group;
    private Map<OrientationEnum, CheckBox> map;
    private Consumer<OrientationEnum> acceptor;

    public OrientationPicker(Skin skin, Consumer<OrientationEnum> acceptor) {
        group = new ButtonGroup<>();
        map = new HashMap<>();
        this.acceptor = acceptor;
        createTable(skin);
    }

    private void createTable(Skin skin) {
        Table table = new Table();
        for (OrientationEnum orientation : values()) {
            CheckBox checkBox = new CheckBox(orientation.VALUE, skin);
            map.put(orientation, checkBox);
            group.add(checkBox);
            table.add(checkBox);
            checkBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    acceptor.accept(orientation);
                }
            });
        }
    }

    public void setChecked(OrientationEnum orientation) {
        map.get(orientation).setChecked(true);
    }
}
