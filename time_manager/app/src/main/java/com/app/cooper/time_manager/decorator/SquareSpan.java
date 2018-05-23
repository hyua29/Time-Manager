package com.app.cooper.time_manager.decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

public class SquareSpan implements LineBackgroundSpan {

    private int color;
    private float squareHeight;
    private float squareWidth;
    public SquareSpan(int color, float squareHeight, float squareWidth) {
        this.color = color;
        this.squareHeight = squareHeight;
        this.squareWidth = squareWidth;
    }

    public SquareSpan(int color) {
        this.color = color;

    }

    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {

        int oldColor = p.getColor();
        if (color != 0) {
            p.setColor(color);
        }

        c.drawRect(left+50,bottom,right-50,bottom+(bottom-top), p);
        p.setColor(oldColor);  // restore old color

    }
}
