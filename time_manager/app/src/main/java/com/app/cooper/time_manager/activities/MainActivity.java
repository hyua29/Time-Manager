package com.app.cooper.time_manager.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.cooper.time_manager.uilts.AlarmReceiver;
import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.custom.views.pickers.EventPickerDialog;
import com.app.cooper.time_manager.custom.views.monthview.CurrentDayDecorator;
import com.app.cooper.time_manager.custom.views.monthview.EventDecorator;
import com.app.cooper.time_manager.objects.Event;
import com.app.cooper.time_manager.uilts.FireBaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.model.Events;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Reference: https://developers.google.com/calendar/quickstart/android
 */
public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;
    private GoogleAccountCredential googleAccountCredential;

    private MaterialCalendarView calendarMonthView;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private ProgressBar progressBarMainPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.createNotificationChannel(this);
        this.setNotificationAlarm();
        this.setupElements();

        googleAccountCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(GoogleAPIChecking.SCOPES))
                .setBackOff(new ExponentialBackOff());

        firebaseDatabase = FireBaseUtils.getDatabase();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user == null) {
            this.loginAnonymousUser();
        } else {
            this.loadEventDays();
            this.renderMonthView();
        }

        this.setSupportActionBar(toolbar);

        CalendarDay currentDate = calendarMonthView.getCurrentDate();
        updateDateOnTitle(String.valueOf(currentDate.getYear()), parseMonth(currentDate.getMonth()));  // update title when page changed

        this.loadGoogleAccount();
        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        this.hasPermissions(MainActivity.this, PERMISSIONS);
    }

    /**
     * load google account from shared preference
     */
    private void loadGoogleAccount() {
        String accountName = getSharedPreferences(GoogleAPIChecking.PREF, Context.MODE_PRIVATE).
                getString(GoogleAPIChecking.PREF_ACCOUNT_NAME,  null);
        googleAccountCredential.setSelectedAccountName(accountName);
    }

    /**
     * set decorator on the view
     * show dialog where events are shown by clicking the date tag
     * change title against current date
     */
    private void renderMonthView() {
        System.out.println(calendarMonthView);
        calendarMonthView.setSelectionColor(getResources().getColor(R.color.none));
        calendarMonthView.addDecorator(new CurrentDayDecorator(getResources().getColor(R.color.colorPrimary)));
        calendarMonthView.setTopbarVisible(false);  // no title and arrows
        calendarMonthView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
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

    private void setupElements() {
        progressBarMainPage = findViewById(R.id.progressBarMainPage);
        toolbar = findViewById(R.id.mainToolbar);
        calendarMonthView = findViewById(R.id.calendarMonthView);
    }

    /**
     * sign in the user if the user does not have an account
     */
    private void loginAnonymousUser() {

        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Anonymous sign in", "signInAnonymously:success");
                            user = firebaseAuth.getCurrentUser();
                            loadEventDays();
                            renderMonthView();
                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAA");
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
        if(user!=null)
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

        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_event:
                 intent = new Intent(this, AddEventActivity.class);
                this.startActivity(intent);
                break;
            case R.id.account:
                intent = new Intent(this, ShowAccountActivity.class);
                this.startActivity(intent);
                break;
            case R.id.sync:
                new MakeRequestTask(googleAccountCredential).execute();
                this.loadEventDays();
                break;

            case R.id.credits:
                intent = new Intent(this, CreditActivity.class);
                this.startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * change the bar title to current calendar year and month
     * @param title
     * @param subtitle
     */
    private void updateDateOnTitle(String title, String subtitle) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
    }



    /**
    parse month to string
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
        System.out.println(user);
        System.out.println(firebaseDatabase);
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

    /**
     * display event dialog when the user clicks a particular day
     * @param c
     */
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

    /**
     * register a service that sends notifications to the user one day before events take place
     */
    private void setNotificationAlarm() {
    alarmMgr = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
    alarmIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);

    // alert user at 7:30 a.m.
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.set(Calendar.HOUR_OF_DAY, 7);
    calendar.set(Calendar.MINUTE, 30);

    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                    calendar.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY, alarmIntent);

    }

    /**
     * a channel is required to send notification to users
     * @param c
     */
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

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     * get events from google calendar and store to firebase
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<Event>> {
        private com.google.api.services.calendar.Calendar mService;
        private Exception mLastError = null;

        private MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<com.app.cooper.time_manager.objects.Event> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<com.app.cooper.time_manager.objects.Event> getDataFromApi() throws IOException {

            List<com.app.cooper.time_manager.objects.Event> eventList = new ArrayList<>();

            Events events = mService.events().list("primary")  // get events on google calendar
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<com.google.api.services.calendar.model.Event> items = events.getItems();

            for (com.google.api.services.calendar.model.Event event : items) {  // convert google event object into the way this app can read
                com.app.cooper.time_manager.objects.Event e = new com.app.cooper.time_manager.objects.Event();

                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }

                Calendar cs = Calendar.getInstance();
                cs.setTimeInMillis(start.getValue());

                e.setEventName(event.getSummary());
                e.setStartDay(cs.get(Calendar.DAY_OF_MONTH));
                e.setStartMonth(cs.get(Calendar.MONTH));
                e.setStartYear(cs.get(Calendar.YEAR));
                e.setStartHour(cs.get(Calendar.HOUR_OF_DAY));
                e.setStartMinute(cs.get(Calendar.MINUTE));


                DateTime end = event.getEnd().getDateTime();
                if (end == null)
                    end = event.getEnd().getDate();

                Calendar ce = Calendar.getInstance();
                ce.setTimeInMillis(end.getValue());

                e.setEndHour(ce.get(Calendar.HOUR_OF_DAY));
                e.setEndMinute(ce.get(Calendar.MINUTE));

                eventList.add(e);
            }
            return eventList;
        }


        @Override
        protected void onPreExecute() {
            progressBarMainPage.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<com.app.cooper.time_manager.objects.Event> output) {
            progressBarMainPage.setVisibility(View.INVISIBLE);
            if (output == null || output.size() == 0) {
                //mOutputText.setText("No results returned.");
                showError("There is no Events on google calendar");
            } else {
                for (com.app.cooper.time_manager.objects.Event e : output)  // save to firebase
                    saveEventToFirebase(e);
                Toast.makeText(MainActivity.this, "Sync successful",
                        Toast.LENGTH_SHORT).show();
                System.out.println(output);
            }
        }

        @Override
        protected void onCancelled() {
            progressBarMainPage.setVisibility(View.INVISIBLE);
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    GoogleAPIChecking checking = new GoogleAPIChecking(MainActivity.this);
                    checking.showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ShowAccountActivity.REQUEST_AUTHORIZATION);
                } else {
                    showError(mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
                System.out.println("Request cancelled");
            }
        }
    }

    /**
     * save event to firebase
     * @param event
     */
    private void saveEventToFirebase(final com.app.cooper.time_manager.objects.Event event) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dayRef = database.getReference("users/" + user.getUid() + "/events/" + event.getDateStamp());

        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<com.app.cooper.time_manager.objects.Event> eventList;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    GenericTypeIndicator<List<com.app.cooper.time_manager.objects.Event>> t = new GenericTypeIndicator<List<com.app.cooper.time_manager.objects.Event>>() {};
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
    }

    /**
     * display error message if failed to login
     * @param errorMessage
     */
    private void showError(String errorMessage) {
        AlertDialog.Builder saveAlert = new AlertDialog.Builder(this);
        saveAlert.setTitle("Oops!");
        saveAlert.setMessage(errorMessage);
        saveAlert.setPositiveButton("Confirm", null);
        saveAlert.create().show();
    }

    /**
     * check whether this application has the permission and redirect user to setting if permission not granted
     * @param context
     * @param permissions
     * @return
     */
    public boolean hasPermissions(Context context, String[] permissions) {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                    listPermissionsNeeded.add(permission);
                }
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

}
