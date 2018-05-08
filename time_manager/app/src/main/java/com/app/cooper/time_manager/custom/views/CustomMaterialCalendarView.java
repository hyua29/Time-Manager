package com.app.cooper.time_manager.custom.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

/**
 * this MaterialCalendarView allows both itself and its parent to consume the same event
 */
public class CustomMaterialCalendarView extends MaterialCalendarView {


    public CustomMaterialCalendarView(Context context) {
        super(context);
    }

    public CustomMaterialCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }
}