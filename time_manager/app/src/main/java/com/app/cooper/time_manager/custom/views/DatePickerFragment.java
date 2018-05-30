package com.app.cooper.time_manager.custom.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.app.cooper.time_manager.R;

import java.util.Calendar;

/**
 * A date picker
 */
public class DatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.datePicker, (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);

        return datePickerDialog;
    }


}
