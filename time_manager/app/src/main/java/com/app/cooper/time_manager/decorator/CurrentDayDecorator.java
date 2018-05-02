package com.app.cooper.time_manager.decorator;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;


/**
 * decorate the current day in the calendar view
 */
public class CurrentDayDecorator implements DayViewDecorator {

    private final int color;

    public CurrentDayDecorator(int color) {
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(new CalendarDay());
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DayMarkDotSoan(10, color));
    }
}
