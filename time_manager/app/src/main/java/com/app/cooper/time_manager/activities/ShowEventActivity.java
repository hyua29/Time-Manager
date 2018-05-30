package com.app.cooper.time_manager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.decorator.EventDecorator;
import com.app.cooper.time_manager.objects.Event;
import com.app.cooper.time_manager.uilts.FireBaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * display the event on the screen
 */
public class ShowEventActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private Event e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        firebaseDatabase = FireBaseUtils.getDatabase();

        Toolbar toolbar = findViewById(R.id.addEventToolbar);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.readInAttributes();
    }

    /**
     * read attributes from 'e' and display it on the screen
     */
    private void readInAttributes() {
        Intent intent = getIntent();
        e = intent.getParcelableExtra("eventSelected");

        TextView title = findViewById(R.id.title);
        title.setText(e.getEventName());

        TextView dateToShow = findViewById(R.id.dateToShow);
        dateToShow.setText(e.getDate());

        TextView timeToShow = findViewById(R.id.timeToShow);
        timeToShow.setText(e.getStartTime());

        TextView timeToShow1 = findViewById(R.id.timeToShow1);
        timeToShow1.setText(e.getEndTime());

        TextView eventType = findViewById(R.id.eventType);
        eventType.setText(e.getEventType());

        TextView location = findViewById(R.id.location);
        if (e.getLocation().length()>0)
            location.setText(e.getLocation());
        else
            location.setText("none");

        TextView description = findViewById(R.id.description);
        if (e.getDescription().length()>0)
            description.setText(e.getDescription());
        else
            description.setText("none");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      Intent intent;
        switch (item.getItemId()) {
            case R.id.edit_event:
                intent = new Intent(this, AddEventActivity.class);
                intent.putExtra("event", e);
                startActivity(intent);
            case R.id.delete_event:
                deleteEvent();
                System.out.println("ro delete page");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * display delete warning dialog
     * remove event from the database if the action is confirmed
     */
    private void deleteEvent() {
        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(ShowEventActivity.this);
        deleteAlert.setTitle("Delete Event?");
        deleteAlert.setMessage("Are you sure you wanna delete this event?");
        deleteAlert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference eventRef = firebaseDatabase.getReference("users/" + user.getUid() + "/events/" + e.getDateStamp() + "/");

                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    List<Event> events;
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        GenericTypeIndicator<List<Event>> t = new GenericTypeIndicator<List<Event>>() {};
                        events = snapshot.getValue(t);
                        for(int i=0;i<events.size();i++) {
                            if (events.get(i).getId() == e.getId()) {
                                events.remove(i);
                            }

                        }
                        if(events.size() == 0)
                            eventRef.removeValue();
                        else
                            eventRef.setValue(events);

                        NavUtils.navigateUpFromSameTask(ShowEventActivity.this);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Code
                    }
                });
            }

        });
        deleteAlert.create().show();
    }

}
