package com.app.cooper.time_manager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.objects.Event;

public class ShowEventActivity extends AppCompatActivity {

    private Event e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Intent intent = getIntent();
        e = intent.getParcelableExtra("eventSelected");
        System.out.println(e);
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


}
