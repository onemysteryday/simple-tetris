package com.mygdx.tetris;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.tetris.component.*;

import java.util.Random;

public class GameScreen extends ScreenAdapter {

    public static final String TAG = GameScreen.class.getName();

    private SimpleTetrisGame game;
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private ScreenViewport hudViewPort;
    private ShapeRenderer renderer;
    private BitmapFont font;

    private Block block;
    private TetrisRectangle[][] boxes;
    private Array<Integer> willRemoveRow = new Array<>(false,40);

    private Circle circleL;
    private Circle circleR;
    private Circle circleD;
    private Circle circleA;

    private GlyphLayout glyphLayout;

    private int score;
    private int highestScore;
    private float speed = 0f;
    private boolean desktop;
    private float initialTime = 0;
    private float dropRate = 0.8f;

    public GameScreen(SimpleTetrisGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        desktop = Gdx.app.getType() == Application.ApplicationType.Desktop;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float denominator = desktop ? 3f : 2f;
        float scale = (w / denominator) / 10;

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(1 / scale);

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);

        hudViewPort = new ScreenViewport();
        //float fontScale = w > h ? h / w : w / h;
        font = new BitmapFont(Gdx.files.internal("thaleahfat.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(Gdx.graphics.getDensity());

        glyphLayout = new GlyphLayout(font, "L");
        createHudCircles();

        reset();
    }

    @Override
    public void resize(int width, int height) {
        hudViewPort.update(width, height, true);
        viewport.update(width, height, true);
        camera.setToOrtho(false, 10, 20);
        camera.translate(0, desktop ? 0f : -5f);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        speed = 0f;

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            block.rotate();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            block.move(-1, delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            block.move(1, delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            speed = 0.5f;
        }

        if (Gdx.input.justTouched()) {
            handleRotate();
            handleTouch();
        } else if (Gdx.input.isTouched()) {
            handleTouch();
        }

        initialTime += delta;
        if (initialTime >= 90) {
            initialTime = 0;
            dropRate -= 0.2;
            dropRate = MathUtils.clamp(dropRate, 0.2f, 0.8f);
        }

        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glLineWidth(3);

        renderer.setProjectionMatrix(camera.combined);

        block.update(delta, speed);

        if (block.isCollide()) {
            block = createBlock();
            block.setDropRate(dropRate);
        } else {
            block.draw(renderer);
        }

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int row = 0; row < 20; row++) {
            boolean willRemove = true;
            int nullCol = 0;
            for (int col = 0; col < 10; col++) {
                TetrisRectangle rec = boxes[row][col];
                if (rec == null) {
                    willRemove = false;
                    nullCol += 1;
                    continue;
                }
                if (rec.y == 19)  {
                    reset();
                    break;
                } else {
                    renderer.setColor(rec.getColor());
                    renderer.rect(rec.x + 0.05f, rec.y + 0.05f, rec.width - 0.1f, rec.height - 0.1f);
                }
            }

            if (willRemove) {
                willRemoveRow.add(row);
            }

            if (nullCol == 10) {
                break;
            }
        }
        renderer.end();

        renderBox();

        hudViewPort.apply();

        SpriteBatch batch = game.getBatch();
        batch.setProjectionMatrix(hudViewPort.getCamera().combined);
        batch.begin();
        font.draw(batch, "Simple Tetris", 16, hudViewPort.getWorldHeight() - 16,
                0, Align.left, false);
        font.draw(batch, "Score: " + score + "\nTop Score: " + highestScore + "\nFPS: " + Gdx.graphics.getFramesPerSecond(),
                hudViewPort.getWorldWidth() - 16, hudViewPort.getWorldHeight() - 16,
                0, Align.right, false);

        if (!desktop) {

            font.draw(batch, "L", circleL.x, circleL.y + glyphLayout.height / 2,
                    0, Align.center, false);
            font.draw(batch, "R", circleR.x, circleR.y + glyphLayout.height / 2,
                    0, Align.center, false);

            font.draw(batch, "D", circleD.x, circleD.y + glyphLayout.height / 2,
                    0, Align.center, false);

            font.draw(batch, "A",  circleA.x, circleA.y + glyphLayout.height / 2,
                    0, Align.center, false);
        }

        batch.end();

        renderControls();

        destroyBoxIfFull();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        game = null;
        renderer.dispose();
        font.dispose();
    }

    private void renderBox() {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(1, 1, 1, 1);
        renderer.rect(0, 0, Constants.BOUND_WIDTH, Constants.BOUND_HEIGHT);
        renderer.end();
    }

    private void renderControls() {
        if (desktop) return;
        int segments = 128;

        renderer.setProjectionMatrix(hudViewPort.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.WHITE);
        renderer.circle(circleL.x,  circleL.y, circleL.radius, segments);
        renderer.circle(circleR.x,  circleR.y, circleR.radius, segments);
        renderer.circle(circleD.x,  circleD.y, circleD.radius, segments);

        renderer.circle(circleA.x,  circleA.y, circleA.radius, segments);
        renderer.end();
    }

    private void createHudCircles() {
        float w = Gdx.graphics.getWidth();
        float scale = (w / 6) / 2;
        float padding = scale * 1.5f;

        circleL = new Circle(padding, scale * 4, scale);
        circleR = new Circle(scale * 4.5f, scale * 4, scale);
        circleD = new Circle(scale * 3f, padding, scale);
        circleA = new Circle(w - padding,  scale * 4, scale);
    }

    private void handleTouch() {
        Vector2 vec = hudViewPort.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

        if (circleL.contains(vec)) {
            block.move(-1, Gdx.graphics.getDeltaTime());
        }

        if (circleR.contains(vec)) {
            block.move(1, Gdx.graphics.getDeltaTime());
        }

        if (circleD.contains(vec)) {
            speed = 0.5f;
        }
    }

    private void handleRotate() {
        Vector2 vec = hudViewPort.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        if (circleA.contains(vec)) {
            block.rotate();
        }
    }

    private void destroyBoxIfFull() {
        int moveDown = willRemoveRow.size;

        if (moveDown == 0) return;

        for (int i = 0; i < moveDown; i++) {
            int row = willRemoveRow.get(i);
            for (int col = 0; col < 10; col++) {
                boxes[row][col] = null;
            }
        }

        int tempMoveDown = 0;
        for (int row = 0; row < 20; row++) {
            int emptyCell = 0;
            for (int col = 0; col < 10; col++) {
                TetrisRectangle rec = boxes[row][col];
                if (rec == null) {
                    emptyCell += 1;
                    continue;
                }

                if (tempMoveDown > 0) {
                    rec.setPosition(col, row - tempMoveDown);
                    boxes[row][col] = null;
                    boxes[(int) rec.y][(int) rec.x] = rec;
                }
            }

            if (emptyCell == 10) {
                tempMoveDown += 1;
            } else if (tempMoveDown > 0) {
                row -= tempMoveDown;
                tempMoveDown = 0;
            }
        }

        score += moveDown * 10;

        willRemoveRow.clear();
    }

    private void reset() {
        boxes = new TetrisRectangle[20][10];
        block = createBlock();
        if (score > highestScore) {
            highestScore = score;
        }
        score = 0;
        dropRate = 0.8f;
    }

    private Block createBlock() {
        int v = new Random().nextInt(7);

        if (v == 1) {
            return new JBlock(boxes);
        }

        if (v == 2) {
            return new LBlock(boxes);
        }

        if (v == 3) {
            return new OBlock(boxes);
        }

        if (v == 4) {
            return new SBlock(boxes);
        }

        if (v == 5) {
            return new ZBlock(boxes);
        }

        if (v == 6) {
            return new TBlock(boxes);
        }

        return new IBlock(boxes);
    }
}
