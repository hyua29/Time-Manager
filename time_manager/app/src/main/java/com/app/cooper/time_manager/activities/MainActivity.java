package com.app.cooper.time_manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.custom.views.DatePickerFragment;
import com.app.cooper.time_manager.custom.views.EventPickerDialog;
import com.app.cooper.time_manager.decorator.CurrentDayDecorator;
import com.app.cooper.time_manager.decorator.EventDecorator;
import com.app.cooper.time_manager.event.management.EventRecorder;
import com.app.cooper.time_manager.objects.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_NONE;


public class MainActivity extends AppCompatActivity {

    private MaterialCalendarView calendarMonthView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainToolbar);
        calendarMonthView = findViewById(R.id.calendarMonthView);
        //calendarMonthView.setSelectionMode(SELECTION_MODE_NONE);
        calendarMonthView.setSelectionColor(getResources().getColor(R.color.none));
        //---------------------- testing

        DayViewDecorator eventDecorator = new EventDecorator(getResources().getColor(R.color.grey), EventRecorder.getCalendarDays());
        //-------------------------
        calendarMonthView.addDecorator(eventDecorator);
        calendarMonthView.addDecorator(new CurrentDayDecorator(getResources().getColor(R.color.colorPrimary)));


        this.setSupportActionBar(toolbar);

        calendarMonthView.setTopbarVisible(false);  // no title and arrows

        CalendarDay currentDate = calendarMonthView.getCurrentDate();
        updateDateOnTitle(String.valueOf(currentDate.getYear()), parseMonth(currentDate.getMonth()));  // update title when page changed

        calendarMonthView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                System.out.println("Month changed");

                updateDateOnTitle(String.valueOf(date.getYear()), parseMonth(date.getMonth()));
            }
        });

        calendarMonthView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                List<Event> eventList = EventRecorder.filterEventsByDate(date) ;
                if (eventList.size() == 0 )
                    return;
                EventPickerDialog dialog = new EventPickerDialog();
                dialog.setEventList(eventList);
                dialog.show(getFragmentManager(), "");
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }


    @Override
    protected void onResume() {
        super.onResume();
        DayViewDecorator eventDecorator = new EventDecorator(getResources().getColor(R.color.grey), EventRecorder.getCalendarDays());
        //-------------------------
        calendarMonthView.addDecorator(eventDecorator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.add_event:
                Intent intent = new Intent(this, AddEventActivity.class);
                //this.startActivityForResult(intent, ADD_PERSON_REQUEST);
                this.startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDateOnTitle(String title, String subtitle) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
    }



    /*
    change subtitle accordingly
     */
    private String parseMonth(int monthInt) {
        String month;
        switch (monthInt) {
            case 1:
                month = getString(R.string.Jan);
                break;
            case 2:
                month = getString(R.string.Feb);
                break;
            case 3:
                month = getString(R.string.Mar);
                break;
            case 4:
                month = getString(R.string.Apr);
                break;
            case 5:
                month = getString(R.string.May);
                break;
            case 6:
                month = getString(R.string.Jun);
                break;
            case 7:
                month = getString(R.string.Jul);
                break;
            case 8:
                month = getString(R.string.Aug);
                break;
            case 9:
                month = getString(R.string.Sep);
                break;
            case 10:
                month = getString(R.string.Otc);
                break;
            case 11:
                month = getString(R.string.Nov);
                break;
            case 0:
                month = getString(R.string.Dec);
                break;
            default:
                month = "error";
                break;
        }
        return month;
    }

}
