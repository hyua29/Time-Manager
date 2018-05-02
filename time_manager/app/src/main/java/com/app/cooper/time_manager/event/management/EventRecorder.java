package com.app.cooper.time_manager.event.management;

import com.app.cooper.time_manager.objects.Event;
import com.app.cooper.time_manager.objects.EventAssociatedCalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;

public class EventRecorder {
    private static Collection<EventAssociatedCalendarDay> eventAssociatedCalendarDays = new ArrayList<>();
    private static Collection<Event> events = new ArrayList<>();


    public static void recordEvent(EventAssociatedCalendarDay day, Event e) {
        eventAssociatedCalendarDays.add(day);
        events.add(e);
    }

    public static Collection<CalendarDay> getCalendarDays() {
        Collection<CalendarDay> dates = new ArrayList<>();
        for(EventAssociatedCalendarDay e : eventAssociatedCalendarDays) {
            dates.add(new CalendarDay(e.getYear(), e.getMonth(), e.getDay()));
        }
        return dates;
    }

    public static List<Event> filterEventsByDate(CalendarDay day) {
        List<Event> result = new ArrayList<>();
        for(Event event: events) {
            if (event.getStartYear() == day.getYear()
                    && event.getStartMonth() == day.getMonth()
                    && event.getStartDay() == day.getDay())
                result.add(event);

        }
        return result;
    }

}
