package com.app.cooper.time_manager.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.app.cooper.time_manager.R;
import com.app.cooper.time_manager.uilts.SoftKeyboardUtils;

public class AddDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_description);

        SoftKeyboardUtils.hideKeyboardByClicking(this, findViewById(android.R.id.content));

    }

    public void saveDescription(View view) {
        EditText description = findViewById(R.id.description);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("description", description.getText().toString());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void cancelDescription(View view) {
        finish();
    }
}
