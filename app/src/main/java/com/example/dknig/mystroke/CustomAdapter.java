package com.example.dknig.mystroke;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/* Customization adapter for staff list use  (Adapter Helper Class in Staff Tab) */

public class CustomAdapter extends ArrayAdapter<String> {

    Context context2;
    ArrayList<Bitmap> imageList;
    public CustomAdapter(Context context, List<String> staffNameList, ArrayList<Bitmap> imageList) {

        super(context, R.layout.activity_staff_details, staffNameList);
        this.imageList = imageList;
        context2 = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater buckysInflater = LayoutInflater.from(getContext());
        View customView = buckysInflater.inflate(R.layout.activity_staff_details, parent, false);
        String name = getItem(position);
        TextView buckyText = (TextView) customView.findViewById(R.id.staff_name);
        ImageView buckyView = (ImageView) customView.findViewById(R.id.staff_image);
        if(imageList.get(position)!=null) {
            buckyView.setImageBitmap(imageList.get(position));
        } else {
            buckyView.setImageResource(R.drawable.head_photo);
        }
        buckyText.setText(name);

        return customView;
    }


}
