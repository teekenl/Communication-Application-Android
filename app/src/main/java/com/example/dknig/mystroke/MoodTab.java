package com.example.dknig.mystroke;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class MoodTab extends AppCompatActivity {

    MyDBHandler3 moodDB;
    TextView mainTitle;
    ImageView image1,image2,image3,image4,image5;
    GraphView moodGraph;
    TextView view;
    TextView view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_mood);

        // set the orientation of mood activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Initialization of textview, imageview
        view = (TextView) findViewById(R.id.noGraphView);
        view2 = (TextView) findViewById(R.id.noGraphView2);
        mainTitle = (TextView) findViewById(R.id.mainTitleGraph);
        image1 = (ImageView) findViewById(R.id.graphimageview1);
        image2 = (ImageView) findViewById(R.id.graphimageview2);
        image3 = (ImageView) findViewById(R.id.graphimageview3);
        image4 = (ImageView) findViewById(R.id.graphimageview4);
        image5 = (ImageView) findViewById(R.id.graphimageview5);
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
        image4.setVisibility(View.GONE);
        image5.setVisibility(View.GONE);
        mainTitle.setVisibility(View.GONE);

        // Initialization mood database (MyDBHandler3)
        moodDB = new MyDBHandler3(this,null,null,1);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        final int width = dm.widthPixels;
        final int height = dm.heightPixels;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");
        // setup graph
        moodGraph = (GraphView)  findViewById(R.id.graph);
        moodGraph.setVisibility(View.GONE);
        ProgressBar loading = (ProgressBar)findViewById(R.id.mood_loading_spinner);

        // Loading animation goes here
        loading.animate()
                .alpha(0f)
                .setDuration(5000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // set the graph initially invisible
                        moodGraph.setAlpha(0f);

                        moodGraph.animate()
                                .alpha(1f)
                                .setDuration(2000)
                                .setListener(null);

                        moodGraph.setVisibility(View.VISIBLE);

                        // Retrieve the details of mood include the type and time from database
                        String moodName = moodDB.databasetoString();
                        String[] splittedName = moodName.split(",");
                        String moodTime = moodDB.databasetoString2();
                        String[] splittedTime = moodTime.split(",");
                        DataPoint[] data;

                        //Check at least two data point
                        if(splittedName.length>=2 && splittedTime.length==splittedName.length) {
                           data = new DataPoint[splittedName.length];

                            for(int i=0; i< splittedName.length; i++) {
                                // check both condition are not empty
                                if(!splittedName[i].trim().isEmpty()&& !splittedTime[i].isEmpty()) {
                                    Integer moodValue = 0;
                                    Date d2 = new Date();
                                    Calendar c = Calendar.getInstance();
                                    String[] splittedDate = splittedTime[i].split("-");

                                    // Set mood value as point in graph
                                    if(splittedName[i].trim().equals("great")){
                                        moodValue = 5;
                                    }
                                    if(splittedName[i].trim().equals("good")) {
                                        moodValue = 4;
                                    }
                                    if(splittedName[i].trim().equals("ok")) {
                                        moodValue = 3;
                                    }
                                    if(splittedName[i].trim().equals("bad")) {
                                        moodValue = 2;
                                    }
                                    if(splittedName[i].trim().equals("worse")) {
                                        moodValue = 1;
                                    }

                                    //set up date
                                    c.set(Calendar.DAY_OF_MONTH,Integer.parseInt(splittedDate[2]));
                                    c.set(Calendar.YEAR,Integer.parseInt(splittedDate[0]));
                                    c.set(Calendar.MONTH,Integer.parseInt(splittedDate[1]));
                                    d2 = c.getTime();
                                    data[i] = new DataPoint(d2, moodValue);

                                }
                            }

                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(data);
                            moodGraph.removeAllSeries();
                            moodGraph.addSeries(series);
                            moodGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(MoodTab.this));
                            moodGraph.getGridLabelRenderer().setNumHorizontalLabels(splittedName.length);
                            moodGraph.getViewport().setXAxisBoundsManual(true);
                            // Set X-Axis Label
                            moodGraph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
                            moodGraph.getGridLabelRenderer().setHorizontalAxisTitleTextSize((float)(height*.03));
                            moodGraph.getGridLabelRenderer().setVerticalAxisTitleTextSize((float)(height*.03));
                            //moodGraph.setBackgroundColor(Color.GRAY);
                            moodGraph.getGridLabelRenderer().setGridColor(Color.GRAY);
                            //Set Y-Axis Label
                            moodGraph.getGridLabelRenderer().setVerticalAxisTitle("Mood");
                            moodGraph.getGridLabelRenderer().setHumanRounding(false);
                            // Set minimum and maximum limit of Y-Axiss
                            moodGraph.getViewport().setMinY(0);
                            moodGraph.getViewport().setMaxY(5);
                            moodGraph.getViewport().setYAxisBoundsManual(true);
                            moodGraph.setVisibility(View.VISIBLE);
                            view.setVisibility(View.GONE);
                            view.setVisibility(View.GONE);
                            image1.setVisibility(View.VISIBLE);
                            image2.setVisibility(View.VISIBLE);
                            image3.setVisibility(View.VISIBLE);
                            image4.setVisibility(View.VISIBLE);
                            image5.setVisibility(View.VISIBLE);
                            mainTitle.setVisibility(View.VISIBLE);

                        } else {
                            moodGraph.setVisibility(View.GONE);
                            image1.setVisibility(View.GONE);
                            image2.setVisibility(View.GONE);
                            image3.setVisibility(View.GONE);
                            image4.setVisibility(View.GONE);
                            image5.setVisibility(View.GONE);
                            view.setVisibility(View.VISIBLE);
                            view2.setVisibility(View.VISIBLE);
                            mainTitle.setVisibility(View.GONE);
                        }

                    }
                });

    }

    // Initialization purpose (not in use)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_mood_menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
