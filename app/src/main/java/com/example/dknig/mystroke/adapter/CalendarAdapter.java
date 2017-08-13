package com.example.dknig.mystroke.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dknig.mystroke.R;
import com.example.dknig.mystroke.util.CalendarCollection;

/* Initialize calendar adapter for calendar view  (EventTab Helper Class) */
public class CalendarAdapter extends BaseAdapter {
    private Context context;

    private java.util.Calendar month;
    public GregorianCalendar pmonth;

    /**
     * calendar instance for previous month for getting complete view
     */

    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay;
    int maxWeeknumber;
    int maxP;
    int calMaxP;
    int lastWeekDay;
    int leftDays;
    int mnthlength;
    String itemvalue, curentDateString;
    DateFormat df;
    TextView previous1, previous2;
    View currentView;
    String str_output,str_output2;

    private ArrayList<String> items;
    public static List<String> day_string;
    private View previousView;
    public ArrayList<CalendarCollection>  date_collection_arr;

    public CalendarAdapter(Context context, GregorianCalendar monthCalendar,ArrayList<CalendarCollection> date_collection_arr) {
        this.date_collection_arr=date_collection_arr;
        CalendarAdapter.day_string = new ArrayList<String>();
        Locale.setDefault(Locale.US);
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        this.context = context;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();

    }

    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    // Get number count of day of month in calendar
    public int getCount() {
        return day_string.size();
    }

    // Get number position of day of month
    public Object getItem(int position) {
        return day_string.get(position);
    }

    // Get number
    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.cal_item, null);

        }


        dayView = (TextView) v.findViewById(R.id.date);
        String[] separatedTime = day_string.get(position).split("-");


        String gridvalue = separatedTime[2].replaceFirst("^0*", "");
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            dayView.setTextColor(Color.parseColor("#FF80AB"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(Color.parseColor("#FF80AB"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            // setting curent month's days in blue color.
            dayView.setTextColor(Color.BLACK);
        }


        if (day_string.get(position).equals(curentDateString)) {
            dayView.setTextColor(Color.WHITE);
            v.setBackgroundColor(Color.parseColor("#f06292"));
        } else {
            v.setBackgroundColor(Color.WHITE);
        }


        dayView.setText(gridvalue);

        // create date string for comparison
        String date = day_string.get(position);

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        // show icon if date is not empty and it exists in the items array
		/*ImageView iw = (ImageView) v.findViewById(R.id.date_icon);
		if (date.length() > 0 && items != null && items.contains(date)) {
			iw.setVisibility(View.VISIBLE);
		} else {
			iw.setVisibility(View.GONE);
		}
		*/

        setEventView(v, position,dayView);

        return v;
    }

    public void parseCalendar(String text) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
            SimpleDateFormat formatTarget = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatTarget.format(format.parse(text));
            String[] sdate = date.split("-");
            str_output = sdate[0].replaceFirst("^0*", "");
            str_output2 = sdate[1].replaceFirst("^0*", "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // set different layout for selected view in calendar
    public View setSelected(View view,int pos, TextView tvmonth) {
        previous1 = (TextView) view.findViewById(R.id.date);
        String text = tvmonth.getText().toString();

        parseCalendar(text);

        if (previousView != null && previous2!=null && !previous1.getText().toString().equals(previous2.getText().toString())){
            previousView.setBackgroundColor(Color.WHITE);
            TextView previousText = (TextView) previousView.findViewById(R.id.date);
            previousText.setTextColor(Color.BLACK);
            String previousDate = previousText.getText().toString();
            int clen = CalendarCollection.date_collection_arr.size();

            for (int i = 0; i < clen; i++) {
                CalendarCollection obj = CalendarCollection.date_collection_arr.get(i);
                String date = obj.date;
                String[] sdate = date.split("-");
                String ssdate = sdate[2].replaceFirst("^0*", "");
                String ssdate2 = sdate[1].replaceFirst("^0*", "");
                String ssdate3 = sdate[0].replaceFirst("^0*", "");
                if (ssdate.equals(previousDate) && str_output.equals(ssdate3) && str_output2.equals(ssdate2)) {
                    previousText.setTextColor(Color.WHITE);
                    previousView.setBackgroundResource(R.drawable.rounded_calender_item);
                    break;
                }

        }
        }

        previous1.setTextColor(Color.WHITE);
        view.setBackgroundColor(Color.parseColor("#f06292"));

        int len=day_string.size();
        if (len>pos) {
            if (day_string.get(pos).equals(curentDateString)) {

            }else{

                previousView = view;

            }

        }

        if(previousView!=null) {
            previous2 = (TextView) previousView.findViewById(R.id.date);
        }
        return view;
    }

    public void refreshDays() {
        // clear items
        items.clear();
        day_string.clear();
        Locale.setDefault(Locale.US);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        /**
         * Calendar instance for getting a complete gridview including the three
         * month's (previous,current,next) dates.
         */
        pmonthmaxset = (GregorianCalendar) pmonth.clone();
        /**
         * setting the start date as previous month's required date.
         */
        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        /**
         * filling calendar gridview.
         */
        for (int n = 0; n < mnthlength; n++) {

            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            day_string.add(itemvalue);

        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }




    public void setEventView(View v,int pos,TextView txt){

        int len=CalendarCollection.date_collection_arr.size();
        for (int i = 0; i < len; i++) {
            CalendarCollection cal_obj=CalendarCollection.date_collection_arr.get(i);
            String date=cal_obj.date;
            int len1=day_string.size();
            if (len1>pos) {

                if (day_string.get(pos).equals(date) && !day_string.get(pos).equals(curentDateString)) {
                    v.setBackgroundResource(R.drawable.rounded_calender_item);
                    txt.setTextColor(Color.WHITE);
                }
            }}



    }

    public void getPositionList(String date,final Activity act){

        int len=CalendarCollection.date_collection_arr.size();
        for (int i = 0; i < len; i++) {
            CalendarCollection cal_collection=CalendarCollection.date_collection_arr.get(i);
            String event_date=cal_collection.date;

            String event_message=cal_collection.event_message;

            if (date.equals(event_date)) {

                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Date: "+event_date)
                        .setMessage("Event: "+event_message)
                        .setPositiveButton("OK",new android.content.DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            }else{


            }}



    }

}