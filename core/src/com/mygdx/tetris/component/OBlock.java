package com.mygdx.tetris.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class OBlock extends AbstractBlock {

    public OBlock(TetrisRectangle[][] boxes) {
        this.color = Color.YELLOW;
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
    }

    protected void buildSquares() {
        Vector2 pos = position.cpy();
        Array<TetrisRectangle> rectangles = new Array<>(4);

        if (rotation == 3 && pos.x == 0) {
            pos.x += 1;
        }

        for (int i = 0; i < 4; i++) {
            int pX = i;
            int pY = 0;
            if (i > 1) {
                pY = -1;
                pX = 3 - i;
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
