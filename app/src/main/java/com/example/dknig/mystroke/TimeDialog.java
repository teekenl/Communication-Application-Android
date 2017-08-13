package com.example.dknig.mystroke;

/**
 * Created by dknig on 2/10/2016.
 */


import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

// Create a dialog that allow user to select or pick up the time

@SuppressLint("ValidFragment")
public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    EditText timeedited;
    public TimeDialog(View view) {
        timeedited = (EditText) view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the day, month and year from calendar (Initialization)
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),this,hour,min,false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Change the format of time after the hours exceeds 12 (For example, 24 changed to be 12)
        if(hourOfDay>12) {
            hourOfDay=hourOfDay-12;
            timeedited.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).append(" PM"));
        } else {
            timeedited.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).append(" AM"));
        }


    }
}
