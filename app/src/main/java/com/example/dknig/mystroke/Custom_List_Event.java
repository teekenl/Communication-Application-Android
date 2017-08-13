package com.example.dknig.mystroke;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.FloatRange;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dknig.mystroke.util.CalendarCollection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Custom_List_Event extends ArrayAdapter<String> {

    ArrayList<Bitmap> images;
    ArrayList<String> completed;
    private static final String TAG = "EventTab";

    public Custom_List_Event(Context context, List<String> eventName, ArrayList<Bitmap> images, ArrayList<String> completed) {
        super(context, R.layout.activity_list_view_event, eventName);
        this.completed = completed;
        this.images = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater buckysInflater = LayoutInflater.from(getContext());
        View customView = buckysInflater.inflate(R.layout.activity_list_view_event, parent, false);
        final String singleEventItem = getItem(position);

        TextView buckyText = (TextView) customView.findViewById(R.id.eventTextView);
        ImageView buckyView = (ImageView) customView.findViewById(R.id.eventIcon);

        CheckBox checkBox1 = (CheckBox) customView.findViewById(R.id.checkedDone);
        Button speech  = (Button) customView.findViewById(R.id.speechAudio);
        speech.setTag(position);

        buckyView.setImageBitmap(images.get(position));

        if(completed.get(position).equals("true")) {
            checkBox1.setChecked(true);
        } else {
            checkBox1.setChecked(false);
        }
        checkBox1.setEnabled(false);
        //speech.setEnabled(false);
        buckyText.setText(singleEventItem);

        return customView;
    }

    public String getTimeNow() {
        DateFormat df = new SimpleDateFormat("EEE, MMM / d");
        String date = df.format(Calendar.getInstance().getTime());

        return date;
    }
}
