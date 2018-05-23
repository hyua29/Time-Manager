package com.app.cooper.time_manager.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.cooper.time_manager.AlarmReceiver;
import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.custom.views.EventPickerDialog;
import com.app.cooper.time_manager.decorator.CurrentDayDecorator;
import com.app.cooper.time_manager.decorator.EventDecorator;
import com.app.cooper.time_manager.objects.Event;
import com.app.cooper.time_manager.services.NotificationService;
import com.app.cooper.time_manager.uilts.FireBaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MaterialCalendarView calendarMonthView;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.createNotificationChannel(this);
        //Intent serviceIntent = new Intent(this, NotificationService.class);
        //startService(serviceIntent);
        //startService(serviceIntent);
        this.setNotificationAlarm();


        firebaseDatabase = FireBaseUtils.getDatabase();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            this.loginAnonymousUser();
            System.out.println("nulllllllllllllll user ");
        } else
            System.out.println(user.getUid());

        toolbar = findViewById(R.id.mainToolbar);
        calendarMonthView = findViewById(R.id.calendarMonthView);
        //calendarMonthView.setSelectionMode(SELECTION_MODE_NONE);
        calendarMonthView.setSelectionColor(getResources().getColor(R.color.none));
        //---------------------- testing
        this.loadEventDays();
        //-------------------------
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
                getEventsAgainstDate(date);
            }
        });

    }

    private void loginAnonymousUser() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Anonymous sign in", "signInAnonymously:success");
                            user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Anonymous sign in", "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loadEventDays();
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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_event:
                 intent = new Intent(this, AddEventActivity.class);
                //this.startActivityForResult(intent, ADD_PERSON_REQUEST);
                this.startActivity(intent);
                break;
            case R.id.account:
                intent = new Intent(this, ShowAccountActivity.class);
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
            case 0:
                month = getString(R.string.Jan);
                break;
            case 1:
                month = getString(R.string.Feb);
                break;
            case 2:
                month = getString(R.string.Mar);
                break;
            case 3:
                month = getString(R.string.Apr);
                break;
            case 4:
                month = getString(R.string.May);
                break;
            case 5:
                month = getString(R.string.Jun);
                break;
            case 6:
                month = getString(R.string.Jul);
                break;
            case 7:
                month = getString(R.string.Aug);
                break;
            case 8:
                month = getString(R.string.Sep);
                break;
            case 9:
                month = getString(R.string.Otc);
                break;
            case 10:
                month = getString(R.string.Nov);
                break;
            case 11:
                month = getString(R.string.Dec);
                break;
            default:
                month = "error";
                break;
        }
        return month;
    }

    /**
     * load event day at start up
     */
    private void loadEventDays() {
        final DatabaseReference dayRef = firebaseDatabase.getReference("users/" + user.getUid() + "/events/");

        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            Collection<CalendarDay> days = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String[] key = postSnapshot.getKey().split("-");

                    if (key.length != 3)
                        continue;
                    int year = Integer.parseInt(key[0]);
                    int month = Integer.parseInt((key[1]));
                    int day = Integer.parseInt((key[2]));
                    days.add(new CalendarDay(year, month, day));
                }

                DayViewDecorator eventDecorator = new EventDecorator(getResources().getColor(R.color.CyanWater), days);
                calendarMonthView.addDecorator(eventDecorator);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Code
            }
        });
    }

    private void getEventsAgainstDate(CalendarDay c) {
        String key = c.getYear() + "-" + c.getMonth() + "-" + c.getDay();
        final DatabaseReference dayRef = firebaseDatabase.getReference("users/" + user.getUid() + "/events/" + key);

        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Event> days;
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                GenericTypeIndicator<List<Event>> t = new GenericTypeIndicator<List<Event>>() {};
                days = snapshot.getValue(t);

                if (days == null || days.size() == 0)
                    return;

                EventPickerDialog dialog = new EventPickerDialog();
                dialog.setEventList(days);
                dialog.show(getFragmentManager(), "");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Code
            }
        });
    }

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private void setNotificationAlarm() {

    alarmMgr = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
    alarmIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);

    // Set the alarm to start at 8:30 a.m.
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.set(Calendar.HOUR_OF_DAY, 7);
    calendar.set(Calendar.MINUTE, 30);

    // setRepeating() lets you specify a precise custom interval--in this case,
    // 20 minutes.
    //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),1000 * 60 * 20, alarmIntent);
        //TODO: replace this
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                        5000, alarmIntent);
    System.out.println("AAAAAAAAAAAAAAAAA");

    }

    private void createNotificationChannel(Context c) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = c.getString(R.string.channel_name);
            String description = c.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = c.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
