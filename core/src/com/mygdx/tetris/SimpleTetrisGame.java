package com.mygdx.tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SimpleTetrisGame extends Game {
    private static final float TIME_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();
        //doPhysicsStep(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

//    private void doPhysicsStep(float deltaTime) {
//        // fixed time step
//        // max frame time to avoid spiral of death (on slow devices)
//        float frameTime = Math.min(deltaTime, 0.25f);
//        accumulator += frameTime;
//        while (accumulator >= TIME_STEP) {
//            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
//            accumulator -= TIME_STEP;
//        }
//    }
}
