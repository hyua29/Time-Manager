package com.app.cooper.time_manager.decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class DayMarkDotSoan implements LineBackgroundSpan {

    /**
     * Default radius used
     */
    public static final float DEFAULT_RADIUS = 3;

    private final float radius;
    private final int color;


    public DayMarkDotSoan() {
        this.radius = DEFAULT_RADIUS;
        this.color = 0;
    }


    public DayMarkDotSoan(int color) {
        this.radius = DEFAULT_RADIUS;
        this.color = color;
    }



    public DayMarkDotSoan(float radius) {
        this.radius = radius;
        this.color = 0;
    }

    /**
     * Create a span to draw a dot using a specified radius and color
     *
     * @param radius radius for the dot
     * @param color  color of the dot
     */
    public DayMarkDotSoan(float radius, int color) {
        this.radius = radius;
        this.color = color;
    }


    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        {
            int oldColor = p.getColor();
            if (color != 0) {
                p.setColor(color);
            }
            c.drawCircle((left + right) / 2, top - radius, radius, p);
            p.setColor(oldColor);
        }
    }
}
