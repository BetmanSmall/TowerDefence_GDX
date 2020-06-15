package com.betmansmall.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;

public class BackSliderTable extends Table {
    private final Rectangle scissorBounds;
    private final Rectangle areaBounds;

    private float widthStart = 60f;
    private float widthBack = 20f;
    private float speed = 15f;

    private Vector2 clamp = new Vector2();
    private Vector2 posTap = new Vector2();
    private Vector2 end = new Vector2();
    private Vector2 first = new Vector2();
    private Vector2 last = new Vector2();

    private boolean show = false;
    private boolean isTouched = false;
    private boolean isStart = false;
    private boolean isBack = false;
    private boolean auto = false;
    private boolean enableDrag = true;

    public BackSliderTable(float width, float height) {
        scissorBounds = new Rectangle();
        areaBounds = new Rectangle(0, 0, width, height);
        this.setSize(width, height);
    }

    private NavigationDrawerListener listener;

    public interface NavigationDrawerListener {
        void moving(Vector2 clamp);
    }

    public void setNavigationDrawerListener(NavigationDrawerListener listener) {
        this.listener = listener;
    }

    public void setWidthStartDrag(float widthStartDrag) {
        this.widthStart = widthStartDrag;
    }

    public void setWidthBackDrag(float widthBackDrag) {
        this.widthBack = widthBackDrag;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void showManually(boolean show, float speed) {
        this.auto = true;
        this.show = show;
        this.speed = speed;
    }

    public void showManually(boolean show) {
        this.showManually(show, speed);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        getStage().calculateScissors(areaBounds, scissorBounds);
        batch.flush();
        if (ScissorStack.pushScissors(scissorBounds)) {
            super.draw(batch, alpha);
            batch.flush();
            ScissorStack.popScissors();
        }

        if (isTouched() && inputX() < stgToScrX(this.getWidth(), 0).x) {
            auto = false;
            if (!isTouched) {
                isTouched = true;
                first.set(scrToStgX(inputX(), 0));
            }
            last.set(scrToStgX(inputX(), 0)).sub(first);

            if (isCompletelyClosed()) // open = false, close = true;
                startDrag();

            if ((isStart || isBack) && enableDrag) // open = false, close =
                // false;
                if (inputX() > stgToScrX(widthStart, 0).x)
                    dragging();

            if (isCompletelyOpened()) // open = true, close = false;
                backDrag();

        } else
            noDrag();

        updatePosition();

        moving();

        rotateMenuButton();

        fadeBackground();
    }

    private boolean isMax = false;
    private boolean isMin = false;

    private void moving() {
        if (listener == null)
            return;
        if (!isCompletelyClosed() && !isCompletelyOpened()) {
            listener.moving(clamp);
        } else {
            if (!isMax && isCompletelyOpened()) {
                isMax = true;
                isMin = false;
                listener.moving(clamp);
            }
            if (!isMin && isCompletelyClosed()) {
                isMin = true;
                isMax = false;
                listener.moving(clamp);
            }
        }
    }

    private void updatePosition() {
        clamp.set(MathUtils.clamp(end.x, 0, this.getWidth()), 0);
        this.setPosition(clamp.x, 0, Align.bottomRight);
    }

    private void dragging() {
        if (isStart) {
            end.set(scrToStgX(inputX(), 0));
        }
        if (isBack && last.x < -widthBack) {
            end.set(last.add(this.getWidth() + widthBack, 0));
        }
    }

    private void backDrag() {
        isStart = false;
        isBack = true;
        show = false;
    }

    private void startDrag() {
        // check if the player touch on the drawer to OPEN it.
        if (inputX() < stgToScrX(widthStart, 0).x) {
            isStart = true;
            isBack = false;
            hintToOpen(); // hint to player if he want to open the drawer
        }
    }

    private void noDrag() {
        isStart = false;
        isBack = false;
        isTouched = false;

        // set end of X to updated X from clamp
        end.set(clamp);

        if (auto) {
            if (show) {
                end.add(speed, 0); // player want to OPEN drawer
            } else {
                end.sub(speed, 0); // player want to CLOSE drawer
            }
        } else {
            if (toOpen()) {
                end.add(speed, 0); // player want to OPEN drawer
            } else if (toClose()) {
                end.sub(speed, 0); // player want to CLOSE drawer
            }
        }

    }

    private void hintToOpen() {
        end.set(stgToScrX(widthStart, 0));
    }

    public boolean isCompletelyClosed() {
        return clamp.x == 0;
    }

    public boolean isCompletelyOpened() {
        return clamp.x == this.getWidth();
    }

    private boolean toOpen() {
        return clamp.x > this.getWidth() / 2;
    }

    private boolean toClose() {
        return clamp.x < this.getWidth() / 2;
    }

    private Vector2 stgToScrX(float x, float y) {
        return getStage().stageToScreenCoordinates(posTap.set(x, y));
    }

    private Vector2 scrToStgX(float x, float y) {
        return getStage().screenToStageCoordinates(posTap.set(x, y));
    }

    private float inputX() {
        return Gdx.input.getX();
    }

    private boolean isTouched() {
        return Gdx.input.isTouched();
    }

    private Actor menuButton = new Actor();
    private boolean isRotateMenuButton = false;
    private float menuButtonRotation = 0f;

    private void rotateMenuButton() {
        if (isRotateMenuButton) {
            menuButton.setRotation(clamp.x / this.getWidth() * menuButtonRotation);
        }
    }

    public void setRotateMenuButton(Actor actor, float rotation) {
        this.menuButton = actor;
        this.isRotateMenuButton = true;
        this.menuButtonRotation = rotation;
    }

    public void setEnableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    private Actor background = new Actor();
    private boolean isFadeBackground = false;
    private float maxFade = 1f;

    private void fadeBackground() {
        if (isFadeBackground) {
            background.setColor(background.getColor().r, background.getColor().g, background.getColor().b,
                    MathUtils.clamp(clamp.x / this.getWidth() / 2, 0, maxFade));
        }
    }

    public void setFadeBackground(Actor background, float maxFade) {
        this.background = background;
        this.isFadeBackground = true;
        this.maxFade = maxFade;
    }
}
