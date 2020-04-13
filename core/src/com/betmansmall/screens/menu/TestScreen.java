package com.betmansmall.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.utils.NavigationDrawer;
import com.betmansmall.utils.Utils;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.widget.VisTextButton;

import org.lwjgl.opengl.GL11;

/**
 * Created by Crowni on 9/14/2017.
 **/
public class TestScreen extends AbstractScreen {
//    private static final int NAV_WIDTH = 200;
//    private static final int NAV_HEIGHT = 1920;
    private static final int NAV_WIDTH = Gdx.graphics.getHeight()/3;
    private static final int NAV_HEIGHT = Gdx.graphics.getHeight();
    private Stage stage;
    private final NavigationDrawer drawer = new NavigationDrawer(NAV_WIDTH, NAV_HEIGHT);

    public TestScreen(GameMaster gameMaster) {
        super(gameMaster);
        stage = new Stage(new ScreenViewport());

        TextureAtlas atlas = new TextureAtlas("data/menu_ui.atlas");

        final Image icon_music = new Image(atlas.findRegion("icon_music"));
        icon_music.setName("icon_music");
        final Image icon_off_music = new Image(atlas.findRegion("icon_off_music"));
        icon_music.setName("icon_music");
        drawer.stack(icon_music, icon_off_music).expand().fill().row(); // .pad(52, 52, 300, 52).pad(0,drawer.getWidth()/5, drawer.getHeight()/3, 0)

        final VisTextButton button_back = new VisTextButton("BACK");
        button_back.setName("BUTTON_BACK");
        drawer.add(button_back).expand().fill().row();

        TextButton onBtn = new VisTextButton("ON");
        onBtn.setName("OFF");
        TextButton offBtn = new VisTextButton("OFF");
        offBtn.setName("OFF");
        onBtn.setVisible(false);
        drawer.stack(onBtn, offBtn).expand().fill().row(); // .pad(52, 52, 300, 52).pad(0,drawer.getWidth()/5, drawer.getHeight()/3, 0)

        final Image image_background = new Image(Utils.getTintedDrawable(atlas.findRegion("image_background"), Color.BLACK));
        image_background.setName("IMAGE_BACKGROUND");
        drawer.setBackground(image_background.getDrawable());
        drawer.bottom().left();
        drawer.setWidthStartDrag(40f);
        drawer.setWidthBackDrag(0F);
        drawer.setTouchable(Touchable.enabled);
        image_background.setFillParent(true);
        stage.addActor(image_background);
        drawer.setFadeBackground(image_background, 0.5f);

        stage.addActor(drawer);

        final Image button_menu = new Image(atlas.findRegion("button_menu"));
        button_menu.setName("BUTTON_MENU");
        button_menu.setOrigin(Align.center);
        stage.addActor(button_menu);
        drawer.setRotateMenuButton(button_menu, 90f);

        Image image_shadow = new Image(atlas.findRegion("image_shadow"));
        image_shadow.setHeight(NAV_HEIGHT);
        image_shadow.setX(NAV_WIDTH);
        drawer.setAreaWidth(NAV_WIDTH + image_shadow.getWidth());
        drawer.addActor(image_shadow);

        // show the panel
        drawer.showManually(true);

        ClickListener listener = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                boolean closed = drawer.isCompletelyClosed();
                Actor actor = event.getListenerActor();
                String name = actor.getName();
                if (name != null) {
                    if (name.equals("icon_off_music")) {
                        Logger.logDebug("BUTTON_BACK clicked.");
                        game.removeTopScreen();
                    } else if (name.equals("BUTTON_MENU") || name.equals("IMAGE_BACKGROUND")) {
                        Logger.logDebug("Menu button clicked.");
                        image_background.setTouchable(closed ? Touchable.enabled : Touchable.disabled);
                        drawer.showManually(closed);
                        onBtn.setVisible(!onBtn.isVisible());
                        offBtn.setVisible(!offBtn.isVisible());
                    } else if (name.contains("OFF")) {
                        Logger.logDebug("Music button clicked.");
                        onBtn.setVisible(!onBtn.isVisible());
                        offBtn.setVisible(!offBtn.isVisible());
                    } else if (name.contains("icon_music")) {
                        Logger.logDebug("Music button clicked.");
                        icon_music.setVisible(!icon_music.isVisible());
                        icon_off_music.setVisible(!icon_off_music.isVisible());
                    }
                }
            }
        };

        Utils.addListeners(listener, icon_music, icon_off_music, button_back, onBtn, offBtn, button_menu, image_background); // , button_menu, image_background
    }

    @Override
    public void show() {
        Gdx.app.log("MainMenuScreen::show()", "-- Called!");
        Gdx.input.setInputProcessor(stage);
        resize(NAV_WIDTH, NAV_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(24 / 255F, 168 / 255F, 173 / 255F, 0);
        Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().setScreenSize(width, height);
    }
}
