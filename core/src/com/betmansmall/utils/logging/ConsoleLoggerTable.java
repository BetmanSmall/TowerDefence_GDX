package com.betmansmall.utils.logging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class ConsoleLoggerTable extends VisTable implements Disposable {
    private Array<String> arrayActionsHistory;
    private float deleteActionThrough, actionInHistoryTime;
    private VisLabel actionsHistoryLabel;

    private static ConsoleLoggerTable instance = null;

    public static ConsoleLoggerTable instance() {
        if (instance == null) instance = new ConsoleLoggerTable();
        return instance;
    }

    public ConsoleLoggerTable() {
        super();
        this.arrayActionsHistory = new Array<String>();
        this.arrayActionsHistory.add("actionsHistoryLabel");
        this.deleteActionThrough = 0f;
        this.actionInHistoryTime = 1f;
        this.actionsHistoryLabel = new VisLabel("actionsHistoryLabel", Color.WHITE);

        setFillParent(true);
        add(actionsHistoryLabel).expand().left();
    }

    @Override
    public void dispose() {
        this.arrayActionsHistory.clear();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(arrayActionsHistory.size > 0) {
            deleteActionThrough += delta;
            if (deleteActionThrough > actionInHistoryTime) {
                arrayActionsHistory.removeIndex(0);
                deleteActionThrough = 0f;
            }
            StringBuilder sb = new StringBuilder();
            for(String str : arrayActionsHistory) {
                sb.append("\n" + str);
            }
            actionsHistoryLabel.setText(sb.toString());
        }
    }

    public static void clearArr() {
        instance().arrayActionsHistory.clear();
    }

    public static void log(String action) {
        instance().addActionToHistory(action);
    }

    private void addActionToHistory(String action) {
        if (instance != null) {
            if (instance.arrayActionsHistory != null) {
                instance.arrayActionsHistory.add(action);
            }
        }
    }

    @Override
    public String toString() {
        final java.lang.StringBuilder sb = new java.lang.StringBuilder("ConsoleLoggerTable{");
        sb.append("arrayActionsHistory=").append(arrayActionsHistory);
        sb.append(", deleteActionThrough=").append(deleteActionThrough);
        sb.append(", actionInHistoryTime=").append(actionInHistoryTime);
        sb.append(", actionsHistoryLabel=").append(actionsHistoryLabel);
//        sb.append(", instance=").append(instance);
        sb.append('}');
        return sb.toString();
    }
}
