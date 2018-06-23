package com.example.spec.medmedes3;

import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView welcome; //welcome textview to hold the user's name

    int totalOfEntries = 0; //number value to store total glucose levels added together

    int average = 0; //glucose levels divided by # of glucose entries to get an average

    int count = 0; //# of glucose entries used to calculate an average

    TextView avg; //textview to show user the average glucose level

    private FirebaseAuth mAuth; //reference to Firebase database for user authentication

    private FirebaseDatabase database; //Firebase database to store values

    private DatabaseReference myRef; //reference to above

    AlertDialog dialog; //AltertDialog builder for dialog popups

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access the database for authentication
        mAuth = FirebaseAuth.getInstance();

        //grab the average box so we can quickly calculate that
        avg = findViewById(R.id.tv_avg_num);

        //get database reference
        database =  FirebaseDatabase.getInstance();
        myRef = database.getReference("User").child(mAuth.getCurrentUser().getUid());

        //listen for changes in order to update the average
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //reinitialize average and count
                average = 0;
                totalOfEntries = 0;
                count = 0;

                String uname = dataSnapshot.child("username").getValue(String.class);
                welcome = findViewById(R.id.welcome);
                String text = getResources().getString(R.string.welcome_message, uname);
                welcome.setText(text);
                //set welcome text with the user's name to make them feel more comfortable with the app
                //using string formatting allows for dynamic translatable strings

                //TODO: only calculate average of past 30 days
                //go through all of them and calculate average
                for (DataSnapshot ds : dataSnapshot.child("Entries").getChildren()) {
                    //iterate through the entries, only calculate average if both date and glucose are present
                    if (ds.child("glucose").getValue(Integer.class)!=null) {
                        count = count + 1;

                        Integer glu = ds.child("glucose").getValue(Integer.class);

                        totalOfEntries = totalOfEntries + glu;
                    }//end if
                }//end for loop

                //then calculate average
                //don't divide by 0
                if(count != 0) {
                    average = totalOfEntries / count; //calculates the total average
                }//end if

                //inform user of average
                avg.setText(String.valueOf(average));

            }//end on DataChanged

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }//end onCancelled
        }); //end listener

        myRef.child("dummy").setValue("");
        //read fake value to trigger listener to calculate average and indicate username

    }//end onCreate


    public void Submit(View v){

        final EditText glucose = findViewById(R.id.et_current);

        //tell the user how they're doing
        if(!glucose.getText().toString().equals("")) { //do not try to enter if accidentally pressed submit button
            // otherwise, parse the data

            //get the glucose level, then clear the box so the user knows it went through
            Integer glustr = Integer.parseInt(glucose.getText().toString());
            glucose.setText("");

            //TODO: Maybe add 'tips' for improvement?

            //change the message depending on the glucose level
            //dialog & color is used so it's easy to read and understand, especially since diabetes patients often cant see well
            if (glustr > 180) {
                //Dangerously high glucose
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.badDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_dg_title));
                dialog.setMessage(getResources().getString(R.string.main_dangerous_glucose));
            } else if (glustr > 130) {
                //High glucose
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.warningDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_hg_title));
                dialog.setMessage(getResources().getString(R.string.main_high_glucose));
            } else if (glustr < 80) {
                //Low glucose
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.badDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_lg_title));
                dialog.setMessage(getResources().getString(R.string.main_low_glucose));
            } else {
                //Normal glucose
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.goodDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_gg_title));
                dialog.setMessage(getResources().getString(R.string.main_normal_glucose));
            }//end else

            //now show our dialog
            dialog.show();

            /*commenting bc this might not be needed
            //get the date
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            //month is plus one because the Calendar class starts with zero
            int month = now.get(Calendar.MONTH);
            int day = now.get(Calendar.DAY_OF_MONTH);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            //convert the month into a string
            String mthStr = new DateFormatSymbols().getMonths()[month];
            */

            //get the date
            Long date = System.currentTimeMillis();

            //Store as user.year.month.day.hour.minute
            //myRef.child(mAuth.getCurrentUser().getUid()).child("Entries").child(String.valueOf(year)).child(mthStr).child(String.valueOf(day)).child(String.valueOf(hour)).child(String.valueOf(minute)).setValue(glustr);
            myRef.child("Entries").child(String.valueOf(date)).child("date").setValue(String.valueOf(date));
            myRef.child("Entries").child(String.valueOf(date)).child("glucose").setValue(glustr);

        }//end no content if

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

        //TODO: check to see if this can be removed? looks like it's not being used....
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if user is signed in (non-null), if not go to account creation/login.
        if(mAuth.getCurrentUser()==null){
            //if the username isn't available, take to account creation
            Intent i = new Intent(getApplicationContext(), AccountCreation.class);

            startActivity(i);
        }//end if
        //We start in MainActivity instead of account creation because most users will usually be logged in
    }//end onStart


    public void onBackPressed() {//deal with backbutton
        //do nothing, that way we can avoid weird login issues
    } //end onBackPressed


    public void Logout(View v){
        //log the user out
        FirebaseAuth.getInstance().signOut();

        //then take them to login
        Intent i = new Intent(getApplicationContext(), AccountCreation.class);

        startActivity(i);
    }//end Logout


}//end class
