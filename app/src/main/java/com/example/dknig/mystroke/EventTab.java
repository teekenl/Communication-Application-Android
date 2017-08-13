package com.example.dknig.mystroke;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import android.speech.tts.TextToSpeech;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.example.dknig.mystroke.adapter.CalendarAdapter;
import com.example.dknig.mystroke.util.CalendarCollection;

public class EventTab extends AppCompatActivity implements TextToSpeech.OnInitListener{

    // Calendar Variable
    public GregorianCalendar cal_month, cal_month_copy;
    private CalendarAdapter cal_adapter;
    private TextView tv_month;
    Dialog calendar;

    // Variable for event activity use
    String discard_view_event_name;
    int currentItem;
    View discard_view,current_view;
    public static double normalSpeechRate;
    MyDBHandler eventDB;
    MyDBHandler4 listItem;
    CustomEventAdapter base;
    ArrayAdapter<CharSequence> adapter,adapter2;
    public ArrayList<String> eventNameList;
    public ArrayList<Integer> eventImageList;
    public ArrayList<Bitmap>  eventImageList2;
    Datedialog dialog;
    TimeDialog dialog2;
    ArrayList<String> eventName;
    GridView gridview, event_pre;
    EditText editTextName,editTextPlace,editTextTime;
    Custom_List_Event buckyAdapter;
    AlertDialog.Builder eventAddDialog2,deleteDialog,doneDialog,event_pre_Setup,notes_dialog,notes_edit_dialog,discarded_view;
    AlertDialog eventAddDialog,event_pre_Dialog,notes_dialog2,notes_edit_dialog2,discarded_view2;
    AlertDialog.Builder selectedDateView;
    AlertDialog selectedDateView2;
    AlertDialog delete,done;
    ListView buckyListView;
    TextView noEvent,mainTimeName,notesView;
    EditText notesEditText,timepickeredit;
    TextView noSelectedEvent,timepickerview,timepickermaintext;
    ProgressBar loading_spinner,loading_spinner2;
    RelativeLayout mainContainer;
    ArrayList<String> completed;
    View newView;
    String selectedGridDate2;
    Button doneButton;
    Button cancelButton,notesedit, notesdone;
    Button editButton;
    Button event_pre_done;
    ImageView eventPhotoView;
    Bitmap selectedBitmap;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    ImageView deleteView;
    TextView mainEventName,mainPlaceName,mainStaffName,mainRepeatName;
    Handler mHandler;
    String eventID;
    String event_pre_name;
    int event_pre_image;
    int selectedPosition =-1,selectedItem=-1;

    // Variable for speech feature
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_tab);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mainContainer = (RelativeLayout) findViewById(R.id.viewContainer);
        eventDB = new MyDBHandler(this,null,null,1);
        listItem = new MyDBHandler4(this,null,null,1);

        //Text to Speech
        tts = new TextToSpeech(this,this);

        CalendarCollection.date_collection_arr=new ArrayList<CalendarCollection>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");

        adapter2 = ArrayAdapter.createFromResource(this,R.array.repetition,R.layout.spinner_layout);
        adapter2.setDropDownViewResource(R.layout.spinner_layout);


        mHandler = new Handler();
        eventName = new ArrayList<String>();
        loading_spinner = (ProgressBar) findViewById(R.id.loading_spinner);
        loading_spinner.setVisibility(View.GONE);
        loading_spinner2 = (ProgressBar) findViewById(R.id.loading_spinner2);
        loading_spinner2.setVisibility(View.GONE);

        eventImageList2 = new ArrayList<Bitmap>();
        completed = new ArrayList<String>();

        List<Event> eventList = eventDB.allEventList();

        for (int i = 0; i < eventList.size(); i++) {

            if(!eventList.get(i).getEventName().trim().isEmpty() && !eventList.get(i).getId().trim().isEmpty() ) {
                eventName.add(eventList.get(i).getEventName());
                eventImageList2.add(DbBitmapUtility.getImage(eventList.get(i).getEventImage()));
                completed.add(eventList.get(i).getCompleted());
            }
        }

        buckyAdapter = new Custom_List_Event(this,eventName, eventImageList2, completed);

        buckyListView = (ListView) findViewById(R.id.eventListView);
        noEvent = (TextView) findViewById(R.id.noEventView);
        noSelectedEvent = (TextView) findViewById(R.id.noSelectedView);

        deleteView = (ImageView) findViewById(R.id.deleteView);
        init_View_Dialog();

        // List View setup
        buckyListView.setAdapter(buckyAdapter);

        if(buckyListView.getAdapter().getCount()>0) {
            noEvent.setVisibility(View.GONE);
        } else {
            noEvent.setVisibility(View.VISIBLE);
        }

        buckyListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        final String selectedEventName= String.valueOf(parent.getItemAtPosition(position));
                        if (selectedItem != position) {
                            if(editButton.getVisibility()==View.VISIBLE) {
                                selectedItem = position;
                                for (int j = 0; j < parent.getChildCount(); j++) {
                                    View view2 = (View) parent.getChildAt(j);
                                    CheckBox otherBox = (CheckBox) view2.findViewById(R.id.checkedDone);
                                    otherBox.setEnabled(false);
                                    //FloatingActionButton speech = (FloatingActionButton) view2.findViewById(R.id.speechAudio);
                                    //speech.setEnabled(false);
                                }
                                currentItem = position;
                                CheckBox checkBox1 = (CheckBox) view.findViewById(R.id.checkedDone);
                                ImageView buckyViewImage = (ImageView) view.findViewById(R.id.eventIcon);
                                // FloatingActionButton speech = (FloatingActionButton) view.findViewById(R.id.speechAudio);
                                //speech.setEnabled(true);

                                checkBox1.setEnabled(true);
                                Event events;
                                events = eventDB.selectedEventList(selectedEventName);
                                eventID = events.getId();

                                viewItem(buckyViewImage);

                                checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            eventDB.updateEventCompleted(selectedEventName, "true");
                                        } else {
                                            eventDB.updateEventCompleted(selectedEventName, "false");
                                        }
                                    }
                                });

                                if (mainContainer.getChildCount() > 0) {
                                    noSelectedEvent.setVisibility(View.GONE);
                                } else {
                                    noSelectedEvent.setVisibility(View.VISIBLE);
                                }
                            } else {

                                for (int j = 0; j < parent.getChildCount(); j++) {
                                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                                    parent.getChildAt(j).setPressed(false);
                                    View view2 = (View) parent.getChildAt(j);
                                    CheckBox otherBox = (CheckBox) view2.findViewById(R.id.checkedDone);
                                    otherBox.setEnabled(false);
                                    //FloatingActionButton speech = (FloatingActionButton) view2.findViewById(R.id.speechAudio);
                                    //speech.setEnabled(false);
                                }
                                discard_view_event_name = selectedEventName;
                                discard_view = parent.getChildAt(position);
                                current_view = parent.getChildAt(currentItem);
                                discarded_view2.show();
                            }
                        }
                    }
                }

        );

        eventAddDialog2 = new AlertDialog.Builder(EventTab.this);
        deleteDialog = new AlertDialog.Builder(EventTab.this);
        doneDialog = new AlertDialog.Builder(EventTab.this);
        discarded_view= new AlertDialog.Builder(EventTab.this);
        notes_edit_dialog = new AlertDialog.Builder(EventTab.this);
        notes_dialog = new AlertDialog.Builder(EventTab.this);
        event_pre_Setup = new AlertDialog.Builder(EventTab.this);
        selectedDateView = new AlertDialog.Builder(EventTab.this);

        // Event Add Dialog
        LayoutInflater inflater =getLayoutInflater();
        View myview = inflater.inflate(R.layout.eventadd_dialog, null);
        View customTitle = inflater.inflate(R.layout.event_custom_title, null);

        //Delete event dialog
        LayoutInflater inflater2 =getLayoutInflater();
        View customTitle2 = inflater2.inflate(R.layout.delete_custom_title, null);
        //Done event dialog
        LayoutInflater inflater3 =getLayoutInflater();
        View customTitle3 = inflater3.inflate(R.layout.done_custom_title, null);

        // Pre-built Event Dialog
        LayoutInflater inflater4 =getLayoutInflater();
        View event_preView = inflater4.inflate(R.layout.pret_event_add, null);
        View event_pre_custom_Title = inflater4.inflate(R.layout.event_pre_built_title, null);

        // Done Confirmation dialog after notes of event has been added or updated
        LayoutInflater inflater5 = getLayoutInflater();
        View notes_title = inflater5.inflate(R.layout.done_notes_title, null);

        // Done Co
        LayoutInflater inflater6 = getLayoutInflater();
        View notes_edit_title = inflater6.inflate(R.layout.notesedit_title,null);
        View notes_edit_view = inflater6.inflate(R.layout.notes_edit_dialog,null);

        notesedit = (Button) notes_edit_title.findViewById(R.id.notes_enlarge_edit);
        notesdone = (Button) notes_edit_title.findViewById(R.id.notes_enlarge_done);
        notesView = (TextView) notes_edit_view.findViewById(R.id.noteTextView);
        notesEditText = (EditText) notes_edit_view.findViewById(R.id.noteseditText);


        notesedit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesdone.setVisibility(View.VISIBLE);
                notesedit.setVisibility(View.GONE);
                notesEditText.setVisibility(View.VISIBLE);
                notesView.setVisibility(View.GONE);
            }
        });

        notesdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!notesEditText.getText().toString().trim().isEmpty()) {
                    notes_dialog2.show();
                } else {
                    Toast.makeText(EventTab.this,"The content of notes is empty",Toast.LENGTH_SHORT);
                }
            }
        });

        event_pre_done = (Button) event_pre_custom_Title.findViewById(R.id.event_pre_done);
        event_pre_done.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedPosition > -1) {
                            loading_spinner2.animate().alpha(0f).setDuration(2000)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            loading_spinner2.setVisibility(View.GONE);
                                            // Set the content view to 0% opacity but visible, so that it is visible
                                            // (but fully transparent) during the animation.
                                            buckyListView.setAlpha(0f);

                                            // Animate the content view to 100% opacity, and clear any animation
                                            // listener set on the view.
                                            buckyListView.animate()
                                                    .alpha(1f)
                                                    .setListener(null);
                                            buckyListView.setVisibility(View.VISIBLE);
                                        }
                                    });
                            if (selectedPosition == eventNameList.size() - 1) {

                                eventAddDialog.show();
                                event_pre_Dialog.cancel();

                            } else {
                                if(notSameEventName(event_pre_name)) {
                                Event events = new Event();
                                events.setEventPlace("N/A");
                                events.setEventName(event_pre_name);
                                events.setEventImage(DbBitmapUtility.getBytes(selectedBitmap));
                                events.setCompleted("false");
                                events.setEventRepeat("N/A");
                                events.setEventStaff("N/A");
                                events.setDateTime(getTimeNow());
                                events.setNotes("N/A");
                                events.setTime("N/A");
                                eventDB.addEvent(events);

                                eventName.clear();
                                eventImageList2.clear();
                                completed.clear();
                                List<Event> eventList = eventDB.allEventList();

                                for (int i = 0; i < eventList.size(); i++) {

                                    if(!eventList.get(i).getEventName().trim().isEmpty() && !eventList.get(i).getId().trim().isEmpty() ) {
                                        eventName.add(eventList.get(i).getEventName());
                                        eventImageList2.add(DbBitmapUtility.getImage(eventList.get(i).getEventImage()));
                                        completed.add(eventList.get(i).getCompleted());
                                    }
                                }

                                buckyAdapter.notifyDataSetChanged();
                                event_pre_Dialog.cancel();

                            }else {
                                customSameNameDialog();
                            }
                        }

                            if (mainContainer.getChildCount() > 0) {
                                noSelectedEvent.setVisibility(View.GONE);
                            } else {
                                noSelectedEvent.setVisibility(View.VISIBLE);
                            }
                            if (buckyListView.getAdapter().getCount() > 0) {
                                noEvent.setVisibility(View.GONE);
                            } else {
                                noEvent.setVisibility(View.VISIBLE);
                            }

                            selectedPosition = -1;
                        } else {
                            Context context = getApplicationContext();
                            CharSequence text = "Please choose event";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                }
        );
        //List of event
        eventNameList = new ArrayList<String>();
        //List of image
        eventImageList = new ArrayList<Integer>();

        // Pre built list of event
        eventNameList.add("Therapy1");
        eventNameList.add("Therapy2");
        eventNameList.add("Therapy3");
        eventNameList.add("New Event");

        eventImageList.add(R.drawable.therapy1);
        eventImageList.add(R.drawable.therapy2);
        eventImageList.add(R.drawable.therapy3);
        eventImageList.add(R.drawable.ic_pre_event_add3);

        base = new CustomEventAdapter(this, eventNameList,eventImageList);

        // Grid View setup
        event_pre = (GridView)event_preView.findViewById(R.id.event_pre_built);
        event_pre.setAdapter(base);

        event_pre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int j = 0; j < parent.getChildCount(); j++) {

                    View view2 = parent.getChildAt(j);

                    ImageView no_tick = (ImageView) view2.findViewById(R.id.pre_tick);
                    no_tick.setVisibility(View.GONE);

                }
                ImageView selectImageEvent = (ImageView)view.findViewById(R.id.event_image);
                selectedBitmap = ((BitmapDrawable) selectImageEvent.getDrawable()).getBitmap();
                ImageView pre_tick = (ImageView) view.findViewById(R.id.pre_tick);
                pre_tick.setVisibility(View.VISIBLE);
                selectedPosition = position;

                event_pre_name = eventNameList.get(position);

            }
        });

        selectedDateView.setTitle("Select Event")
                .setMessage("Are you sure want to pick up this date and their corresponding event. " +
                        "The results will be display on Event List")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItem = -1;
                        refreshSelectedDateView();
                        dialog.dismiss();
                        calendar.dismiss();
                        mainContainer.removeAllViews();
                        noSelectedEvent.setVisibility(View.VISIBLE);
                    }
                });
        selectedDateView2 = selectedDateView.create();

        // Discard details change
        discarded_view.setTitle("Oops!")
                .setMessage("Are you sure want to discard the information, they will not be saved")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        current_view.setBackgroundColor(Color.parseColor("#f1f1f1"));
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        discard_view.setBackgroundColor(Color.parseColor("#f1f1f1"));


                        CheckBox checkBox1 = (CheckBox) discard_view.findViewById(R.id.checkedDone);
                        ImageView images = (ImageView) discard_view.findViewById(R.id.eventIcon);
                        // FloatingActionButton speech = (FloatingActionButton) view.findViewById(R.id.speechAudio);
                        //speech.setEnabled(true);

                        checkBox1.setEnabled(true);
                        final Event events;
                        events = eventDB.selectedEventList(discard_view_event_name);
                        eventID = events.getId();



                        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    eventDB.updateEventCompleted(discard_view_event_name, "true");
                                } else {
                                    eventDB.updateEventCompleted(discard_view_event_name, "false");
                                }
                            }
                        });

                        if (mainContainer.getChildCount() > 0) {
                            noSelectedEvent.setVisibility(View.GONE);
                        } else {
                            noSelectedEvent.setVisibility(View.VISIBLE);
                        }
                        viewItem(images);
                        selectedItem = -1;
                    }
                });

        discarded_view2 = discarded_view.create();


        //Pre-built dialog show / display
        event_pre_Setup.setView(event_preView);
        event_pre_Setup.setCancelable(false);
        event_pre_Setup.setCustomTitle(event_pre_custom_Title);
        event_pre_Setup.setMessage("Choose A New Event");
        event_pre_Setup.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loading_spinner2.animate().alpha(0f).setDuration(2000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        loading_spinner2.setVisibility(View.GONE);
                                        // Set the content view to 0% opacity but visible, so that it is visible
                                        // (but fully transparent) during the animation.
                                        buckyListView.setAlpha(0f);

                                        // Animate the content view to 100% opacity, and clear any animation
                                        // listener set on the view.
                                        buckyListView.animate()
                                                .alpha(1f)
                                                .setListener(null);
                                        buckyListView.setVisibility(View.VISIBLE);
                                        selectedPosition=-1;
                                    }
                                });
                        if(mainContainer.getChildCount()>0) {
                            noSelectedEvent.setVisibility(View.GONE);
                        } else {
                            noSelectedEvent.setVisibility(View.VISIBLE);
                        }
                        if(buckyListView.getAdapter().getCount()>0) {
                            noEvent.setVisibility(View.GONE);
                        } else {
                            noEvent.setVisibility(View.VISIBLE);
                        }


                        int numVisibleChildren = event_pre.getChildCount();
                        int firstVisiblePosition = event_pre.getFirstVisiblePosition();

                        for ( int i = 0; i < numVisibleChildren; i++ ) {
                            View view = event_pre.getChildAt(i);
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }

                        dialog.dismiss();
                    }
                }
        );

        event_pre_Dialog = event_pre_Setup.create();

        notes_dialog.setCancelable(false)
                .setCustomTitle(notes_title)
                .setMessage("Would you like to update the notes")
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Event event = new Event();
                        event.setId(eventID);
                        event.setNotes(notesEditText.getText().toString());
                        eventDB.updateNotes(event);
                        notesView.setText(notesEditText.getText().toString());
                        notesdone.setVisibility(View.GONE);
                        notesedit.setVisibility(View.VISIBLE);
                        notesView.setVisibility(View.VISIBLE);
                        notesEditText.setText("");
                        notesEditText.setVisibility(View.GONE);


                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        notes_dialog2 = notes_dialog.create();

        notes_edit_dialog.setView(notes_edit_view)
                .setCancelable(false)
                .setCustomTitle(notes_edit_title)
                .setMessage("The notes is shown in the following")
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        notesdone.setVisibility(View.GONE);
                        notesedit.setVisibility(View.VISIBLE);
                        notesView.setVisibility(View.VISIBLE);
                        notesEditText.setText("");
                        notesEditText.setVisibility(View.GONE);

                    }
                });

        notes_edit_dialog2 = notes_edit_dialog.create();

        eventAddDialog2.setView(myview);
        eventAddDialog2.setCancelable(false);
        eventAddDialog2.setCustomTitle(customTitle);
        eventAddDialog2.setMessage("Enter the name of event");
        eventAddDialog2.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText eventEdited = (EditText) eventAddDialog.findViewById(R.id.eventDialogEditedName);
                String eventEdited2 = eventEdited.getText().toString();
                eventEdited.setText("");
                loading_spinner2.animate().alpha(0f).setDuration(2000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                loading_spinner2.setVisibility(View.GONE);
                                // Set the content view to 0% opacity but visible, so that it is visible
                                // (but fully transparent) during the animation.
                                buckyListView.setAlpha(0f);

                                // Animate the content view to 100% opacity, and clear any animation
                                // listener set on the view.
                                buckyListView.animate()
                                        .alpha(1f)
                                        .setListener(null);
                                buckyListView.setVisibility(View.VISIBLE);
                            }
                        });

                if (!eventEdited2.equals("")) {
                    if(notSameEventName(eventEdited2)) {
                    mainContainer.removeAllViews();

                    Event events = new Event();
                    events.setEventImage(DbBitmapUtility.getBytes(selectedBitmap));
                    events.setEventPlace("N/A");
                    events.setEventName(eventEdited2);
                    events.setCompleted("false");
                    events.setEventRepeat("N/A");
                    events.setEventStaff("N/A");
                    events.setDateTime(getTimeNow());
                    events.setNotes("N/A");
                    events.setTime("N/A");
                    eventDB.addEvent(events);

                    eventName.clear();
                    eventImageList2.clear();
                    completed.clear();
                    List<Event> eventList = eventDB.allEventList();

                    for (int i = 0; i < eventList.size(); i++) {

                        if(!eventList.get(i).getEventName().trim().isEmpty() && !eventList.get(i).getId().trim().isEmpty() ) {
                            eventName.add(eventList.get(i).getEventName());
                            eventImageList2.add(DbBitmapUtility.getImage(eventList.get(i).getEventImage()));
                            completed.add(eventList.get(i).getCompleted());
                        }
                    }



                    buckyAdapter.notifyDataSetChanged();
                    listItem.addItemList(eventEdited2);

                    eventAddDialog.cancel();
                    if (mainContainer.getChildCount() > 0) {
                        noSelectedEvent.setVisibility(View.GONE);
                    } else {
                        noSelectedEvent.setVisibility(View.VISIBLE);
                    }
                    if (buckyListView.getAdapter().getCount() > 0) {
                        noEvent.setVisibility(View.GONE);
                    } else {
                        noEvent.setVisibility(View.VISIBLE);
                    }
                } else {
                    customSameNameDialog();
                }
            }

            }
        });

        eventAddDialog2.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText eventEdited = (EditText) eventAddDialog.findViewById(R.id.eventDialogEditedName);
                eventEdited.setText("");
                loading_spinner2.animate().alpha(0f).setDuration(2000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                loading_spinner2.setVisibility(View.GONE);
                                // Set the content view to 0% opacity but visible, so that it is visible
                                // (but fully transparent) during the animation.
                                buckyListView.setAlpha(0f);

                                // Animate the content view to 100% opacity, and clear any animation
                                // listener set on the view.
                                buckyListView.animate()
                                        .alpha(1f)
                                        .setListener(null);
                                buckyListView.setVisibility(View.VISIBLE);
                            }
                        });

                eventAddDialog.cancel();
                event_pre_Dialog.show();
                if(mainContainer.getChildCount()>0) {
                    noSelectedEvent.setVisibility(View.GONE);
                } else {
                    noSelectedEvent.setVisibility(View.VISIBLE);
                }
                if(buckyListView.getAdapter().getCount()>0) {
                    noEvent.setVisibility(View.GONE);
                } else {
                    noEvent.setVisibility(View.VISIBLE);
                }


            }
        });

        eventAddDialog = eventAddDialog2.create();



        // setup delete confirmation dialog
        deleteDialog.setCancelable(false);
        deleteDialog.setCustomTitle(customTitle2);
        deleteDialog.setMessage("Are you sure want to delete this event.");
        deleteDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loading_spinner2.setAlpha(1f);
                loading_spinner2.setVisibility(View.VISIBLE);
                buckyListView.setVisibility(View.GONE);
                loading_spinner.setAlpha(1f);
                loading_spinner.setVisibility(View.VISIBLE);
                mainContainer.setVisibility(View.GONE);
                loading_spinner.animate().alpha(0f).setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                loading_spinner.setVisibility(View.GONE);
                                // Set the content view to 0% opacity but visible, so that it is visible
                                // (but fully transparent) during the animation.
                                mainContainer.setAlpha(0f);

                                // Animate the content view to 100% opacity, and clear any animation
                                // listener set on the view.
                                mainContainer.animate()
                                        .alpha(1f)
                                        .setListener(null);
                                mainContainer.setVisibility(View.VISIBLE);
                            }
                        });
                mainContainer.removeAllViews();
                if(mainContainer.getChildCount()>0) {
                    noSelectedEvent.setVisibility(View.GONE);
                } else {
                    noSelectedEvent.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();

                Event eventDelete = new Event();
                eventDelete.setId(eventID);
                eventDB.deleteEvent(eventDelete);

                eventName.clear();
                eventImageList2.clear();
                completed.clear();
                List<Event> eventList = eventDB.allEventList();

                for (int i = 0; i < eventList.size(); i++) {

                    if(!eventList.get(i).getEventName().trim().isEmpty() && !eventList.get(i).getId().trim().isEmpty() ) {
                        eventName.add(eventList.get(i).getEventName());
                        eventImageList2.add(DbBitmapUtility.getImage(eventList.get(i).getEventImage()));
                        completed.add(eventList.get(i).getCompleted());
                    }
                }


            buckyAdapter.notifyDataSetChanged();

                doneButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                deleteView.setVisibility(View.GONE);

                loading_spinner2.animate().alpha(0f).setDuration(2000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                loading_spinner2.setVisibility(View.GONE);
                                // Set the content view to 0% opacity but visible, so that it is visible
                                // (but fully transparent) during the animation.
                                buckyListView.setAlpha(0f);

                                // Animate the content view to 100% opacity, and clear any animation
                                // listener set on the view.
                                buckyListView.animate()
                                        .alpha(1f)
                                        .setListener(null);
                                buckyListView.setVisibility(View.VISIBLE);
                            }
                        });
                if(buckyListView.getAdapter().getCount()>0) {
                    noEvent.setVisibility(View.GONE);
                } else {
                    noEvent.setVisibility(View.VISIBLE);
                }

            }
        });

        deleteDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        delete = deleteDialog.create();


        deleteView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete.show();

                    }
                }
        );


        // setup done confirmation dialog
        doneDialog.setCancelable(false);
        doneDialog.setCustomTitle(customTitle3);
        doneDialog.setMessage("Would you like to update this event");
        doneDialog.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!editTextName.getText().toString().trim().isEmpty() && !editTextTime.getText().toString().trim().isEmpty()
                                && !editTextPlace.getText().toString().trim().isEmpty()){
                        loading_spinner.setAlpha(1f);
                        loading_spinner.setVisibility(View.VISIBLE);
                        mainContainer.setVisibility(View.GONE);
                        loading_spinner.animate().alpha(0f).setDuration(1000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        loading_spinner.setVisibility(View.GONE);
                                        // Set the content view to 0% opacity but visible, so that it is visible
                                        // (but fully transparent) during the animation.
                                        mainContainer.setAlpha(0f);

                                        // Animate the content view to 100% opacity, and clear any animation
                                        // listener set on the view.
                                        mainContainer.animate()
                                                .alpha(1f)
                                                .setListener(null);
                                        deleteView.setVisibility(View.GONE);
                                        mainContainer.setVisibility(View.VISIBLE);
                                    }
                                });

                        doneButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                        editButton.setVisibility(View.VISIBLE);
                        spinner1.setVisibility(View.GONE);
                        spinner3.setVisibility(View.GONE);
                        editTextName.setVisibility(View.GONE);
                        editTextPlace.setVisibility(View.GONE);
                        String time = editTextTime.getText().toString();
                        editTextTime.setVisibility(View.GONE);

                        Event events = new Event();
                            //Update the event details
                        events.setEventName(editTextName.getText().toString());
                        events.setEventPlace(editTextPlace.getText().toString());
                        events.setEventRepeat(spinner3.getSelectedItem().toString());
                        events.setEventStaff(spinner1.getSelectedItem().toString());
                        events.setDateTime(editTextTime.getText().toString());
                        events.setTime(timepickeredit.getText().toString());
                        events.setId(eventID);
                        Bitmap bitmap = ((BitmapDrawable) eventPhotoView.getDrawable()).getBitmap();
                        events.setEventImage(DbBitmapUtility.getBytes(bitmap));
                        eventDB.updateEvent(events);

                        eventName.clear();
                        eventImageList2.clear();
                        completed.clear();
                        List<Event> eventList = eventDB.allEventList();

                        for (int i = 0; i < eventList.size(); i++) {

                            if(!eventList.get(i).getEventName().trim().isEmpty() && !eventList.get(i).getId().trim().isEmpty() ) {
                                eventName.add(eventList.get(i).getEventName());
                                eventImageList2.add(DbBitmapUtility.getImage(eventList.get(i).getEventImage()));
                                completed.add(eventList.get(i).getCompleted());
                            }
                        }


                        mainTimeName.setVisibility(View.VISIBLE);
                        mainEventName.setVisibility(View.VISIBLE);
                        mainEventName.setText(editTextName.getText().toString());
                        mainPlaceName.setVisibility(View.VISIBLE);
                        mainPlaceName.setText(editTextPlace.getText().toString());
                        mainStaffName.setVisibility(View.VISIBLE);
                        mainStaffName.setText(spinner1.getSelectedItem().toString());
                        mainRepeatName.setVisibility(View.VISIBLE);
                        mainTimeName.setText(editTextTime.getText().toString());
                        mainRepeatName.setText(spinner3.getSelectedItem().toString());
                        timepickeredit.setVisibility(View.GONE);
                        timepickerview.setText(timepickeredit.getText().toString());
                        timepickeredit.setText("");
                        timepickerview.setVisibility(View.VISIBLE);

                        if(selectedItem>-1) {
                            buckyAdapter.notifyDataSetChanged();
                            selectedItem = -1;
                        }
                        if(mainContainer.getChildCount()>0) {
                            noSelectedEvent.setVisibility(View.GONE);
                        } else {
                            noSelectedEvent.setVisibility(View.VISIBLE);
                        }

                        if(buckyListView.getAdapter().getCount()>0) {
                            noEvent.setVisibility(View.GONE);
                        } else {
                            noEvent.setVisibility(View.VISIBLE);
                        }

                            editTextName.setText("");
                            editTextPlace.setText("");
                            editTextTime.setText("");
                    }else
                            {
                            Context context = getApplicationContext();
                            CharSequence text = "The name, date or place  of event is blank!";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                        }
                    }

                }
        );

        doneDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        done = doneDialog.create();

        function_dialog();

    }

    // Check the condition if the name of event is similar to the event list
    public boolean notSameEventName(String selectedName) {
        for(int i = 0; i <eventName.size();i++) {
            if(selectedName.trim().equals(eventName.get(i))) {
                return false;
            }
        }
        return true;
    }

    // Method to get current time
    public String getTimeNow() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(Calendar.getInstance().getTime());

        return date;
    }

    // Error notification dialog to alert user the similar name exist in the event list
    public void customSameNameDialog () {
        event_pre_Dialog.dismiss();
        LayoutInflater flater =getLayoutInflater();
        View eventadd = flater.inflate(R.layout.eventadd_dialog, null);
        final EditText eventname = (EditText)eventadd.findViewById(R.id.eventDialogEditedName);
        AlertDialog.Builder dialog = new AlertDialog.Builder(EventTab.this);
        final AlertDialog dialog2;
        dialog.setTitle("Same Name Conflicts")
                .setView(eventadd)
                .setMessage("There is same or similar name exist in event list.\n" +
                        "Please enter a new name")
                .setPositiveButton("DONE",null)
                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        event_pre_Dialog.show();
                    }
                });
        dialog2 = dialog.create();
        dialog2.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = dialog2.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!eventname.getText().toString().trim().isEmpty()) {
                            Event events = new Event();
                            events.setEventPlace("N/A");
                            events.setEventName(eventname.getText().toString().trim());
                            events.setCompleted("false");
                            events.setEventImage(DbBitmapUtility.getBytes(selectedBitmap));
                            events.setEventRepeat("N/A");
                            events.setEventStaff("N/A");
                            events.setDateTime(getTimeNow());
                            events.setNotes("N/A");
                            events.setTime("N/A");
                            eventDB.addEvent(events);

                            eventName.clear();
                            eventImageList2.clear();
                            completed.clear();
                            List<Event> eventList = eventDB.allEventList();

                            for (int i = 0; i < eventList.size(); i++) {

                                if(!eventList.get(i).getEventName().trim().isEmpty() && !eventList.get(i).getId().trim().isEmpty() ) {
                                    eventName.add(eventList.get(i).getEventName());
                                    eventImageList2.add(DbBitmapUtility.getImage(eventList.get(i).getEventImage()));
                                    completed.add(eventList.get(i).getCompleted());
                                }
                            }


                            buckyAdapter.notifyDataSetChanged();
                            dialog2.dismiss();
                            event_pre_Dialog.dismiss();
                            eventAddDialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog2.show();
    }


    // refresh and show all event
    public void refreshSelectedAllDateView(View view) {
        eventName.clear();
        eventImageList2.clear();
        completed.clear();
        List<Event> eventList = eventDB.allEventList();

        for (int i = 0; i < eventList.size(); i++) {

            if(!eventList.get(i).getEventName().trim().isEmpty() && !eventList.get(i).getId().trim().isEmpty() ) {
                eventName.add(eventList.get(i).getEventName());
                eventImageList2.add(DbBitmapUtility.getImage(eventList.get(i).getEventImage()));
                completed.add(eventList.get(i).getCompleted());
            }
        }

        buckyAdapter.notifyDataSetChanged();

        if(buckyAdapter.getCount()>0) {
            noEvent.setVisibility(View.GONE);
        } else {
            noEvent.setVisibility(View.VISIBLE);
        }

    }

    // refresh and show selected date of event
    public void refreshSelectedDateView() {
        eventName.clear();
        eventImageList2.clear();
        completed.clear();
        List<Event> eventList = eventDB.allSelectedEventList(selectedGridDate2);

        for (int i = 0; i < eventList.size(); i++) {

            if(!eventList.get(i).getEventName().trim().isEmpty() && !eventList.get(i).getId().trim().isEmpty() ) {
                eventName.add(eventList.get(i).getEventName());
                eventImageList2.add(DbBitmapUtility.getImage(eventList.get(i).getEventImage()));
                completed.add(eventList.get(i).getCompleted());
            }
        }

        buckyAdapter.notifyDataSetChanged();

        if(buckyAdapter.getCount()>0) {
            noEvent.setVisibility(View.GONE);
        } else {
            noEvent.setVisibility(View.VISIBLE);
        }

    }

    //Choose the rate or speed of speech
    public void setRateSpeech() {
        startActivity(new Intent(EventTab.this,pop.class));

    }

    // speech function
    public void displaySpeech (View view) {
        Event events;
        int tag = (Integer) view.getTag();
        String eventID2 = eventDB.getEventID(eventName.get(tag));
        events = eventDB.selectedEventList2(eventID2);

        String textToSpeech = "You have";
        textToSpeech += events.getEventName();

        if(!events.getEventStaff().trim().isEmpty() && !events.getEventStaff().trim().equals("N/A")) {
            textToSpeech += "with";
            textToSpeech +=events.getEventStaff();
        }

        if(!events.getEventPlace().trim().isEmpty() && !events.getEventPlace().trim().equals("N/A")) {
            textToSpeech += "in";
            textToSpeech += events.getEventPlace();
        }

        if(!events.getTime().trim().isEmpty() && !events.getTime().trim().equals("N/A")) {
            textToSpeech += "on";
            textToSpeech += events.getTime();
        }

        tts.setSpeechRate((float)normalSpeechRate);
        tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if(result==TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This language is not supported");

            } else {
                // do nothing

            }

        } else {
            Log.e("TTS", "Initialization failed");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_event_menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Navigate back to home page
                onBackPressed();
                return true;
            case R.id.action_add_item:
                addEventItem();
                return true;
            case R.id.action_speech_rate:
                setRateSpeech();
                return true;
            case R.id.action_calendar:
                showCalendar();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Add Event
    public void addEventItem() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(event_pre_Dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        String item = listItem.databaseToString();
        String[] splittedItem = item.split(",");

        if (!item.trim().isEmpty()) {
            eventNameList.clear();;
            eventImageList.clear();
            eventNameList.add("Therapy1");
            eventNameList.add("Therapy2");
            eventNameList.add("Therapy3");
            eventImageList.add(R.drawable.therapy1);
            eventImageList.add(R.drawable.therapy2);
            eventImageList.add(R.drawable.therapy3);
        }

        for (int i = 0; i < splittedItem.length; i++) {
            if (!splittedItem[i].trim().isEmpty()) {
                eventNameList.add(splittedItem[i]);
                eventImageList.add(R.drawable.therapy1);
            }
        }

        if (!item.trim().isEmpty()) {
            eventNameList.add("New Event");
            eventImageList.add(R.drawable.ic_pre_event_add3);
        }
        base.notifyDataSetChanged();

        event_pre_Dialog.show();
        event_pre_Dialog.getWindow().setAttributes(lp);

        loading_spinner2.setAlpha(1f);
        loading_spinner2.setVisibility(View.VISIBLE);
        buckyListView.setVisibility(View.GONE);
        if(noEvent.isShown()) {
            noEvent.setVisibility(View.GONE);
        }

    }

    // show notes function
    public void showNotes(View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(notes_edit_dialog2.getWindow().getAttributes());
        lp.width = 600;
        lp.height = 500;
        lp.x = -20;

        notes_edit_dialog2.show();
        notes_edit_dialog2.getWindow().setAttributes(lp);

    }

    // Show Calendar
    public void showCalendar() {

        // initialize variable
        String nameList = eventDB.eventDateName();
        String dateList = eventDB.eventDateTime();
        String[] splittedName = nameList.split(",");
        String[] splittedDate = dateList.split(",");

        for(int i=0; i<splittedName.length; i++) {
            if(!splittedDate[i].equals("N/A") && !splittedName[i].trim().isEmpty()) {
                CalendarCollection.date_collection_arr.add(new CalendarCollection(splittedDate[i], splittedName[i]));
            }
        }

        if(CalendarCollection.date_collection_arr.size()>0) {
            cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
            cal_month_copy = (GregorianCalendar) cal_month.clone();
            cal_adapter = new CalendarAdapter(this, cal_month, CalendarCollection.date_collection_arr);

            LayoutInflater inflater = getLayoutInflater();
            View calendarView = inflater.inflate(R.layout.activity_calender, null);

            calendar = new AlertDialog.Builder(EventTab.this)
                    .setTitle("Event Calendar")
                    .setMessage("Choose a date")
                    .setView(calendarView)
                    .setCancelable(false)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }

                    ).create();

            calendar.show();
            // date format of calendar
            tv_month = (TextView) calendar.findViewById(R.id.tv_month);
            tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));

            // Setup the button action for calendar
            ImageButton previous = (ImageButton) calendar.findViewById(R.id.ib_prev);

            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPreviousMonth();
                    refreshCalendar();
                }
            });

            ImageButton next = (ImageButton) calendar.findViewById(R.id.Ib_next);
            next.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    setNextMonth();
                    refreshCalendar();

                }
            });

            gridview = (GridView) calendar.findViewById(R.id.gv_calendar);
            gridview.setAdapter(cal_adapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    String selectedGridDate = CalendarAdapter.day_string
                            .get(position);

                    ((CalendarAdapter) parent.getAdapter()).setSelected(v, position, tv_month);

                    String[] separatedTime = selectedGridDate.split("-");
                    String gridvalueString = separatedTime[2].replaceFirst("^0*", "");
                    int gridvalue = Integer.parseInt(gridvalueString);

                    if ((gridvalue > 10) && (position < 8)) {
                        setPreviousMonth();
                        refreshCalendar();
                    } else if ((gridvalue < 7) && (position > 28)) {
                        setNextMonth();
                        refreshCalendar();
                    }

                    selectedGridDate2 = selectedGridDate;
                    int clen = CalendarCollection.date_collection_arr.size();
                    if (clen > 0) {
                        for (int i = 0; i < clen; i++) {
                            if (selectedGridDate.equals(CalendarCollection.date_collection_arr.get(i).date)) {
                                selectedDateView2.show();
                            }
                        }
                    }

                    //((CalendarAdapter) parent.getAdapter()).getPositionList(selectedGridDate, EventTab.this);

                }

            });
        } else {
            new AlertDialog.Builder(EventTab.this)
                    .setTitle("No Appointment Added")
                    .setMessage("There is no appointment at the moment.")
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
        }

    }


    // Next month
    protected void setNextMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMaximum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1),
                    cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) + 1);
        }

    }

    // Previous month
    protected void setPreviousMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMinimum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1),
                    cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) - 1);
        }

    }


    // Refresh Calendar
    public void refreshCalendar() {
        cal_adapter.refreshDays();
        cal_adapter.notifyDataSetChanged();
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));
    }


    // Initialise all important component used in this activity
    public void init_View_Dialog() {

        // Variable for dialog use (adding, viewing, updating event)
        newView = (ViewGroup) getLayoutInflater().inflate(R.layout.main_container,null);
        editButton = (Button) newView.findViewById(R.id.mainEditButton);
        doneButton = (Button) newView.findViewById(R.id.mainDoneButton);
        cancelButton = (Button) newView.findViewById(R.id.mainCancelButton);
        eventPhotoView = (ImageView) newView.findViewById(R.id.mainPhotoView);
        mainEventName = (TextView) newView.findViewById(R.id.MainEditedName);
        mainPlaceName = (TextView) newView.findViewById(R.id.MainEditedPlace);
        mainRepeatName = (TextView) newView.findViewById(R.id.MainEditedRepeat);
        mainStaffName = (TextView) newView.findViewById(R.id.MainEditedStaff);
        mainTimeName = (TextView) newView.findViewById(R.id.MainEditedTime);
        timepickeredit =(EditText) newView.findViewById(R.id.timepickeredit);
        timepickermaintext = (TextView) newView.findViewById(R.id.timepickermaintext);
        timepickerview = (TextView) newView.findViewById(R.id.timepickerview);

        editTextTime = (EditText) newView.findViewById(R.id.editTextTime);
        editTextTime.setInputType(InputType.TYPE_NULL);
        editTextTime.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                   dialog =new Datedialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
            }

        });

        timepickeredit.setInputType(InputType.TYPE_NULL);
        timepickeredit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2 = new TimeDialog(v);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog2.show(ft,"TimePicker");
            }
        });
        editTextPlace = (EditText) newView.findViewById(R.id.editTextPlace);
        editTextName = (EditText) newView.findViewById(R.id.editTextName);

        spinner1 = (Spinner) newView.findViewById(R.id.spinner1);
        MyDBHandler2 db = new MyDBHandler2(this,null,null,1);
        String allStaffList = db.databaseToString();
        String[] splittedStaff = allStaffList.split(",");
        ArrayList<String> staffList = new ArrayList<String>();
        for(int i=0; i<splittedStaff.length; i++) {
            staffList.add(splittedStaff[i]);
        }
        ArrayAdapter staffAdapter = new ArrayAdapter(this,R.layout.spinner_layout,staffList);
        spinner1.setAdapter(staffAdapter);
        if(staffList.size()<=0) {
            spinner1.setEnabled(false);
        } else {
            spinner1.setEnabled(true);
        }

        spinner3 = (Spinner) newView.findViewById(R.id.spinner3);
        spinner3.setAdapter(adapter2);

    }


    public void function_dialog() {

        // Method for editing or updating for particular event
        editButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loading_spinner.setAlpha(1f);
                        loading_spinner.setVisibility(View.VISIBLE);
                        mainContainer.setVisibility(View.GONE);
                        loading_spinner.animate().alpha(0f).setDuration(1000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        loading_spinner.setVisibility(View.GONE);
                                        // Set the content view to 0% opacity but visible, so that it is visible
                                        // (but fully transparent) during the animation.
                                        mainContainer.setAlpha(0f);

                                        // Animate the content view to 100% opacity, and clear any animation
                                        // listener set on the view.
                                        mainContainer.animate()
                                                .alpha(1f)
                                                .setListener(null);
                                        deleteView.setVisibility(View.VISIBLE);
                                        mainContainer.setVisibility(View.VISIBLE);
                                    }
                                });

                        doneButton.setVisibility(View.VISIBLE);
                        cancelButton.setVisibility(View.VISIBLE);
                        editButton.setVisibility(View.GONE);
                        spinner1.setVisibility(View.VISIBLE);
                        editTextTime.setVisibility(View.VISIBLE);
                        spinner3.setVisibility(View.VISIBLE);
                        editTextName.setVisibility(View.VISIBLE);
                        editTextPlace.setVisibility(View.VISIBLE);
                        editTextTime.setText(mainTimeName.getText().toString());
                        editTextName.setText(mainEventName.getText().toString());
                        editTextPlace.setText(mainPlaceName.getText().toString());
                        mainTimeName.setVisibility(View.GONE);
                        mainEventName.setVisibility(View.GONE);
                        mainPlaceName.setVisibility(View.GONE);
                        mainStaffName.setVisibility(View.GONE);
                        mainRepeatName.setVisibility(View.GONE);
                        timepickeredit.setVisibility(View.VISIBLE);
                        timepickeredit.setText(timepickerview.getText().toString());
                        timepickerview.setVisibility(View.GONE);

                        Context context = getApplicationContext();
                        CharSequence text = "Your are in edit mode";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                }
        );

        // Method for cancellationf of updates for particular event
        cancelButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doneButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                        editButton.setVisibility(View.VISIBLE);
                        spinner1.setVisibility(View.GONE);
                        spinner3.setVisibility(View.GONE);
                        editTextName.setVisibility(View.GONE);
                        editTextPlace.setVisibility(View.GONE);
                        mainTimeName.setVisibility(View.VISIBLE);
                        editTextTime.setVisibility(View.GONE);
                        mainEventName.setVisibility(View.VISIBLE);
                        mainPlaceName.setVisibility(View.VISIBLE);
                        mainStaffName.setVisibility(View.VISIBLE);
                        mainRepeatName.setVisibility(View.VISIBLE);
                        deleteView.setVisibility(View.GONE);
                        timepickeredit.setVisibility(View.GONE);
                        timepickerview.setVisibility(View.VISIBLE);

                        if (mainContainer.getChildCount() > 0) {
                            noSelectedEvent.setVisibility(View.GONE);
                        } else {
                            noSelectedEvent.setVisibility(View.VISIBLE);
                        }

                        if (buckyListView.getAdapter().getCount() > 0) {
                            noEvent.setVisibility(View.GONE);
                        } else {
                            noEvent.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );


        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        done.show();
                    }
                }
        );


        eventPhotoView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage3();
                    }
                }
        );
    }

    // view the particular details of event
    public void viewItem(ImageView image) {

        spinner1.setSelection(0);
        spinner3.setSelection(0);
        editTextTime.setText("");
        timepickeredit.setText("");
        eventPhotoView.setImageDrawable(image.getDrawable());

        if(mainContainer.getChildCount()<=0) {
            Event events;
            events = eventDB.selectedEventList2(eventID);
            String notes = events.getNotes();
            notesView.setText(notes);
            mainEventName.setText(events.getEventName());
            mainTimeName.setText(events.getDateTime());
            mainPlaceName.setText(events.getEventPlace());
            if(!events.getEventRepeat().trim().isEmpty()) {
                mainRepeatName.setText(events.getEventRepeat());
            } else {
                mainRepeatName.setText("N/A");
            }

            if(!events.getEventStaff().trim().isEmpty()) {
                mainStaffName.setText(events.getEventStaff());
            } else {
                mainStaffName.setText("N/A");
            }
            timepickerview.setText(events.getTime());
            mainContainer.addView(newView);

        } else {
            mainContainer.removeAllViews();

            Event events;
            events = eventDB.selectedEventList2(eventID);
            String notes = events.getNotes();
            notesView.setText(notes);

            mainEventName.setText(events.getEventName());
            mainTimeName.setText(events.getDateTime());
            mainPlaceName.setText(events.getEventPlace());
            if(!events.getEventRepeat().trim().isEmpty()) {
                mainRepeatName.setText(events.getEventRepeat());
            } else {
                mainRepeatName.setText("N/A");
            }

            if(!events.getEventStaff().trim().isEmpty()) {
                mainStaffName.setText(events.getEventStaff());
            } else {
                mainStaffName.setText("N/A");
            }
            timepickerview.setText(events.getTime());
            mainContainer.addView(newView);

        }

        noSelectedEvent.setVisibility(View.GONE);
        doneButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);
        deleteView.setVisibility(View.GONE);
        spinner1.setVisibility(View.GONE);
        editTextTime.setVisibility(View.GONE);
        spinner3.setVisibility(View.GONE);
        editTextName.setVisibility(View.GONE);
        editTextPlace.setVisibility(View.GONE);
        timepickeredit.setVisibility(View.GONE);
        timepickerview.setVisibility(View.VISIBLE);
        mainTimeName.setVisibility(View.VISIBLE);
        mainEventName.setVisibility(View.VISIBLE);
        mainPlaceName.setVisibility(View.VISIBLE);
        mainStaffName.setVisibility(View.VISIBLE);
        mainRepeatName.setVisibility(View.VISIBLE);

    }

    // Choose the option to select images
    public void selectImage3() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EventTab.this);
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


    // Collect result from response of intent activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check the condition of result
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bundle extras = data.getExtras();
                Bitmap photo = (Bitmap) extras.get("data");
                eventPhotoView.setImageBitmap(photo);
            } else if (requestCode == 2 && data!=null) {
                try {
                    Uri URI = data.getData();
                    Bitmap bitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), URI);

                    eventPhotoView.setImageBitmap(bitmap);
                } catch(Exception e) {
                    Toast.makeText(EventTab.this,"Please try it again",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}
