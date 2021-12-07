package com.mygdx.tetris.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SBlock extends AbstractBlock {

    public SBlock(TetrisRectangle[][] boxes) {
        this.color = Color.GREEN;
        this.position = new Vector2(4, 21);
        this.boxes = boxes;
        buildSquares();
    }

    @Override
    public void updatePosition(int x, int y) {
        buildSquares();
    }

    @Override
    public void rotate() {
        if (rotation == 1 && position.x > 7) {
            position.x = 7;
        }

        if (rotation == 1 && position.x < 1) {
            position.x = 1;
        }

        if (rotation < 1) {
            rotation += 1;
        } else {
            rotation = 0;
        }

        buildSquares();
    }

    protected void buildSquares() {
        Vector2 pos = position.cpy();
        Array<TetrisRectangle> rectangles = new Array<>(4);

        if (rotation == 3 && pos.x == 0) {
            pos.x += 1;
        }

        for (int i = 0; i < 4; i++) {
            int pX = rotation == 0 ? i : 0;
            int pY = rotation == 0 ? 0 : i;
            if (i > 1) {
                pY = rotation == 0 ? -1 : 2 - i;
                pX = rotation == 0 ? 2 - i : 1;
            }

            float x = pos.x + pX;
            float y = pos.y + pY;
            TetrisRectangle rec = new TetrisRectangle(x, y, 1, 1);
            rec.setColor(color);
            rectangles.add(rec);
        }

        ensureValidPosition(pos, rectangles);
    }
}
