package com.mygdx.tetris.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class JBlock extends AbstractBlock {

    public JBlock(TetrisRectangle[][] boxes) {
        this.color = new Color(0.3f, 0.0f, 1f, 1);
        this.position = new Vector2(4, 20);
        this.boxes = boxes;
        buildSquares();
    }

    @Override
    public void updatePosition(int x, int y) {
        buildSquares();
    }

    @Override
    public void rotate() {

        if (rotation == 1 && position.x <= 2) {
            position.x = 2;
        }

        if (rotation == 3 && position.x >= 8) {
            position.x = 7;
        }

        if (rotation < 3) {
            rotation += 1;
        } else {
            rotation = 0;
        }

        buildSquares();
    }

    @Override
    protected void buildSquares() {
        Vector2 pos = position.cpy();
        Array<TetrisRectangle> rectangles = new Array<>(4);

        if (rotation == 3 && pos.x == 0) {
            pos.x += 1;
        }

        for (int i = 0; i < 3; i++) {
            int pX = 0;
            int pY = 0;
            if (rotation == 0) {
                pX = i;
            } else if (rotation == 1) {
                pY = -i;
            } else if (rotation == 2) {
                pX = -i;
            } else if (rotation == 3) {
                pY = i;
            }

            float x = pos.x + pX;
            float y = pos.y + pY;
            TetrisRectangle rec = new TetrisRectangle(x, y, 1, 1);
            rec.setColor(color);
            rectangles.add(rec);
        }

        TetrisRectangle first = new TetrisRectangle();
        first.setColor(color);

        if (rotation == 0) {
            first.set(pos.x, pos.y + 1, 1, 1);
        } else if (rotation == 1) {
            first.set(pos.x + 1, pos.y, 1, 1);
        } else if (rotation == 2) {
            first.set(pos.x, pos.y - 1, 1, 1);
        } else if (rotation == 3) {
            first.set(pos.x - 1, pos.y, 1, 1);
        }

        rectangles.add(first);

        ensureValidPosition(pos, rectangles);
    }
}
