package com.example.dknig.mystroke;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    MyDBHandler3 moodDB;
    String currentDate;
    MyDBHandler5 logDB;
    CircleImageView buckysImageView;
    ImageButton buckysImageButton;
    TextView nameView;
    Dialog nameEditDialog;
    Dialog feelingDialog;
    Handler customHandler;
    private ProgressBar loading_spinner;
    private View mainLayout;
    private int mShortAnimationDuration;
    private long currentTime;
    public Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization of staff database (MyDBHandler3)
        moodDB = new MyDBHandler3(this,null,null,1);
        // Initialization of staff database (MyDBHandler5)
        logDB = new MyDBHandler5(this,null,null,1);

        // Create view for main activity
        setContentView(R.layout.activity_main);

        // Set orientation view for main activity
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        buckysImageView = (CircleImageView) findViewById(R.id.personalView);
        nameView = (TextView) findViewById(R.id.NameView);
        currentTime = System.currentTimeMillis();
        myHandler = new Handler();

        loading_spinner = (ProgressBar) findViewById(R.id.loading_spinner);

        mainLayout = findViewById(R.id.mainlayout);
        mainLayout.setVisibility(View.GONE);
        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_longAnimTime);

        // Initialization of notification view
        final Dialog feelingDialog = new Dialog(MainActivity.this);
        feelingDialog.setContentView(R.layout.mood_dialog);
        feelingDialog.setTitle("Get Started On Your Day");
        feelingDialog.setCancelable(false);

        // there are a lot of settings, for dialog, check them all

        //set up Button
        ImageButton greatButton = (ImageButton) feelingDialog.findViewById(R.id.greatButton);
        ImageButton goodButton  = (ImageButton) feelingDialog.findViewById(R.id.goodButton);
        ImageButton okButton = (ImageButton) feelingDialog.findViewById(R.id.okButton);
        ImageButton badButton = (ImageButton) feelingDialog.findViewById(R.id.badButton);
        ImageButton worseButton = (ImageButton) feelingDialog.findViewById(R.id.worseButton);

        currentDate = specificDateTime();

        greatButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feelingDialog.dismiss();
                        moodDB.addMood("great",specifiedDateFormat());
                        LogDate log = new LogDate("great", specificDateTime());
                        logDB.addLog(log);
                    }
                }
        );
        goodButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feelingDialog.dismiss();
                        moodDB.addMood("good",specifiedDateFormat());
                        LogDate log = new LogDate("good", specificDateTime());
                        logDB.addLog(log);
                    }
                }
        );
        okButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feelingDialog.dismiss();
                        moodDB.addMood("ok",specifiedDateFormat());
                        LogDate log = new LogDate("ok", specificDateTime());
                        logDB.addLog(log);
                    }
                }
        );
        badButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feelingDialog.dismiss();
                        moodDB.addMood("bad",specifiedDateFormat());
                        LogDate log = new LogDate("bad", specificDateTime());
                        logDB.addLog(log);
                    }
                }
        );
        worseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feelingDialog.dismiss();
                        moodDB.addMood("worse",specifiedDateFormat());
                        LogDate log = new LogDate("worse", specificDateTime());
                        logDB.addLog(log);
                    }
                }
        );


        Button button = (Button) feelingDialog.findViewById(R.id.skipButton);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feelingDialog.dismiss();
                    }
                }
        );

        // Get date or time of last recorded log from log database (MyDBHandler5)
        String date = logDB.lastLog();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date afterDate;
        Date beforeDate;

        if(date.trim().isEmpty()) {
            feelingDialog.show();
        } else if(!date.trim().isEmpty()) {
            try {
                afterDate = df.parse(currentDate);
                beforeDate = df.parse(date);
                beforeDate.setHours(beforeDate.getHours()+6);
                if (beforeDate.before(afterDate)) {
                    feelingDialog.show();
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // animation for loading
        loading_spinner.animate()
                .alpha(0f)
                .setDuration(10000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loading_spinner.setVisibility(View.GONE);
                        // Set the content view to 0% opacity but visible, so that it is visible
                        // (but fully transparent) during the animation.
                        mainLayout.setAlpha(0f);


                        // Animate the content view to 100% opacity, and clear any animation
                        // listener set on the view.
                        mainLayout.animate()
                                .alpha(1f)
                                .setDuration(1000)
                                .setListener(null);
                        mainLayout.setVisibility(View.VISIBLE);
                    }
                });

        nameEditDialog = new Dialog(MainActivity.this);
        nameEditDialog.setContentView(R.layout.editname);
        nameEditDialog.setTitle("");
        nameEditDialog.setCancelable(true);

    }

    // Method for formatting time
    public String specifiedDateFormat(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(Calendar.getInstance().getTime());

        return date;
    }

    // Second Method for formatting time
    public String specificDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    // navigate to Event Tab
    public void eventTab(View view) {
        Intent intent = new Intent(this, EventTab.class);
        startActivity(intent);
    }

    // navigate to Staff Tab
    public void staffTab(View view) {
        Intent intent = new Intent(this, StaffTab.class);
        startActivity(intent);
    }

    //navigate to Activities Tab
    public void activityTab(View view) {
        Intent intent = new Intent(this, AcitivityTab.class);
        startActivity(intent);
    }

    //navigate to Graph Tab
    public void moodTab(View view) {
        Intent intent = new Intent(this, MoodTab.class);
        startActivity(intent);
    }

    // edit Name when on Click
    public void editNameTab(View view) {
        //set up Button
        Button button = (Button) nameEditDialog.findViewById(R.id.nameDoneButton);
        Button button2 = (Button) nameEditDialog.findViewById(R.id.nameCancelButton);
        final EditText nameEdit = (EditText) nameEditDialog.findViewById(R.id.nameEditText);
        nameEdit.setText("");
        nameEdit.setHint("Enter Your Name");

        // set button for editing the name when clicking the profile name
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String legitName = nameEdit.getText().toString();
                        String EditedName = nameEdit.getText().toString().toLowerCase();

                        if(legitName.length()>10) {
                            Context context = getApplicationContext();
                            CharSequence text = "The name is too long";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                        } else if(!EditedName.contains("enter")&& !EditedName.contains("your") && !EditedName.contains("name")
                                && !EditedName.isEmpty()) {
                            nameEditDialog.dismiss();

                            nameView.setText(legitName);
                            Context context = getApplicationContext();
                            CharSequence text = "Your Name has been changed successfully!";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            Context context = getApplicationContext();
                            CharSequence text = "Please enter valid name";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                }
        );


        button2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nameEditDialog.dismiss();
                    }
                }
        );
        nameEditDialog.show();
    }

    // Method used to take photo or uploading from gallery (for profile use)
    public void selectImage(View view) {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // Collect the result from response of intent activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
               Bundle extras = data.getExtras();
                Bitmap photo = (Bitmap) extras.get("data");
                buckysImageView.setImageBitmap(photo);
            } else if (requestCode == 2 && data!=null) {
                try {
                Uri URI = data.getData();
                Bitmap bitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), URI);

                buckysImageView.setImageBitmap(bitmap);
                } catch(Exception e) {
                    Toast.makeText(MainActivity.this,"Please try it again",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}

