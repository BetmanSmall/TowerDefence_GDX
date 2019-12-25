package com.betmansmall.widget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Align;

/**
 * Sidebar is a widget that can be dragged in and out of the screen.
 * It consists from widget with size x, wrapped by container of larger size 1.8x, wrapped by scroll pane of size x.
 * Thus, widget is always visible for 20-100% and scroll pane allows to move it from and into the screen.
 *
 * @author Alexander on 25.12.2019.
 */
public class Sidebar<T extends Actor> extends Container<ScrollPane> {
    private T actor;
    private float hideRatio; // part of widget that can be hidden.
    private boolean vertical;

    /**
     * @param actor internal actor
     * @param align position of sidebar on the screen
     */
    public Sidebar(T actor, int align, float hideRatio) {
        this.actor = actor;
        this.hideRatio = hideRatio;
        align = normalizeAlign(align);
        vertical = align == Align.top || align == Align.bottom;
        setActor(createPane(createWideContainer(new Container<>(actor).align(align))));
        size(actor.getWidth(), actor.getHeight()); // size the scroll pane
        createInterceptListener();
    }

    private Container createWideContainer(Container<T> innerContainer) {
        return new Container<>(innerContainer).size(
                actor.getWidth() * (1 + (!vertical ? hideRatio : 0)), // expand for scrolling horizontally
                actor.getHeight() * (1 + (vertical ? hideRatio : 0))); // expand for scrolling vertically
    }

    private ScrollPane createPane(Container innerContainer) {
        ScrollPane pane = new ScrollPane(innerContainer);
        pane.setOverscroll(false, false);
        pane.setScrollingDisabled(vertical, !vertical); // allow to scroll by one axis
        pane.setFlickScroll(true);
        return pane;
    }

    private void createInterceptListener() {
        getCaptureListeners().insert(0, new InputListener() { // blocks scroll start, if clicked outside actor
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!actor.isAscendantOf(hit(x, y, false))) event.stop();
                return false;
            }
        });
    }

    /**
     * Handles non orthogonal {@link Align} values. Horizontal values is prioritized.
     */
    private int normalizeAlign(int align) {
        switch (align) {
            case Align.bottom:
            case Align.top:
            case Align.left:
            case Align.right:
                return align;
            case Align.topLeft:
            case Align.bottomLeft:
                return Align.left;
            case Align.topRight:
            case Align.bottomRight:
                return Align.right;
        }
        throw new IllegalArgumentException("Value " + align + " is not part of com.badlogic.gdx.utils.Align");
    }
}
