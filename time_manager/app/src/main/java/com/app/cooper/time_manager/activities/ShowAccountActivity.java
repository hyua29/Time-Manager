package com.app.cooper.time_manager.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cooper.time_manager.R;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/** TODO: show failed feedback
 * Reference: https://developers.google.com/identity/sign-in/android/sign-in
 * https://developers.google.com/calendar/quickstart/android
 *
 * display all the accounts this user has logged in
 */
public class ShowAccountActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private GoogleAccountCredential mCredential;
    private TextView firebaseAccount;
    private TextView googleAccount;

    private TextView newFireBaseAccount;
    private TextView newGoogleAccount;

    private ProgressBar mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_account);

        this.setSupportActionBar((Toolbar) findViewById(R.id.addAccountToolbar));
        getSupportActionBar().setTitle("Account");

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(GoogleAPIChecking.SCOPES))
                .setBackOff(new ExponentialBackOff());

        mProgress = findViewById(R.id.progressBar);

        this.displayLoggedInAccounts();
        this.setUpListeners();

    }

    /**
     * initialize listeners for all clickable entities on the screen
     */
    private void setUpListeners() {
        newFireBaseAccount = findViewById(R.id.newFireBaseAccount);
        newFireBaseAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddAccountActivity.class);
                startActivity(intent);
            }
        });

        newGoogleAccount = findViewById(R.id.newGoogleAccount);
        newGoogleAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove account that has been stored locally
                SharedPreferences.Editor editor = getSharedPreferences(GoogleAPIChecking.PREF, MODE_PRIVATE).edit();
                editor.putString(GoogleAPIChecking.PREF_ACCOUNT_NAME, null);
                editor.apply();

                chooseAccount();

            }
        });

    }

    /**
     * check what account the user has logged in and display them on the screen
     */
    private void displayLoggedInAccounts() {
        firebaseAccount = findViewById(R.id.firebaseAccount);
        firebaseAccount.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        googleAccount = findViewById(R.id.googleAccount);
        googleAccount.setText(getSharedPreferences(GoogleAPIChecking.PREF, Context.MODE_PRIVATE).
                        getString(GoogleAPIChecking.PREF_ACCOUNT_NAME,  null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.displayLoggedInAccounts();
    }


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        GoogleAPIChecking checking = new GoogleAPIChecking(this);
        if (! checking.isGooglePlayServicesAvailable()) {
            checking.acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! checking.isDeviceOnline()) {
            Toast.makeText(ShowAccountActivity.this, "No network connection available.",
                    Toast.LENGTH_SHORT).show();
        } else {
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getSharedPreferences(GoogleAPIChecking.PREF, Context.MODE_PRIVATE).
                    getString(GoogleAPIChecking.PREF_ACCOUNT_NAME,  null);

            // get it from local preference
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                System.out.println("From localllllllllllllll");
                System.out.println(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
                mProgress.setVisibility(View.VISIBLE);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(ShowAccountActivity.this, "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getSharedPreferences(GoogleAPIChecking.PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(GoogleAPIChecking.PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }


}
