package com.app.cooper.time_manager.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Detects left and right swipes across a view.
 */
public class OnSwipeTouchListener extends SimpleOnGestureListener implements OnTouchListener {
    private static final int SWIPE_DISTANCE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private final GestureDetector gestureDetector;
    private final Context context;

    public OnSwipeTouchListener(Context context) {

        this.gestureDetector = new GestureDetector(context, this);
        this.context = context;
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println("click detecting");
        return gestureDetector.onTouchEvent(event);
    }




    @Override
    public boolean onDown(MotionEvent e) {
        System.out.println("down");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("onfling");
        float distanceX = e2.getX() - e1.getX();
        float distanceY = e2.getY() - e1.getY();
        if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceX > 0)
                onSwipeRight();
            else
                onSwipeLeft();
            return true;
        }
        return false;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }
}