package com.example.dknig.mystroke;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/* Customization adapter for event list use  (Adapter Helper Class in Event Tab) */

public class CustomEventAdapter extends BaseAdapter {

    ArrayList<String> result;
    Context context2;
    ArrayList<Integer> imageId;
    private static LayoutInflater inflater=null;

    public CustomEventAdapter(Context context, ArrayList<String> eventNameList, ArrayList<Integer> eventImageList) {

        // TODO Auto-generated constructor stub
        result = eventNameList;
        context2 = context;
        imageId = eventImageList;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return result.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder
        {
            TextView tv;
            ImageView img;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.pre_event_details, null);
            holder.tv=(TextView) rowView.findViewById(R.id.event_name);
            holder.img=(ImageView) rowView.findViewById(R.id.event_image);

            holder.tv.setText(result.get(position));
            holder.img.setImageResource(imageId.get(position));

            return rowView;
        }

}
