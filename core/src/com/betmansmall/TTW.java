package com.betmansmall;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.betmansmall.console.RenderedConsole;
import com.betmansmall.game.GameMaster;
import com.betmansmall.graphics.PaletteIndexedBatch;

public class TTW {
    public static final int DESKTOP_VIEWPORT_HEIGHT = 480;
    public static final int MOBILE_VIEWPORT_HEIGHT  = 360;
    public static final int DESKTOP_VIEWPORT_MIN_WIDTH = 640;

    public static GameMaster game;
//    public static FileHandle home;
    public static Viewport              viewport;
    public static Viewport              defaultViewport;
    public static ScalingViewport       scalingViewport; // 480p -> 360p for mobile
    public static ExtendViewport        extendViewport;  // 480p /w dynamic width
    public static PaletteIndexedBatch batch;
    public static ShaderProgram shader;
    public static ShapeRenderer shapes;
//    public static MPQFileHandleResolver mpqs;
    public static AssetManager assets;
    public static GameMaster.InputProcessor input;
    public static RenderedConsole console;
    public static GdxCommandManager     commands;
    public static GdxCvarManager        cvars;
    public static GdxKeyMapper          keys;
    public static I18NBundle bundle;
//    public static StringTBLs            string;
//    public static Colors                colors;
//    public static Palettes              palettes;
//    public static Colormaps             colormaps;
//    public static Fonts                 fonts;
//    public static Files                 files;
//    public static COFs                  cofs;
//    public static Textures              textures;
//    public static Audio                 audio;
//    public static MusicController       music;
//    public static Cursor                cursor;
//    public static CharData              charData;
//    public static Engine                engine;
}
