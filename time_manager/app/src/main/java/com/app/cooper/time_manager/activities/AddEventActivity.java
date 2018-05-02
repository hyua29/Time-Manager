package com.app.cooper.time_manager.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.custom.views.DatePickerFragment;
import com.app.cooper.time_manager.custom.views.EventTypePickerDialog;
import com.app.cooper.time_manager.custom.views.RangeTimePickerDialog;
import com.app.cooper.time_manager.databinding.ActivityAddEventBinding;
import com.app.cooper.time_manager.enums.NotificationType;
import com.app.cooper.time_manager.event.management.EventRecorder;
import com.app.cooper.time_manager.objects.Event;
import com.app.cooper.time_manager.objects.EventAssociatedCalendarDay;
import com.app.cooper.time_manager.uilts.SoftKeyboardUtils;
import com.asksira.dropdownview.DropDownView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEventActivity extends AppCompatActivity implements RangeTimePickerDialog.OnTimeSelectListener, DatePickerDialog.OnDateSetListener, EventTypePickerDialog.OnEventTypeSelectListener {
    private Toolbar toolbar;
    private Event event;
    private TextView textViewDate;
    private TextView textViewStartTime;
    private TextView textViewEndTime;
    private TextView textViewEventType;
    private EditText eventTitle;
    //private Event temp;
    //ActivityAddEventBinding binding;

    private final int REQUEST_DESCRIPTION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //temp = new Event(); // your data is created here
        //temp.setEventName("aaaaa");
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_add_event);
        //binding.setTemp(temp); // generated setter based on the data in the layout file
        //binding.executePendingBindings();
        //System.out.println(temp);

        setContentView(R.layout.activity_add_event);

        event = new Event();

        this.toolbar = (Toolbar) findViewById(R.id.addEventToolbar);
        this.textViewDate = (TextView) findViewById(R.id.dateToShow);
        this.textViewStartTime = (TextView) findViewById(R.id.timeToShow);
        this.textViewEndTime = (TextView) findViewById(R.id.timeToShow1);
        this.textViewEventType = (TextView) findViewById(R.id.eventType);
        this.eventTitle = (EditText) findViewById(R.id.eventTitle);

        final DropDownView dropDownView = (DropDownView) findViewById(R.id.notificationPicker);
        this.textViewDate.setText(event.getDate());
        this.textViewStartTime.setText(event.getStartTime());
        this.textViewEndTime.setText(event.getEndTime());
        this.textViewEventType.setText(event.getEventType());
        final List<String> dropList = new ArrayList<String>(Arrays.asList(NotificationType.ONCE.getType(),
                NotificationType.DAILY_NOTIFICATION.getType(), NotificationType.WEEKLY_NOTIFICATION.getType(),
                NotificationType.MONTHLY_NOTIFICATION.getType(), NotificationType.MONTHLY_NOTIFICATION.getType(),
                NotificationType.NO_NOTIFICATION.getType()));
        dropDownView.setDropDownListItem(dropList);
        dropDownView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {  // show layout line when the list drops down
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                TextView layoutLine = (TextView) findViewById(R.id.layoutLine7);
                if(((ColorDrawable)layoutLine.getBackground()).getColor() == getResources().getColor(R.color.White))
                    layoutLine.setBackgroundColor(getResources().getColor(R.color.grey));
                else
                    layoutLine.setBackgroundColor(getResources().getColor(R.color.White));
            }
        });

        dropDownView.setOnSelectionListener(new DropDownView.OnSelectionListener() {
            @Override
            public void onItemSelected(DropDownView view, int position) {
                dropDownView.setPlaceholderText(dropList.get(position));
                event.setNotificationType(dropList.get(position));
                //temp.setEventName("ccc");
                //System.out.println(temp);

            }
        });
        this.setTitle("");
        this.setSupportActionBar(toolbar);
        SoftKeyboardUtils.hideKeyboardByClicking(this, findViewById(android.R.id.content));
        System.out.println(event);

        ConstraintLayout eventTypePickerButton = (ConstraintLayout) findViewById(R.id.eventTypePickerButton);
        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        this.textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        eventTypePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventTypePicker();
            }
        });

        ConstraintLayout descriptionButton = findViewById(R.id.descriptionButton);
        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddDescriptionActivity.class);
                startActivityForResult(intent, REQUEST_DESCRIPTION);
            }
        });

    }

    public void cancelNewEventCreation(View view) {
        this.finish();
    }

    //TODO: adapt it to firebase
    public void saveEvent(View view) {
        event.setEventName(eventTitle.getText().toString());
        event.setId(this.getEventIdFromSharePreference());
        EventAssociatedCalendarDay c = new EventAssociatedCalendarDay(event.getId(), event.getStartYear(), event.getStartMonth() ,event.getStartDay());
        EventRecorder.recordEvent(c, event);
        this.finish();
    }

    // Create an instance of the dialog fragment and show it
    private void showTimePickerDialog() {
        RangeTimePickerDialog dialog = new RangeTimePickerDialog();
        dialog.setIs24HourView(false);
        dialog.setRadiusDialog(20);
        dialog.setTextTabStart("Start");
        dialog.setTextTabEnd("End");
        dialog.setTextBtnPositive("Accept");
        dialog.setTextBtnNegative("Close");
        dialog.setValidateRange(false);
        dialog.setColorBackgroundHeader(R.color.colorPrimary);
        dialog.setColorBackgroundTimePickerHeader(R.color.colorPrimary);
        dialog.setColorTextButton(R.color.colorPrimaryDark);
        dialog.show(getFragmentManager(), "");
    }

    private void showDatePickerDialog() {
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.show(getFragmentManager(), "");
    }

    private void showEventTypePicker() {
        EventTypePickerDialog dialog = new EventTypePickerDialog();
        dialog.show(getFragmentManager(), "");
    }


    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
        event.setStartHour(hourStart);
        event.setStartMinute(minuteStart);
        event.setEndHour(hourEnd);
        event.setEndMinute(minuteEnd);
        this.textViewStartTime.setText(event.getStartTime());
        this.textViewEndTime.setText(event.getEndTime());
        System.out.println(event);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        event.setStartYear(year);
        event.setStartMonth(month);
        event.setStartDay(day);
        textViewDate.setText(String.format(event.getDate()));
        //Calendar cal = new GregorianCalendar(year, month, day);
        //final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        //System.out.println(dateFormat.format(cal.getTime()));
        System.out.println(event);
    }


    @Override
    public void onSelectedEventType(String choice) {
        event.setEventType(choice);
        textViewEventType.setText(choice);
        System.out.println(choice);
    }

    /**
     * receive the description and store it to event
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DESCRIPTION) {
            if (resultCode == Activity.RESULT_OK)
                event.setDescription(data.getStringExtra("description"));
        }

        System.out.println(event);
    }

    private int getEventIdFromSharePreference() {
        int newId = PreferenceManager.getDefaultSharedPreferences(this).getInt("eventId", 0);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("eventId",newId + 1);
        editor.apply();
        System.out.println("new ID!!!!!!!!!!!!!!!");
        System.out.println(newId);
        return newId;
    }


}
