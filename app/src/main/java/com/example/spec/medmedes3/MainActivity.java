package com.example.spec.medmedes3;

import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    SharedPreferences pref;

    TextView welcome; //welcome textview to hold the user's name

    //int user_count = 1;

    //int task_count;

    int entrynum; //number of entries in database, stored in prefs. TODO: move to database or count database entries instead

    int totalOfEntries; //number value to store total glucose levels added together

    int average; //glucose levels divided by # of glucose entries to get an average

    int count; //# of glucose entries used to calculate an average

    TextView avg; //textview to show user the average glucose level

    private FirebaseAuth mAuth; //reference to Firebase database for user authentication

    AlertDialog dialog; //AltertDialog builder for dialog popups

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access the database
        mAuth = FirebaseAuth.getInstance();

        //grab the average box so we can quickly calculate that
        avg = findViewById(R.id.tv_avg_num);

        //get database reference
        //TODO: Remove and replace database references with updated code
        //myRef = FirebaseDatabase.getInstance().getReference("User");
/*
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //reinitialize average and count
                totalOfEntries = 0;
                count = 0;

                //go through all of them and calculate average
                for (int j = 1; dataSnapshot.child("UserDetails" + user_count).child("Entries").child("entry" + String.valueOf(j)).child("level").getValue(String.class) != null;) {
                    Log.d("j", String.valueOf(j));
                    Log.d("ucount", String.valueOf(user_count));
                    Log.d("count", String.valueOf(count));
                    Log.d("average", String.valueOf(totalOfEntries));
                    totalOfEntries = totalOfEntries + Integer.parseInt(dataSnapshot.child("UserDetails" + user_count).child("Entries").child("entry" + String.valueOf(j)).child("level").getValue(String.class));

                    count++;
                    j++;
                }//end for

                Log.d("averageafter", String.valueOf(totalOfEntries));

                //don't divide by 0
                if(count != 0) {
                    average = totalOfEntries / count; //calculates the total average
                }//end if

                //inform user of average
                avg.setText(String.valueOf(average));
            }//end on DataChanged

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }//end onCalcelled
        }; //end listener

        */

        //myRef.addValueEventListener(postListener);

        //myRef.child("dummy").setValue("");
        //read fake value to trigger listener to calculate average

        //TODO: get username from database and use it, remove tempname
        //set welcome text with the user's name to make them feel more comfortable with the app
        //using string formatting allows for dynamic translatable strings
        String uname = "Temporary Companion";
        welcome = findViewById(R.id.welcome);
        String text = getResources().getString(R.string.welcome_message, uname);
        welcome.setText(text);

    }//end onCreate


    public void Submit(View v){

        final EditText glucose = findViewById(R.id.et_current);

        //get the glucose level, then clear the box so the user knows it went through
        String glustr = glucose.getText().toString();
        glucose.setText("");

        //tell the user how they're doing
        if(!glustr.equals("")) { //do not try to enter if accidentally pressed submit button
            // otherwise, parse the data

            //TODO: Maybe add 'tips' for improvement?

            //change the message depending on the glucose level
            //dialog & color is used so it's easy to read and understand, especially since diabetes patients often cant see well
            if (Integer.parseInt(glustr) > 180) {
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.badDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_dg_title));
                dialog.setMessage(getResources().getString(R.string.main_dangerous_glucose));
            } else if (Integer.parseInt(glustr) > 130) {
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.warningDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_hg_title));
                dialog.setMessage(getResources().getString(R.string.main_high_glucose));
            } else if (Integer.parseInt(glustr) < 80) {
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.badDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_lg_title));
                dialog.setMessage(getResources().getString(R.string.main_low_glucose));
            } else {
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.goodDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_gg_title));
                dialog.setMessage(getResources().getString(R.string.main_normal_glucose));
            }//end else

            //now show our dialog
            dialog.show();
            //TODO: move database entering here, notify of database success?

        }//end no content else

/*
        //Determines if the username exists
        ValueEventListener userListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("UserDetails" + 1).child("username").getValue(String.class).equals(pref.getString("username", null))) {

                    user_count =1;
                } else {
                    Log.d("uname", dataSnapshot.child("UserDetails" + 1).child("username").getValue(String.class));

                    for(int i = 1; dataSnapshot.child("UserDetails" + i).child("username").getValue(String.class) != null; i++) {

                        if(dataSnapshot.child("UserDetails" + i).child("username").getValue(String.class).equals(pref.getString("username", "!")))
                        {

                            //  Log.d("MEM_USERNAME", tracker.getString("username", "Null"));
                            Log.d("DATABASE_USERNAME", dataSnapshot.child("UserDetails" + i).child("username").getValue(String.class));

                            user_count = i;
                        }//end if

                    }//end for

                }//end else

            }//end onDataChange
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Getting Post failed, log a message
                //  Log.w("Canceled", "loadPost:onCancelled", databaseError.toException());
                // ...

            }//end onCancelled

        };//end userListener
*/

        //myRef.addListenerForSingleValueEvent(userListener);

        //get the date
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        //month is plus one because the Calendar class starts with zero
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        //TODO: maybe format whole date as one string? Make sure not to make month/day calendar issue, maybe convert month into string
        //i.e. 5 Nov 2349 instead of 5/11/2349 or 11/5/2349 to prevent confusion
        //first see what would be easiest with the database

        //TODO: put date & info into the database

        //myRef.child("UserDetails"+user_count).child("Entries").child("entry" + String.valueOf(entrynum)).setValue("");
        //myRef.child("UserDetails"+user_count).child("Entries").child("entry" + String.valueOf(entrynum)).child("date").setValue(date);
        //myRef.child("UserDetails"+user_count).child("Entries").child("entry" + String.valueOf(entrynum)).child("level").setValue(glustr);

        //increase number of entries by one
        //entrynum++;
        //pref.edit().remove("entrynum");
        //.edit().putInt("entrynum", entrynum).commit();

    }//end Submit



    public void GHist(View v){
        //if chosen, go to Glucose History
        Intent i = new Intent(getApplicationContext(), GlucoseHistory.class);

        startActivity(i);
    }//end GHist


    public void MRemind(View v){
        //if chosen, go to Medicine Reminders
        Intent i = new Intent(getApplicationContext(), MRemind.class);

        startActivity(i);
    }//end MRemind

    @Override
    public void onStart() {
        super.onStart();

        //TODO: implement proper signout option and remove this
        //FirebaseAuth.getInstance().signOut();

        // Check if user is signed in (non-null), if not go to account creation/login.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(mAuth.getCurrentUser()==null){
            //if(uname.equals("")){
            //if the username isn't available, take to account creation
            Intent i = new Intent(getApplicationContext(), AccountCreation.class);

            startActivity(i);
        }//end if
        //We start in MainActivity instead of account creation because most users will usually be logged in
    }//end onStart

    public void onBackPressed() {//deal with backbutton
        //do nothing, that way we can avoid weird login issues
    } //end onBackPressed

}//end class
