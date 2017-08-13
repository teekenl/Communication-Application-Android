package com.example.dknig.mystroke;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

// Create a dialog that allow user to select or pick up the date

@SuppressLint("ValidFragment")
public class Datedialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    EditText txtdate;

    public Datedialog(View view){
        txtdate=(EditText)view;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {


// Use the current date as the default date in the dialog
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    // Create a new instance of DatePickerDialog and return it
    return new DatePickerDialog(getActivity(), this, year, month, day);


}

public void onDateSet(DatePicker view, int year, int month, int day) {
    //show to the selected date in the text box
    String date;
    if(month+1<10) {
        date = year + "-0" + (month + 1) + "-" + day;
    } else {
        date = year + "-" + (month + 1) + "-" + day;
    }
    txtdate.setText(date);
}



}
