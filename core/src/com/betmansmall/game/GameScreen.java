package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
	OrthographicCamera camera;
	Vector3 touchPos;
	SpriteBatch batch;
	BitmapFont font;
	ShapeRenderer shapeRenderer;

	Music gameMusic1;
//	Sound dropSound;
//	String dropString;

//	Texture dropImage;
//	Texture bucketImage;
//	Rectangle bucket;
//	Array<Rectangle> raindrops;

//	long lastDropTime;
//	int dropsGatchered;

	final TowerDefence game;

	public Field field;
	private int gameCoordXForWhichCell;
	private int gameCoordYForWhichCell;

	public GameScreen(final TowerDefence gam) {
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.app.log("tag1", "widht: " + Gdx.graphics.getWidth());
        Gdx.app.log("tag1", "height: " + Gdx.graphics.getHeight());
		this.touchPos = new Vector3();

		this.batch = new SpriteBatch();
		this.font = new BitmapFont();
		this.shapeRenderer = new ShapeRenderer();

		this.game = gam;
		init();

//		dropImage = new Texture("droplet.png");
//		bucketImage = new Texture("bucket.png");
//
//		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));
//		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"));
//
//		rainMusic.setLooping(true);
//		rainMusic.play();
//
//		bucket = new Rectangle();
//		bucket.x = 800 / 2 - 64 / 2;
//		bucket.y = 20;
//		bucket.width = 64;
//		bucket.height = 64;
//
//		raindrops = new Array<Rectangle>();
//		spawnRaindrop();
	}

	public void init() {
//		paint.setColor(Color.BLACK);

//        Log.d("TTW", "getWidth(): " + getWidth());
//        Log.d("TTW", "getHeight(): " + getHeight());

		int sizeCell = 32;
//        int sizeX = (int) (getWidth()/sizeCell); // 25; // 65, 120
//        int sizeY = (int) (getHeight()/sizeCell); // 11; // 30, 60
		int sizeX = 10; // 65, 120
		int sizeY = 10; // 30, 60

		field = new Field();
		field.createField(sizeX, sizeY); // 65, 30
		field.setSizeCell(sizeCell);

//		Log.d("TTW", "field.sizeX(): " + field.getSizeX());
//		Log.d("TTW", "field.sizeY(): " + field.getSizeY());

		for(int y = 0; y < field.getSizeY(); y++)
			for(int x = 0; x < field.getSizeX(); x++)
				if(Math.random()*101 <= 10)
					field.setBusy(x, y);

//        for(int x = field.getSizeX(); x >= 0; x--)
//            for(int y = field.getSizeY(); y >= 0; y--)
//                if(rand()%101 <= 10)
//                    field.setTower(x, y);

		int defaultNumCreateCreeps = 10;
//        int numCreepsK = 0;
//        for(int y = 0; y < field.getSizeY(); y++)
//            for(int x = 0; x < field.getSizeX(); x++)
//                if(Math.random()*101 <= 20)
//                    if(numCreepsK++ < defaultNumCreateCreeps)
//                        field.setCreep(x, y);

		field.setBusy(0, 0);
		field.setBusy(1, 0);
		field.setBusy(1, 1);
		field.setBusy(0, 1);

		field.createSpawnPoint(defaultNumCreateCreeps, 0, 0);
		field.createExitPoint(field.getSizeX() - 1, field.getSizeY() - 1);

		field.setCreep(0, 0);
		field.setCreep(0, 0);
		field.setCreep(0, 0);

//        loadMap(TOWER_DEFENCE_PATH + "maps/arcticv1.tmx");
	}

	public void mousePressEvent(float mouseX, float mouseY) {
		gameCoordXForWhichCell = new Integer( (int) mouseX);
		gameCoordYForWhichCell = new Integer( (int) mouseY);

		if(whichCell(gameCoordXForWhichCell, gameCoordYForWhichCell)) {
//			Log.d("TTW", "mousePressEvent() -- mouseX: " + gameCoordXForWhichCell + " mouseY: " + gameCoordYForWhichCell);
//			if(MainActivity.inputMode) {
				field.createExitPoint(gameCoordXForWhichCell, gameCoordYForWhichCell);
//                field.waveAlgorithm(gameCoordXForWhichCell, gameCoordYForWhichCell);
//			} else {
//				if (field.containEmpty(gameCoordXForWhichCell, gameCoordYForWhichCell)) {
//					field.setBusy(gameCoordXForWhichCell, gameCoordYForWhichCell);
//				} else if (field.containBusy(gameCoordXForWhichCell, gameCoordYForWhichCell)) {
//					field.clearBusy(gameCoordXForWhichCell, gameCoordYForWhichCell);
//				}
//
//				field.waveAlgorithm();
//			}
//			invalidate();
		}
	}

	public boolean whichCell(Integer mouseX, Integer mouseY) {
		int mainCoorMapX = field.getMainCoorMapX();
		int mainCoorMapY = field.getMainCoorMapY();
		int spaceWidget = field.getSpaceWidget();
		int sizeCell = field.getSizeCell();

		int tmpX, tmpY;
		tmpX = ( (mouseX.intValue()+sizeCell - spaceWidget - mainCoorMapX) / sizeCell);
		tmpY = ( (mouseY.intValue()+sizeCell - spaceWidget - mainCoorMapY) / sizeCell);
		if(tmpX > 0 && tmpX < field.getSizeX()+1)
			if(tmpY > 0 && tmpY < field.getSizeY()+1)
			{
				gameCoordXForWhichCell = tmpX-1;
				gameCoordYForWhichCell = tmpY-1;
				return true;
			}

		return false;
	}

//	private void spawnRaindrop(){
//		Rectangle raindrop = new Rectangle();
//		raindrop.x = MathUtils.random(0, 800-64);
//		raindrop.y = 480;
//		raindrop.width = 64;
//		raindrop.height = 64;
//		raindrops.add(raindrop);
//		lastDropTime = TimeUtils.nanoTime();
//	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);

		batch.begin();

//		shapeRenderer.setProjectionMatrix(camera.combined);
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//		Gdx.gl.glLineWidth(1f);
//		shapeRenderer.setColor(Color.BLACK); // 100, 60, 21, 255); // Color.BLUE);
//		shapeRenderer.line(1f, 1f, 1f, 10f);
//		shapeRenderer.line(1f, 1f, 10f, 1f);
//		shapeRenderer.line(10f, 10f, 10f, 1f);
//		shapeRenderer.line(10f, 10f, 1f, 10f);
//		shapeRenderer.end();
//
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//		Gdx.gl.glLineWidth(1);
//		shapeRenderer.setColor(Color.RED); // 100, 60, 21, 255); // Color.BLUE);
//		shapeRenderer.rect(2f, 2f, 7f, 7f);
//		shapeRenderer.end();

		drawGrid(delta);
//		drawRelief(delta);
//		drawCreeps(delta);
//		drawStepsAndMouse(delta);

//		shapeRenderer.end();

		batch.end();

//		 Instruction ------------------------------------------------------
//
//		Gdx.gl.glLineWidth(10);
//		shapeRenderer.setProjectionMatrix(camera.combined);
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//		shapeRenderer.setColor(Color.BLUE);//1, 1, 1, 1);
//		shapeRenderer.line(0, 0, camera.viewportWidth, camera.viewportHeight);
//		shapeRenderer.line(0, camera.viewportHeight, camera.viewportWidth, 0);
//		shapeRenderer.end();
//
//
//		font.setColor(255, 0, 0, 255);
//		game.font.draw(game.batch, global_text, 10, 20);
//		game.batch.draw(bucketImage, bucket.x, bucket.y);
//		for (Rectangle raindrop: raindrops){
//			game.batch.draw(dropImage, raindrop.x, raindrop.y);
//		}
//
//		if(Gdx.input.isTouched()){
//			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
//			camera.unproject(touchPos);
//			bucket.x = (int) (touchPos.x -64 / 2);
//		}
//
//		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
//		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();
//
//		if (bucket.x < 0) bucket.x = 0;
//		if (bucket.x > 800 - 64) bucket.x = 800 - 64;
//
//		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
//
//		Iterator<Rectangle> iter = raindrops.iterator();
//		while (iter.hasNext()){
//			Rectangle raindrop = iter.next();
//			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
//			if (raindrop.y + 64 < 0) iter.remove();
//			if (raindrop.overlaps(bucket)){
//				dropsGatchered++;
//				dropSound.play();
//				iter.remove();
//			}
//		}
	}

	public void drawGrid(float delta) {
		int mainCoorMapX = field.getMainCoorMapX();
		int mainCoorMapY = field.getMainCoorMapY();
		int spaceWidget = field.getSpaceWidget();
		int sizeCell = field.getSizeCell();

		int fieldX = field.getSizeX();
		int fieldY = field.getSizeY();

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		Gdx.gl.glLineWidth(1);

//      paint.setARGB(255, 0, 255, 0);
//		font.setColor(100, 60, 21, 255);
		shapeRenderer.setColor(Color.BROWN); // 100, 60, 21, 255); // Color.BLUE);

		for(int k = 0; k < fieldX+1; k++)
			shapeRenderer.line(mainCoorMapX + spaceWidget + k * sizeCell, mainCoorMapY + spaceWidget, mainCoorMapX + spaceWidget + k*sizeCell, mainCoorMapY + spaceWidget + sizeCell*fieldY);

		for(int k = 0; k < fieldY+1; k++)
			shapeRenderer.line(mainCoorMapX + spaceWidget, mainCoorMapY + spaceWidget + k*sizeCell, mainCoorMapX + spaceWidget + sizeCell*fieldX, mainCoorMapY + spaceWidget + k*sizeCell);

		shapeRenderer.end();
	}

	public void drawRelief(float delta) {
//        paint.setARGB(255,100,60,21);
//		paint.setColor(Color.BLACK);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK); // 100, 60, 21, 255); // Color.BLUE);

		int fieldX = field.getSizeX();
		int fieldY = field.getSizeY();

		for(int y = 0; y < fieldY; y++)
		{
			for(int x = 0; x < fieldX; x++)
			{
				int mainCoorMapX = field.getMainCoorMapX();
				int mainCoorMapY = field.getMainCoorMapY();
				int spaceWidget = field.getSpaceWidget();
				int sizeCell = field.getSizeCell();

				float pxlsX = mainCoorMapX + spaceWidget + x*sizeCell;//+1;
				float pxlsY = mainCoorMapY + spaceWidget + y*sizeCell;//+1;
				float localSizeCell = sizeCell;//-1;
				float localSpaceCell = 2;

				if(field.containBusy(x, y))
				{
					shapeRenderer.rect(pxlsX + localSpaceCell, pxlsY + localSpaceCell, localSizeCell - localSpaceCell*2, localSizeCell - localSpaceCell*2);
//                if(!mapLoad)
//					canvas.drawRect(pxlsX+1 + localSpaceCell, pxlsY+1 + localSpaceCell, pxlsX + sizeCell - localSpaceCell, pxlsY + sizeCell - localSpaceCell, paint);//QColor(0, 0, 0));
//                    p.fillRect(pxlsX+1, pxlsY+1, localSizeCell-1, localSizeCell-1, QColor(0, 0, 0));
//                else
//                    p.drawPixmap(pxlsX, pxlsY, localSizeCell, localSizeCell, field.getBusyPixmapOfCell(x, y));
				}
			}
		}
//		shapeRenderer.rect(0, 0, 10, 10);
		shapeRenderer.end();
	}

//	public void drawCreeps(float delta) {
////        Log.d("TTW", "drawCreeps(1);");
//		int mainCoorMapX = field.getMainCoorMapX();
//		int mainCoorMapY = field.getMainCoorMapY();
//		int spaceWidget = field.getSpaceWidget();
//		int sizeCell = field.getSizeCell();
//
//		int fieldX = field.getSizeX();
//		int fieldY = field.getSizeY();
//
//		for(int y = 0; y < fieldY; y++) {
////            Log.d("TTW", "drawCreeps(2);");
//			for(int x = 0; x < fieldX; x++) {
////                Log.d("TTW", "drawCreeps(3);");
////                int num = field.containCreep(x, y);
//				if(field.containCreep(x, y)) {
////                    Log.d("TTW", "drawCreeps(4);");
//					float pxlsX = mainCoorMapX + spaceWidget + x*sizeCell;//+1;
//					float pxlsY = mainCoorMapY + spaceWidget + y*sizeCell;// - sizeCell/2;//+1;
//					float localSizeCell = sizeCell;//-1;
//					float localSpaceCell = sizeCell/5;
//
////                QColor color = QColor(num*10, num*10, num*10);
////                p.fillRect(pxlsX+1 + localSpaceCell, pxlsY+1 + localSpaceCell, localSizeCell-1 - 2*(localSpaceCell), localSizeCell-1 - 2*(localSpaceCell), color);
//
//					paint.setColor(Color.RED);
////                    canvas.drawRect(pxlsX+1 + localSpaceCell, pxlsY+1 + localSpaceCell, pxlsX + localSizeCell - localSpaceCell, pxlsY + localSizeCell - localSpaceCell, paint);
////                    canvas.drawRect(pxlsX+1 + space, pxlsY+1 + space, pxlsX + sizeCell - space, pxlsY + sizeCell - space, paint);//QColor(0, 0, 0));
////                    canvas.drawLine(pxlsX, pxlsY, pxlsX + localSizeCell, pxlsY + localSizeCell, paint);
////                    canvas.drawLine(pxlsX + localSizeCell, pxlsY, pxlsX, pxlsY + localSizeCell, paint);
//					canvas.drawCircle(pxlsX + localSizeCell/2, pxlsY + localSizeCell/2, localSpaceCell, paint);
//
////                    std::vector<Creep*> creeps = field.getCreeps(x, y);
////                    int size = creeps.size();
////                    for(int k = 0; k < size; k++)
////                    {
////                        if(creeps[k]->alive || creeps[k]->preDeath) // fixed!!!
////                        {
////                            int lastX, lastY;
////                            int animationCurrIter, animationMaxIter;
////                            QPixmap pixmap = creeps[k]->getAnimationInformation(&lastX, &lastY, &animationCurrIter, &animationMaxIter);
////
////                            pxlsX = mainCoorMapX + spaceWidget + x*sizeCell - localSpaceCell;
////                            pxlsY = mainCoorMapY + spaceWidget + y*sizeCell - localSpaceCell;
////
////                            if(lastX < x)
////                                pxlsX -= (sizeCell/animationMaxIter)*(animationMaxIter-animationCurrIter);
////                            if(lastX > x)
////                                pxlsX += (sizeCell/animationMaxIter)*(animationMaxIter-animationCurrIter);
////                            if(lastY < y)
////                                pxlsY -= (sizeCell/animationMaxIter)*(animationMaxIter-animationCurrIter);
////                            if(lastY > y)
////                                pxlsY += (sizeCell/animationMaxIter)*(animationMaxIter-animationCurrIter);
////
////                            p.drawPixmap(pxlsX, pxlsY, localSizeCell + localSpaceCell*2, localSizeCell + localSpaceCell*2, pixmap);
////                            //                    p.drawRect(pxlsX, pxlsY, localSizeCell + localSpaceCell*2, localSizeCell + localSpaceCell*2);
////
////                            int maxHP = 100;
////                            int hp = creeps[k]->hp;
////                            float hpWidth = localSizeCell-5;
////                            hpWidth = hpWidth/maxHP*hp;
//////                        qDebug() << "hpWidth: " << hpWidth;
////
////                            p.drawRect(pxlsX + localSpaceCell+2, pxlsY, localSizeCell-4, 10);
////                            p.fillRect(pxlsX + localSpaceCell+3, pxlsY+1, hpWidth, 9, QColor(Qt::green));
////
////                            // IT's NOT GOOD!!! Fixed!
////                            creeps[k]->coorByMapX = pxlsX;
////                            creeps[k]->coorByMapY = pxlsY;
////                            // -----------------------
////                        }
////                    }
//				}
//			}
//		}
//	}
//
//	void drawStepsAndMouse(float delta) {
//		int mainCoorMapX = field.getMainCoorMapX();
//		int mainCoorMapY = field.getMainCoorMapY();
//		int spaceWidget = field.getSpaceWidget();
//		int sizeCell = field.getSizeCell();
//
////        p.setPen(QColor(255,0,0));
////        paint.setARGB(255, 255, 0, 0);
//		paint.setColor(Color.RED);
//
//		int fieldX = field.getSizeX();
//		int fieldY = field.getSizeY();
//
//		for(int y = 0; y < fieldY; y++)
//		{
//			for(int x = 0; x < fieldX; x++)
//			{
//				int pxlsX = mainCoorMapX + spaceWidget + x*sizeCell+1;
//				int pxlsY = mainCoorMapY + spaceWidget + y*sizeCell+1;
//				int localSizeCell = sizeCell-1;
//				int localSpaceCell = sizeCell/4;
//
////                p.drawPixmap(sizeCell, 0, global_pixmap.width(), global_pixmap.height(), global_pixmap);
//
//				if(field.getStepCell(x, y) != 0) {
////                    p.drawText(pxlsX + sizeCell / 2 - 5, pxlsY + sizeCell / 2 + 5, QString("%1").arg(field.getStepCell(x, y)));
//					String str = String.valueOf(field.getStepCell(x, y));
//					canvas.drawText(str, pxlsX + sizeCell / 2 - 5, pxlsY + sizeCell / 2 + 5, paint);
//				}
//
////                if(field.isSetSpawnPoint(x,y)) {
////                    p.fillRect(pxlsX + localSpaceCell, pxlsY + localSpaceCell, localSizeCell - 2 * (localSpaceCell), localSizeCell - 2 * (localSpaceCell), QColor(255, 162, 0));
////                }
////
////                if(field.isSetExitPoint(x, y)) {
////                    p.fillRect(pxlsX + localSpaceCell, pxlsY + localSpaceCell, localSizeCell - 2 * (localSpaceCell), localSizeCell - 2 * (localSpaceCell), QColor(0, 255, 0));
////                }
//			}
//		}
//	}
//}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
//		dropImage.dispose();
//		bucketImage.dispose();
//		dropSound.dispose();
//		rainMusic.dispose();
	}

	@Override
	public void show() {
//		rainMusic.play();
	}
}
