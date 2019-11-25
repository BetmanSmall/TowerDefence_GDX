package com.betmansmall.game.gameInterface.widget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.betmansmall.util.OrientationEnum;
import com.betmansmall.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.betmansmall.util.OrientationEnum.*;

/**
 * Used for selecting one of {@link OrientationEnum} from UI.
 * Selected value is handled with acceptor.
 *
 * @author Alexander on 25.11.2019.
 */
public class OrientationPicker extends Table {
    private ButtonGroup<CheckBox> group;
    private List<Pair<OrientationEnum, CheckBox>> list;
    private OrientationAcceptor acceptor;

    public OrientationPicker(Skin skin, OrientationAcceptor acceptor) {
        list = new ArrayList<>();
        this.acceptor = acceptor;
        createTable(skin);
    }

    private void createTable(Skin skin) {
        Table table = new Table();
        list.add(new Pair<>(UP, new CheckBox("Up", skin)));
        list.add(new Pair<>(DOWN, new CheckBox("Down", skin)));
        list.add(new Pair<>(LEFT, new CheckBox("Left", skin)));
        list.add(new Pair<>(RIGHT, new CheckBox("Right", skin)));
        for (Pair<OrientationEnum, CheckBox> pair : list) {
            group.add(pair.getValue());
            table.add(pair.getValue());
            pair.getValue().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    acceptor.accept(pair.getKey());
                }
            });
        }
    }

    private static abstract class OrientationAcceptor {
        public abstract void accept(OrientationEnum orientation);
    }
}
