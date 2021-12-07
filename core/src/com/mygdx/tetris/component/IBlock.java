package com.mygdx.tetris.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class IBlock extends AbstractBlock {

    public IBlock(TetrisRectangle[][] boxes) {
        this.color = Color.CYAN;
        this.position = new Vector2(4, 23);
        this.boxes = boxes;
        buildSquares();
    }

    @Override
    public void updatePosition(int x, int y) {
        buildSquares();
    }

    @Override
    public void rotate() {
        if (position.y > 19) return;

        if ((rotation == 0 || rotation == 2) && position.x <= 3) {
            position.x = 3;
        }

        if (rotation < 3) {
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
            int pX = 0;
            int pY = 0;
            if (rotation == 0 || rotation == 2) {
                pY = -i;
            } else if (rotation == 1 || rotation == 3) {
                pX = -i;
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
