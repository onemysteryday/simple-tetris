package com.mygdx.tetris.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class TetrisRectangle extends Rectangle {

    private Color color = Color.WHITE;

    public TetrisRectangle() {
    }

    public TetrisRectangle(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public TetrisRectangle(Rectangle rect) {
        super(rect);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
