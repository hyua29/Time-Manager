package com.app.cooper.time_manager.custom.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.app.cooper.time_manager.listener.OnSwipeTouchListener;


public class InterceptingConstrainLayout extends ConstraintLayout {

    private OnSwipeTouchListener onSwipeTouchListener;

    public InterceptingConstrainLayout(Context context) {
        super(context);
        this.setSwipeListener();
    }

    public InterceptingConstrainLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setSwipeListener();
    }

    public InterceptingConstrainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setSwipeListener();
    }

    private void setSwipeListener() {
        onSwipeTouchListener = new OnSwipeTouchListener(this.getContext()){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                System.out.println("swiped left");
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                System.out.println("swiped right");
            }
        };

        this.setOnTouchListener(onSwipeTouchListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onSwipeTouchListener.getGestureDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
