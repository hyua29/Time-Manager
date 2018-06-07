package com.app.cooper.time_manager.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.custom.views.pickers.DatePickerFragmentDialog;
import com.app.cooper.time_manager.custom.views.pickers.EventTypePickerDialog;
import com.app.cooper.time_manager.custom.views.pickers.EventLocationPicker;
import com.app.cooper.time_manager.custom.views.pickers.RangeTimePickerDialog;
import com.app.cooper.time_manager.enums.NotificationType;
import com.app.cooper.time_manager.objects.Event;
import com.app.cooper.time_manager.uilts.SoftKeyboardUtils;
import com.asksira.dropdownview.DropDownView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity implements RangeTimePickerDialog.OnTimeSelectListener, EventLocationPicker.OnLocationSelectListener, DatePickerDialog.OnDateSetListener, EventTypePickerDialog.OnEventTypeSelectListener {
    private Toolbar toolbar;
    private Event event;
    private TextView textViewDate;
    private TextView textViewStartTime;
    private TextView textViewEndTime;
    private TextView textViewEventType;
    private EditText eventTitle;
    private DropDownView dropDownView;

    private final int REQUEST_DESCRIPTION = 1;
    private final int REQUEST_SPEECH_INPUT = 2;
    private final int REQUEST_Location = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_event);

        this.initElements();
        this.setTitle("");
        this.setSupportActionBar(toolbar);

        SoftKeyboardUtils.hideKeyboardByClicking(this, findViewById(android.R.id.content));

        this.setupListeners();

        this.initEvent();

    }

    /**
     * initialize all buttons on the screen
     */
    private void setupListeners() {

        ImageView mic = findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
        ConstraintLayout locationButton = findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

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

            }
        });


        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        ConstraintLayout eventTypePickerButton = findViewById(R.id.eventTypePickerButton);
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


    private void initElements() {
        this.toolbar = findViewById(R.id.addEventToolbar);
        this.textViewDate = findViewById(R.id.dateToShow);
        this.textViewStartTime = findViewById(R.id.timeToShow);
        this.textViewEndTime = findViewById(R.id.timeToShow1);
        this.textViewEventType = findViewById(R.id.eventType);
        this.eventTitle = findViewById(R.id.eventTitle);
        this.dropDownView = findViewById(R.id.notificationPicker);
    }

    /**
     * open map dialog
     */
    private void getLocation() {
        EventLocationPicker dialog = new EventLocationPicker();
        dialog.show(getSupportFragmentManager(),"");

    }

    /**
     * start recording user voice and get the result as text
     */
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQUEST_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    /**
     * if the user chooses to edit the event, write all event attributes on the page
     */
    private void initEvent() {

        event = getIntent().getParcelableExtra("event");
        if(event == null) {
            event = new Event();
            event.setId(this.getEventIdFromSharePreference());
        }
            this.eventTitle.setText(event.getEventName());
            this.textViewDate.setText(event.getDate());
            this.textViewStartTime.setText(event.getStartTime());
            this.textViewEndTime.setText(event.getEndTime());
            this.textViewEventType.setText(event.getEventType());
    }

    public void cancelNewEventCreation(View view) {
        this.finish();
    }

    //TODO: adapt it to firebase
    public void saveEvent(View view) {
        event.setEventName(eventTitle.getText().toString());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dayRef = database.getReference("users/" + user.getUid() + "/events/" + event.getDateStamp());

        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Event> eventList;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    GenericTypeIndicator<List<Event>> t = new GenericTypeIndicator<List<Event>>() {};
                    eventList = snapshot.getValue(t);
                    for (int i=0;i<eventList.size();i++) {
                        if(event.getId() == eventList.get(i).getId())
                            eventList.remove(i);
                    }

                } else {
                    eventList = new ArrayList<>();
                }

                eventList.add(event);
                dayRef.setValue(eventList);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Code
            }
        });

        NavUtils.navigateUpFromSameTask(this);  // go back to main page
    }

    /**
     * display time picker
     */
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

    /**
     * display date picker
     */
    private void showDatePickerDialog() {
        DatePickerFragmentDialog dialog = new DatePickerFragmentDialog();
        dialog.show(getFragmentManager(), "");
    }

    /**
     * display event type picker
     */
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
        System.out.println(event);
    }

    @Override
    public void onLocationSelect(String location) {
        event.setLocation(location);
    }

    @Override
    public void onSelectedEventType(String choice) {
        event.setEventType(choice);
        textViewEventType.setText(choice);
        System.out.println(choice);
    }

    /**
     * receive the description or speech result and store it to event
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

        switch (requestCode) {
            case REQUEST_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //mVoiceInputTv.setText(result.get(0));
                    eventTitle.setText(result.get(0));
                    System.out.println(result);
                }
                break;
            case REQUEST_DESCRIPTION:
                if (resultCode == RESULT_OK && null != data)
                    event.setDescription(data.getStringExtra("description"));

                break;

            case REQUEST_Location:
                if (resultCode == RESULT_OK && null != data)
                    event.setLocation(data.getStringExtra("location"));

                break;
            }

    }

    /**
     * each event needs to have a unique id
     * @return
     */
    private int getEventIdFromSharePreference() {
        int newId = PreferenceManager.getDefaultSharedPreferences(this).getInt("eventId", 0);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("eventId", newId + 1);
        editor.apply();
        System.out.println("new ID!!!!!!!!!!!!!!!");
        System.out.println(newId);
        return newId;
    }

}
