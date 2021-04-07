package com.betmansmall.game.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Demo for billboarded 2D text in 3D space.
 * <p>
 * 3D rendering code taken from http://blog.xoppa.com/basic-3d-using-libgdx-2/ by Xoppa, under Apache 2 license.
 * Text rendering parts in public domain.
 */
public class TextIn3D implements ApplicationListener {
    //region 3D Objects
    public Environment environment;
    public PerspectiveCamera cam;
    public CameraInputController camController;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    //endregion

    //region Text rendering
    public BitmapFont font;
    public BitmapFontCache fontCache;

    public final ScreenViewport uiViewport = new ScreenViewport();
    public SpriteBatch spriteBatch;

    /** World-space position of the text. (Corner of the cube.) */
    public Vector3 textPosition = new Vector3(2.5f, 2.5f, 2.5f);
    //endregion

    @Override
    public void create() {
        //region 3D Objects
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                Usage.Position | Usage.Normal);
        instance = new ModelInstance(model);

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
        //endregion

        //region Text rendering
        font = new BitmapFont();
        fontCache = new BitmapFontCache(font, false);
        spriteBatch = new SpriteBatch();
        //endregion
    }

    /**
     * Multiply 4x4 matrix {@code m} and 4D vector {$code (v, vW)} together.
     * Store result {@code (x/w, y/w, z/w)} back in {@code v} and return {@code w}.
     */
    private static float multiplyProjective(Matrix4 m, Vector3 v, float vW) {
        final float[] mat = m.val;
        final float x = v.x * mat[Matrix4.M00] + v.y * mat[Matrix4.M01] + v.z * mat[Matrix4.M02] + vW * mat[Matrix4.M03];
        final float y = v.x * mat[Matrix4.M10] + v.y * mat[Matrix4.M11] + v.z * mat[Matrix4.M12] + vW * mat[Matrix4.M13];
        final float z = v.x * mat[Matrix4.M20] + v.y * mat[Matrix4.M21] + v.z * mat[Matrix4.M22] + vW * mat[Matrix4.M23];
        final float w = v.x * mat[Matrix4.M30] + v.y * mat[Matrix4.M31] + v.z * mat[Matrix4.M32] + vW * mat[Matrix4.M33];
        final float iw = 1f / w;
        v.set(x * iw, y * iw, z * iw);
        return w;
    }

    @Override
    public void render() {
        //region 3D Objects
        camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        modelBatch.end();
        //endregion

        //region Text rendering

        // Multiply vector with world-space position with 3D projection-view matrix
        final Vector3 clipSpacePos = new Vector3(textPosition);
        final float w = multiplyProjective(cam.combined, clipSpacePos, 1f);

        // Do not render the text if it is behind the camera or too far away
        if (clipSpacePos.z >= -1f && clipSpacePos.z <= 1f) {
            // Calculate the position on screen (clip space is [-1,1], we need [-size/2, size/2], but this depends on your viewport)
            final float textPosX = clipSpacePos.x * Gdx.graphics.getWidth() * 0.5f;
            final float textPosY = clipSpacePos.y * Gdx.graphics.getHeight() * 0.5f;

            // Set the text normally. The position must be 0, otherwise the scaling won't work.
            // If you don't want perspective scaling, you can set x,y to textPosX,textPosY directly and skip the next part.
            fontCache.setText("Now in 3D", 0f, 0, 0f, Align.center, false);

            // Size of the text in the world
            final float fontSize = 5f;
            // Scaling factor
            final float fontScale = fontSize / w;
            // Go through prepared vertices of the font cache and do necessary transformation
            final int regionCount = font.getRegions().size;
            for (int page = 0; page < regionCount; page++) {
                final int vertexCount = fontCache.getVertexCount(page);
                final float[] vertices = fontCache.getVertices(page);
                for (int v = 0; v < vertexCount; v += 5) {
                    // This is why the text position must be 0 - otherwise the scaling would move the text
                    vertices[v] = vertices[v] * fontScale + textPosX;
                    vertices[v + 1] = vertices[v + 1] * fontScale + textPosY;
                }
            }

            // Standard viewport update
            uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            spriteBatch.setProjectionMatrix(uiViewport.getCamera().projection);
            // Draw the text normally
            spriteBatch.begin();
            fontCache.draw(spriteBatch);
            spriteBatch.end();
        }
        //endregion
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    public static void main(String[] args) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new TextIn3D(), config);
    }
}

