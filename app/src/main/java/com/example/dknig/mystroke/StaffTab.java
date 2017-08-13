package com.example.dknig.mystroke;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StaffTab extends AppCompatActivity {

    GridView staffView;
    Button staffDeleteView;
    Spinner titleSpinner;
    EditText editedStaff, editedStaff2;
    ArrayList<String> staffNameList;
    ArrayList<Bitmap> staffImageList;
    ArrayAdapter<String> base;
    AlertDialog.Builder staffDialog,staffEditedDialog, deletedEditedDialog;
    AlertDialog staffDialog2,staffEditedDialog2, deletedEditedDialog2;
    TextView noStaff;
    View myView2;
    ImageView headView,newHeadView;
    EditText editFirst,editLast;
    TextView staffTitle,staffFirst,staffLast;
    ArrayAdapter<CharSequence>adapter,adapter2,adapter3;
    Spinner spinners,rolespinner;
    Button editEnlargeDone, editEndlargeEdit;
    MyDBHandler2 staffDB;
    int selectedStaff=-1;
    String staffID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the orientation view for staff activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // set the back navigation button to be true
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");

        // Create view for staff activity
        setContentView(R.layout.activity_staff_tab);

        // Initialization of staff database (MyDBHandler2)
        staffDB = new MyDBHandler2(this,null,null,1);

        noStaff = (TextView) findViewById(R.id.withNoStaff);

        // Name and Image List of staff
        staffNameList = new ArrayList<String>();
        staffImageList = new ArrayList<Bitmap>();


        // Title and role option in the spinner
        adapter = ArrayAdapter.createFromResource(this,R.array.titleName,R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        adapter2 = ArrayAdapter.createFromResource(this,R.array.roleName,R.layout.spinner_layout);
        adapter2.setDropDownViewResource(R.layout.spinner_layout);
        adapter3 = ArrayAdapter.createFromResource(this,R.array.titleName,R.layout.spinner_layout);
        adapter3.setDropDownViewResource(R.layout.spinner_layout);

        // Retrieve existing staff from database
        List<Staff> staffList = staffDB.allStaffList();

            for (int i = 0; i < staffList.size(); i++) {

                if(!staffList.get(i).getFirstName().trim().isEmpty() && !staffList.get(i).getId().trim().isEmpty() ) {
                staffNameList.add(fullNametitle(staffList.get(i).getTitle(),staffList.get(i).getFirstName()));
                staffImageList.add(DbBitmapUtility.getImage(staffList.get(i).getBitmap()));
                }
            }

        // Adapter for list of staff (for gridview use)
        base = new CustomAdapter(this, staffNameList, staffImageList);
        staffView = (GridView) findViewById(R.id.staff_grid);
        staffView.setAdapter(base);

        // set the no staff message to be visible if the number of staff is larger than 0
        if(staffView.getAdapter().getCount()>0) {
            noStaff.setVisibility(View.GONE);
        } else {
            noStaff.setVisibility(View.VISIBLE);
        }


        // Alert Dialog used to view staffs
        staffView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView image = (ImageView)view.findViewById(R.id.staff_image);
                selectedStaff = position;
                TextView first = (TextView) view.findViewById(R.id.staff_name);

                Staff staffs;

                String first2 = first.getText().toString();
                String[] splitted = first2.split(Pattern.quote("."));
                staffs = staffDB.selectedStaffList(splitted[1]);

                headView.setImageDrawable(image.getDrawable());
                staffID = staffs.getId();
                staffTitle.setText(staffs.getTitle());
                staffFirst.setText(staffs.getFirstName());
                staffLast.setText(staffs.getLastName());

                int width = getResources().getDimensionPixelSize(R.dimen.staffAddWidth);
                int height = getResources().getDimensionPixelSize(R.dimen.staffAddheight);
                staffEditedDialog2.setView(null);
                staffEditedDialog2.setView(myView2);
                staffEditedDialog2.show();
                staffEditedDialog2.getWindow().setLayout(width,height);
            }
        });


        // Dialog for adding the staff
        staffDialog = new AlertDialog.Builder(StaffTab.this);
        LayoutInflater inflater = getLayoutInflater();
        View myview = inflater.inflate(R.layout.activity_staff_dialog, null);
        View customTitle = inflater.inflate(R.layout.staff_custom_title, null);

        // Dialog for editing, updating, viewing the staff
        staffEditedDialog = new AlertDialog.Builder(StaffTab.this);
        LayoutInflater inflater2 = getLayoutInflater();
        myView2 = inflater2.inflate(R.layout.activity_staff_enlarge_dialog,null);
        View customTitle2 = inflater2.inflate(R.layout.staff_enlarge_custom_title, null);

        // delete confirmation dialog for selected staff
        deletedEditedDialog = new AlertDialog.Builder(StaffTab.this);
        LayoutInflater inflater3 = getLayoutInflater();
        View customTitle3 = inflater3.inflate(R.layout.delete_custom_title2, null);

        // Initialization of textview, imageview, editview goes here
        spinners = (Spinner) myview.findViewById(R.id.spinner4);
        rolespinner =  (Spinner) myview.findViewById(R.id.spinnerRole);
        rolespinner.setAdapter(adapter2);
        newHeadView = (ImageView) myview.findViewById(R.id.staffAddPhoto);
        editedStaff2 = (EditText) myview.findViewById(R.id.staffEditedLast);
        editedStaff = (EditText) myview.findViewById(R.id.staffEditedName);
        spinners.setAdapter(adapter);

        titleSpinner = (Spinner)  myView2.findViewById(R.id.staffTitleSpinner);
        titleSpinner.setAdapter(adapter3);
        editFirst = (EditText) myView2.findViewById(R.id.staffDetailsNameEdited);
        editLast = (EditText) myView2.findViewById(R.id.staffDetailsNameEditedLast);

        headView = (ImageView)myView2.findViewById(R.id.staff_enlarge_view);
        staffTitle = (TextView) myView2.findViewById(R.id.staffDetailsNameTitle);
        staffFirst = (TextView) myView2.findViewById(R.id.staffDetailsMainNameView);
        staffLast = (TextView) myView2.findViewById(R.id.staffDetailNameMainViewLast);

        editEndlargeEdit = (Button)customTitle2.findViewById(R.id.staff_enlarge_edit);
        editEnlargeDone = (Button) customTitle2.findViewById(R.id.staff_enlarge_done);
        staffDeleteView = (Button) customTitle2.findViewById(R.id.staffDeleteView);

        // set onclick listener to the view which used to delete staff
        staffDeleteView.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                deletedEditedDialog2.show();
            }
        });

        //set onclick listener to the view which used to edit staff
        editEndlargeEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editFirst.setVisibility(View.VISIBLE);
                        editLast.setVisibility(View.VISIBLE);
                        staffTitle.setVisibility(View.GONE);
                        staffFirst.setVisibility(View.GONE);
                        staffLast.setVisibility(View.GONE);
                        editEndlargeEdit.setVisibility(View.GONE);
                        editEnlargeDone.setVisibility(View.VISIBLE);
                        staffDeleteView.setVisibility(View.VISIBLE);
                        titleSpinner.setVisibility(View.VISIBLE);
                        titleSpinner.setSelection(0);
                        editFirst.setText("");
                        editLast.setText("");
                        Context context = getApplicationContext();
                        CharSequence editToggle = "You are now in edit mode";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, editToggle, duration);
                        toast.show();
                    }
                }
        );


        //set onclick listener to the view which used to finish the updating process for particular staff
        editEnlargeDone.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String testingText = editFirst.getText().toString();
                        if(!testingText.trim().isEmpty()) {
                        titleSpinner.setVisibility(View.GONE);
                        editFirst.setVisibility(View.GONE);
                        editLast.setVisibility(View.GONE);
                        staffTitle.setVisibility(View.VISIBLE);
                        staffFirst.setVisibility(View.VISIBLE);

                        staffTitle.setText(titleSpinner.getSelectedItem().toString());
                        staffFirst.setText(editFirst.getText().toString());
                        staffLast.setText(editLast.getText().toString());
                        staffLast.setVisibility(View.VISIBLE);

                        Staff staffs = new Staff();
                        staffs.setTitle(titleSpinner.getSelectedItem().toString());
                        staffs.setFirstName(editFirst.getText().toString());
                        staffs.setLastName(editLast.getText().toString());
                        staffs.setId(staffID);
                        Bitmap bitmap = ((BitmapDrawable) headView.getDrawable()).getBitmap();
                        staffs.setBitmap(DbBitmapUtility.getBytes(bitmap));
                        staffDB.updateStaff(staffs);

                        staffNameList.clear();
                        staffImageList.clear();

                        // Retrieve new updated list of staff details after updates of particular staff
                        List<Staff> staffList = staffDB.allStaffList();

                        for (int i = 0; i < staffList.size(); i++) {

                            if(!staffList.get(i).getFirstName().trim().isEmpty() && !staffList.get(i).getId().trim().isEmpty() ) {
                                staffNameList.add(fullNametitle(staffList.get(i).getTitle(),staffList.get(i).getFirstName()));
                                staffImageList.add(DbBitmapUtility.getImage(staffList.get(i).getBitmap()));
                            }
                        }

                        // notify the adapter that data has been changed
                        base.notifyDataSetChanged();

                        editEndlargeEdit.setVisibility(View.VISIBLE);
                        editEnlargeDone.setVisibility(View.GONE);
                        staffDeleteView.setVisibility(View.GONE);

                        Context context = getApplicationContext();
                        CharSequence editToggle = "The update has been done successfully";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, editToggle, duration);
                        toast.show();
                    } else {
                            Context context = getApplicationContext();
                            CharSequence editToggle = "The first name shouldn't blank";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, editToggle, duration);
                            toast.show();

                        }
                    }
                }
        );

        // Delete confirmation dialog of particular staff
        deletedEditedDialog.setCustomTitle(customTitle3)
                .setCancelable(false)
                .setTitle("Delete Staff")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedStaff=-1;
                        editFirst.setVisibility(View.GONE);
                        editLast.setVisibility(View.GONE);
                        staffTitle.setVisibility(View.VISIBLE);
                        staffFirst.setVisibility(View.VISIBLE);
                        staffLast.setVisibility(View.VISIBLE);
                        editEndlargeEdit.setVisibility(View.VISIBLE);
                        editEnlargeDone.setVisibility(View.GONE);
                        staffDeleteView.setVisibility(View.GONE);
                        titleSpinner.setVisibility(View.GONE);

                        Staff staffs = new Staff();
                        staffs.setId(staffID);

                        staffDB.deleteStaff(staffs);

                        staffNameList.clear();
                        staffImageList.clear();

                        // Retrieve new updated list of staff details after deletion
                        List<Staff> staffList = staffDB.allStaffList();

                        for (int i = 0; i < staffList.size(); i++) {

                            if(!staffList.get(i).getFirstName().trim().isEmpty() && !staffList.get(i).getId().trim().isEmpty() ) {
                                staffNameList.add(fullNametitle(staffList.get(i).getTitle(),staffList.get(i).getFirstName()));
                                staffImageList.add(DbBitmapUtility.getImage(staffList.get(i).getBitmap()));
                            }
                        }

                        base.notifyDataSetChanged();

                        editFirst.setText("");
                        editLast.setText("");
                        titleSpinner.setSelection(0);

                        if(staffView.getAdapter().getCount()>0) {
                            noStaff.setVisibility(View.GONE);
                        } else {
                            noStaff.setVisibility(View.VISIBLE);
                        }
                        staffEditedDialog2.dismiss();
                    }
                });

        deletedEditedDialog2 = deletedEditedDialog.create();

        // customization of staff editing dialog (used to edit or update staff)
        staffEditedDialog.setView(myView2)
                .setCustomTitle(customTitle2)
                .setCancelable(false)
                .setTitle("Update Staff")
                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedStaff=-1;
                        editFirst.setVisibility(View.GONE);
                        editLast.setVisibility(View.GONE);
                        staffTitle.setVisibility(View.VISIBLE);
                        staffFirst.setVisibility(View.VISIBLE);
                        staffLast.setVisibility(View.VISIBLE);
                        editEndlargeEdit.setVisibility(View.VISIBLE);
                        editEnlargeDone.setVisibility(View.GONE);
                        staffDeleteView.setVisibility(View.GONE);
                        titleSpinner.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });


        staffEditedDialog2 = staffEditedDialog.create();

        // customization of staff adding dialog (used to add new staff)
        staffDialog.setView(myview)
                .setCancelable(false)
                .setCustomTitle(customTitle)
                .setMessage("Please enter name of staff")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        editedStaff.setText("");
                        editedStaff2.setText("");
                        spinners.setSelection(0);
                        rolespinner.setSelection(0);
                    }
                })
                .setPositiveButton("DONE", null);

        staffDialog2 = staffDialog.create();

        // Customization of positive button with extra condition comparison for the alert dialog (Adding staff)
        staffDialog2.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button b = staffDialog2.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!editedStaff.getText().toString().trim().isEmpty()) {

                            String text = spinners.getSelectedItem().toString();
                            String firstText = editedStaff.getText().toString();
                            String lastText = editedStaff2.getText().toString();
                            String text2 = rolespinner.getSelectedItem().toString();
                            String staffName = text+"."+editedStaff.getText().toString();


                            Staff staffs = new Staff();
                            staffs.setTitle(text);
                            staffs.setFirstName(firstText);
                            staffs.setLastName(lastText);
                            Bitmap bitmap = ((BitmapDrawable) newHeadView.getDrawable()).getBitmap();
                            staffs.setBitmap(DbBitmapUtility.getBytes(bitmap));
                            staffDB.addStaff(staffs);

                            staffNameList.clear();
                            staffImageList.clear();

                            // Retrieve new updated list of staff details after addition of staff
                            List<Staff> staffList = staffDB.allStaffList();

                            for (int i = 0; i < staffList.size(); i++) {

                                if(!staffList.get(i).getFirstName().trim().isEmpty() && !staffList.get(i).getId().trim().isEmpty() ) {
                                    staffNameList.add(fullNametitle(staffList.get(i).getTitle(),staffList.get(i).getFirstName()));
                                    staffImageList.add(DbBitmapUtility.getImage(staffList.get(i).getBitmap()));
                                }
                            }

                            base.notifyDataSetChanged();

                            staffDialog2.dismiss();

                            // set the no staff message to be visible if the number of existing staff is larger than 0
                            if(staffView.getAdapter().getCount()>0) {
                                noStaff.setVisibility(View.GONE);
                            } else {
                                noStaff.setVisibility(View.VISIBLE);
                            }

                            editedStaff.setText("");
                            editedStaff2.setText("");
                            spinners.setSelection(0);
                            rolespinner.setSelection(0);

                        } else {
                            Context context = getApplicationContext();
                            CharSequence emptyStaffname = "The name of staff is blank";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, emptyStaffname, duration);
                            toast.show();
                        }
                    }
                });
            }
        });

    }

    // Create menu layout for staff activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_staff_menu1, menu);
        return true;
    }

    // Create click function for each item in the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_staff:
                add_staff();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    // Conversion method from string to bitmap
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    // Conversion method from bitmap to string
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    // Function used to add staff to the database
    public void add_staff() {
        newHeadView.setImageResource(R.drawable.head_photo);
        int width = getResources().getDimensionPixelSize(R.dimen.staffAddWidth);
        int height = getResources().getDimensionPixelSize(R.dimen.staffAddheight);
        staffDialog2.show();
        staffDialog2.getWindow().setLayout(width,height);
    }

    // Get full name of staff
    public String fullNametitle(String s1, String s2) {
        return s1+"."+s2;
    }

    // Method used to take photo or uploading from gallery (for adding dialog use)
    public void selectStaffImage(View view) {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StaffTab.this);
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

    // Second Method used to take photo or uploading from gallery (for editing dialog use)
    public void selectStaffImage2(View view) {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StaffTab.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 3);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 4);

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

        // Check condition of result
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bundle extras = data.getExtras();
                Bitmap photo = (Bitmap) extras.get("data");
                newHeadView.setImageBitmap(photo);
            } else if(requestCode==3) {
                Bundle extras = data.getExtras();
                Bitmap photo = (Bitmap) extras.get("data");
                headView.setImageBitmap(photo);
            } else if (requestCode == 2 && data!=null) {
                try {
                    Uri URI = data.getData();
                    Bitmap bitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), URI);

                    newHeadView.setImageBitmap(bitmap);
                } catch(Exception e) {
                    Toast.makeText(StaffTab.this,"Please try it again",Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == 4 && data!=null) {
                try {
                    Uri URI = data.getData();
                    Bitmap bitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), URI);

                    headView.setImageBitmap(bitmap);
                } catch(Exception e) {
                    Toast.makeText(StaffTab.this,"Please try it again",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
