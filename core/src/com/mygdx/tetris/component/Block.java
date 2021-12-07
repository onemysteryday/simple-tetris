package com.mygdx.tetris.component;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Block {
    String TAG = Block.class.getName();

    void update(float delta, float speed);
    void draw(ShapeRenderer renderer);
    void updatePosition(int x, int y);
    void rotate();
    void move(int value, float delta);
    boolean isCollide();
    void setDropRate(float dropRate);
}
