package com.app.cooper.time_manager.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.objects.Event;
import com.app.cooper.time_manager.uilts.FireBaseUtils;
import com.app.cooper.time_manager.uilts.SoftKeyboardUtils;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller for register page
 */
public class AddAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText passwd;
    private TextView warning;
    private FirebaseDatabase database = FireBaseUtils.getDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        SoftKeyboardUtils.hideKeyboardByClicking(this, findViewById(android.R.id.content));

        this.setTitle("Login");
        email = findViewById(R.id.email);
        passwd = findViewById(R.id.password);
        warning = findViewById(R.id.warning);

        mAuth = FirebaseAuth.getInstance();

        TextView signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    /**
     * generate a new account
     * move all events to the new account
     */
    public void signUp() {
        if (!inputCheck()) {
            warning.animate().alpha(1.0f).setDuration(500);
            new WarningThread().start();
            return;
        }

        FirebaseUser oldUser = mAuth.getInstance().getCurrentUser();

        final DatabaseReference eventRef = database.getReference("users/" + oldUser.getUid() + "/events/");

        // load data from the anonymous account
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            Map<String, ArrayList<Event>> events;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    GenericTypeIndicator<Map<String, ArrayList<Event>>> t = new GenericTypeIndicator<Map<String, ArrayList<Event>>>() {};
                    events = snapshot.getValue(t);

                } else {
                    events = new LinkedHashMap<>();
                }

                //dayRef.setValue(events);
                passEventsToNewAccount(events);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Code
            }
        });

        System.out.println(email.toString());
        System.out.println(passwd.toString());

    }

    /**
     * pass all stored events to the new account after the user has successfully registered
     * @param events
     */
    private void passEventsToNewAccount(final Map<String, ArrayList<Event>> events) {
        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), passwd.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sign UP", "createUserWithEmail:success");
                            FirebaseUser newUser = mAuth.getCurrentUser();
                            final DatabaseReference eventRef = database.getReference("users/" + newUser.getUid() + "/events/");
                            eventRef.setValue(events);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Sign UP", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AddAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    /**
     * sign in the user
     * @param view
     */
    public void signIn(View view) {
        if (!inputCheck()) {
            warning.animate().alpha(1.0f).setDuration(500);
            new WarningThread().start();
            return;
        }

        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), passwd.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sign In", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Sign In", "signInWithEmail:failure", task.getException());
                            Toast.makeText(AddAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    /**
     * check whether the input fields are empty
     * @return
     */
    private boolean inputCheck() {
        if(email.getText().toString().equals("") || passwd.getText().toString().equals(""))
            return false;

        return true;
    }

    /**
     * open a background thread and remove warning after 5 seconds
     */
    private class WarningThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(5000);
                warning.animate().alpha(0.0f).setDuration(500);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
