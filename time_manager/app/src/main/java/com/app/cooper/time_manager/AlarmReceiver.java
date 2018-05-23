package com.app.cooper.time_manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.app.cooper.time_manager.activities.MainActivity;
import com.app.cooper.time_manager.custom.views.EventPickerDialog;
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

import java.util.Calendar;
import java.util.List;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class AlarmReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALARM!!!!!!!!!!!!!!!!", Toast.LENGTH_LONG).show();
        getEventsForTmr(context);

    }



    private void createNotification(Context c, String title, String content) {
        Intent intent = new Intent(c, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c, "notification")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(VISIBILITY_PUBLIC)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
        notificationManager.notify(1, mBuilder.build());
    }

    private void getEventsForTmr(final Context c) {

        FirebaseDatabase firebaseDatabase = FireBaseUtils.getDatabase();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
            return;


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        CalendarDay calendarDay = new CalendarDay(calendar.getTime());

        String key = calendarDay.getYear() + "-" + calendarDay.getMonth() + "-" + calendarDay.getDay();
        final DatabaseReference dayRef = firebaseDatabase.getReference("users/" + user.getUid() + "/events/" + key);

        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Event> days;
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                GenericTypeIndicator<List<Event>> t = new GenericTypeIndicator<List<Event>>() {};
                days = snapshot.getValue(t);

                if (days == null || days.size() == 0)
                    return;

                StringBuilder result = new StringBuilder();
                for(Event e : days)
                    result.append(e.getEventName()).append("\n");

                System.out.println("--------------------");
                System.out.println(result);
                createNotification(c, "Tomorrow's Events", result.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Code
            }
        });
    }
}
