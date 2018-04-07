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

    SharedPreferences pref;

    TextView welcome; //welcome textview to hold the user's name

    //int user_count = 1;

    //int task_count;

    int entrynum; //number of entries in database, stored in prefs. TODO: move to database or count database entries instead

    int totalOfEntries; //number value to store total glucose levels added together

    int average = 0; //glucose levels divided by # of glucose entries to get an average

    int count; //# of glucose entries used to calculate an average

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
        myRef = database.getReference("User");

        //listen for changes in order to update the average
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                //Log.d("VAL", "Value is: " + value);

                //reinitialize average and count
                totalOfEntries = 0;
                count = 0;

                String uname = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("username").getValue(String.class);
                welcome = findViewById(R.id.welcome);
                String text = getResources().getString(R.string.welcome_message, uname);
                welcome.setText(text);
                //set welcome text with the user's name to make them feel more comfortable with the app
                //using string formatting allows for dynamic translatable strings

                /*go through all of them and calculate average
                //something like this? might need to change date format for this to work
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String email = ds.child("email").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    Log.d("TAG", email + " / " + name);
                }

                for (int j = 1; dataSnapshot.child("UserDetails" + user_count).child("Entries").child("entry" + String.valueOf(j)).child("level").getValue(String.class) != null;) {
                    Log.d("j", String.valueOf(j));
                    Log.d("ucount", String.valueOf(user_count));
                    Log.d("count", String.valueOf(count));
                    Log.d("average", String.valueOf(totalOfEntries));
                    totalOfEntries = totalOfEntries + Integer.parseInt(dataSnapshot.child("UserDetails" + user_count).child("Entries").child("entry" + String.valueOf(j)).child("level").getValue(String.class));

                    count++;
                    j++;
                }//end for
                */

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
                //Dangerously high glucose
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.badDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_dg_title));
                dialog.setMessage(getResources().getString(R.string.main_dangerous_glucose));
            } else if (Integer.parseInt(glustr) > 130) {
                //High glucose
                dialog = new AlertDialog.Builder(MainActivity.this, R.style.warningDialog).create();
                dialog.setTitle(getResources().getString(R.string.main_hg_title));
                dialog.setMessage(getResources().getString(R.string.main_high_glucose));
            } else if (Integer.parseInt(glustr) < 80) {
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

            //do I still need to do this like this or does the database allow me to dynamically add child values now?
            //Store as user.year.month.day.hour.minute
            myRef.child(mAuth.getCurrentUser().getUid()).child("Entries").child(String.valueOf(year)).child(mthStr).child(String.valueOf(day)).child(String.valueOf(hour)).child(String.valueOf(minute)).setValue(glustr);

        }//end no content if

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

    public void Logout(View v){
        //log the user out
        FirebaseAuth.getInstance().signOut();

        //then take them to login
        Intent i = new Intent(getApplicationContext(), AccountCreation.class);

        startActivity(i);
    }//end Logout

}//end class
