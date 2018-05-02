package com.app.cooper.time_manager.custom.views;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.app.cooper.time_manager.R;

public class EventTypePickerDialog extends DialogFragment {

    private OnEventTypeSelectListener mCallback;
    //private TextView work;
    //private TextView family;
    //private TextView holiday;
    //private TextView general;
    private String choice;

    public interface OnEventTypeSelectListener {

        void onSelectedEventType(String choice);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.event_type_picker, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final TextView work = (TextView) dialogView.findViewById(R.id.work);
                final TextView family = (TextView) dialogView.findViewById(R.id.family);
                final TextView holiday = (TextView) dialogView.findViewById(R.id.holiday);
                final TextView general = (TextView) dialogView.findViewById(R.id.general);
                work.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choice = (String) work.getText();
                        returnResult();

                    }
                });
                family.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choice = (String) family.getText();
                        returnResult();
                    }
                });
                holiday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choice = (String) holiday.getText();
                        returnResult();
                    }
                });
                general.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choice = (String) general.getText();
                        returnResult();
                    }
                });
            }
        });
        return alertDialog;
    }

    private void returnResult() {
        dismiss();
        mCallback.onSelectedEventType(choice);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (EventTypePickerDialog.OnEventTypeSelectListener) activity;
        }
        catch (ClassCastException e)
        {
            Log.d("MyDialog", "Activity doesn't implement the interface");
        }
    }
}
