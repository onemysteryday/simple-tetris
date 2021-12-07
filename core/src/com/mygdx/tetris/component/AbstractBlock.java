package com.mygdx.tetris.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class AbstractBlock implements Block {
    protected Vector2 position;
    protected Array<TetrisRectangle> squares = new Array<>(4);
    protected int rotation;
    protected float time;
    protected float move;
    protected Color color = Color.WHITE;
    protected TetrisRectangle[][] boxes;
    protected float dropRate = 0.8f;

    protected abstract void buildSquares();

    @Override
    public void updatePosition(int x, int y) {
        position.set(x, y);
        buildSquares();
    }

    @Override
    public void update(float delta, float speed) {
        if (time < dropRate) {
            time += delta + speed;
            return;
        }
        for (TetrisRectangle rec : squares) {
            rec.y -= 1;
        }
        //Gdx.app.log(TAG, position.toString());
        position.y -= 1;
        time = 0;
    }

    @Override
    public void draw(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(color);
        for (TetrisRectangle rec : squares) {
            if (rec.y > 19 || rec.y < 0) continue;
            renderer.rect(rec.x + 0.05f, rec.y + 0.05f, rec.width - 0.1f, rec.height - 0.1f);
        }
        renderer.end();
    }

    @Override
    public void move(int value, float delta) {
        if (move < 0.8) {
            move += delta + 0.12;
            return;
        }

        move = 0;

        boolean canMove = true;

        for (TetrisRectangle rec : squares) {
            float x = rec.x + value;
            if (rec.y < 20 && (x >= 0 && x < 10) && boxes[(int) rec.y][(int) x] != null) {
                canMove = false;
                break;
            }

            if (x > 9 || x < 0) {
                canMove = false;
                break;
            }
        }

        if (!canMove) return;

        position.x += value;

        buildSquares();
    }

    @Override
    public boolean isCollide() {
        boolean collided = false;

        if (position.y > 19) return false;

        for (TetrisRectangle rec : squares) {
            if (rec.y < 0) {
                collided = true;
                break;
            }
            if (rec.y < 20 && boxes[(int) rec.y][(int) rec.x] != null) {
                collided = true;
                break;
            }
        }

        if (collided) {
            for (TetrisRectangle rec : squares) {
                rec.y += 1;
                if (rec.y > 19) continue;
                boxes[(int) rec.y][(int) rec.x] = rec;
            }
        }

        return collided;
    }

    protected void ensureValidPosition(Vector2 pos, Array<TetrisRectangle> rectangles) {
        for (TetrisRectangle rec : rectangles) {
            if (rec.x > -1 && rec.y < 20 && boxes[(int) rec.y][(int) rec.x] != null) {
                return;
            }
        }

        position = pos;
        squares.clear();
        squares.addAll(rectangles);
    }

    @Override
    public void setDropRate(float dropRate) {
        this.dropRate = dropRate;
    }
}
