package com.example.spec.medmedes3;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import java.util.Calendar;

import android.icu.text.StringPrepParseException;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    SharedPreferences pref;

    TextView welcome;

    DatabaseReference myRef;

    int user_count = 1;

    int task_count;

    int entrynum;

    int average;

    int actualaverage;

    int count;

    TextView avg;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        avg = findViewById(R.id.tv_avg_num);

        mAuth = FirebaseAuth.getInstance();

        //get database reference
        //TODO: Old?
        myRef = FirebaseDatabase.getInstance().getReference("User");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //reinitialize average and count
                average = 0;
                count = 0;

                //go through all of them and calculate average
                for (int j = 1; dataSnapshot.child("UserDetails" + user_count).child("Entries").child("entry" + String.valueOf(j)).child("level").getValue(String.class) != null;) {
                    Log.d("j", String.valueOf(j));
                    Log.d("ucount", String.valueOf(user_count));
                    Log.d("count", String.valueOf(count));
                    Log.d("average", String.valueOf(average));
                    average = average + Integer.parseInt(dataSnapshot.child("UserDetails" + user_count).child("Entries").child("entry" + String.valueOf(j)).child("level").getValue(String.class));

                    count++;
                    j++;
                }//end for

                Log.d("averageafter", String.valueOf(average));

                //don't divide by 0
                if(count != 0) {
                    actualaverage = average / count;
                }//end if

                //inform user of average
                avg.setText(String.valueOf(actualaverage));
            }//end on DataChanged

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }//end onCalcelled
        }; //end listener

        myRef.addValueEventListener(postListener);

        myRef.child("dummy").setValue("");
        //readd fake value to trigger listener to calculate average



        //Get preferences to see if a username exists
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        //get username and number of entries
        String uname = pref.getString("username", "");
        entrynum = pref.getInt("entrynum", 1);


        Log.d("uname", uname);
        if(uname.equals("")){
            //if the username isn't available, take to account creation
            Intent i = new Intent(getApplicationContext(), AccountCreation.class);

            startActivity(i);
        }
        //We start in MainActivity instead of account creation because most users will usually
        //have usernames saved, so this means less swapping


        //set welcome text with the user's name to make them feel more comfortable with the app
        welcome = (TextView) findViewById(R.id.welcome);

        welcome.setText("Welcome " + uname + "! \nHow are you doing today?");

    }//end onCreate


    public void Submit(View v){

        final EditText glucose = (EditText) findViewById(R.id.et_current);

        //get the glucose level, then clear the box so the user knows it went through
        String glustr = glucose.getText().toString();
        glucose.setText("");
        Toast.makeText(this, "Submission Complete", Toast.LENGTH_SHORT).show();

        //tell the user how they're doing
        //color coded text since we can't color code the box
        if(Integer.parseInt(glustr)>180){
            Toast toast = Toast.makeText(getApplicationContext(), "Your glucose is very high! Be careful",
                    Toast.LENGTH_LONG);
            TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
            toastMessage.setTextColor(Color.RED);
            toast.show();
        } else if(Integer.parseInt(glustr)>130){
            Toast toast = Toast.makeText(getApplicationContext(), "Your glucose seems high, " +
                            "but it's a normal level if you've eaten recently.",
                    Toast.LENGTH_LONG);
            TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
            toastMessage.setTextColor(Color.RED);
            toast.show();
        } else if (Integer.parseInt(glustr)<80){
            Toast toast = Toast.makeText(getApplicationContext(), "Your glucose is abnormally low! Be careful",
                    Toast.LENGTH_LONG);
            TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
            toastMessage.setTextColor(Color.RED);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Your glucose is normal. Congrats!",
                    Toast.LENGTH_LONG);
            TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
            toastMessage.setTextColor(Color.WHITE);
            toast.show();
        }//end else


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

            }//end if username_exists
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Getting Post failed, log a message
                //  Log.w("Canceled", "loadPost:onCancelled", databaseError.toException());
                // ...

            }//end onCancelled

        };//end onDataChanged

        myRef.addListenerForSingleValueEvent(userListener);

        GregorianCalendar c = new GregorianCalendar();

        //Hardcoded month bc conversion is incorrect (gives december), would fix later
        String monthname = "May";
        //String monthname = new java.text.SimpleDateFormat("MMMM");
        //java.text.SimpleDateFormat df3 = new java.text.SimpleDateFormat("MMMM");
        //String monthname=(String)android.text.format.DateFormat.format("MMMM", 9);
        //String monthname = c.get(1);
        //String monthname=(String)android.text.format.DateFormat.format("MMMM", c.get(GregorianCalendar.MONTH) + 5);
        String date = monthname;
        date = date + " " + String.valueOf(c.get(GregorianCalendar.DAY_OF_MONTH));
        date = date + " " + String.valueOf(c.get(GregorianCalendar.YEAR));
        date = date + " " + String.valueOf(c.get(GregorianCalendar.HOUR_OF_DAY));
        date = date + ":" + String.valueOf(c.get(GregorianCalendar.MINUTE));
        date = date + ":" + String.valueOf(c.get(GregorianCalendar.SECOND));


        //put the info into the database
        Log.d("TASK", Integer.toString(task_count));
        Log.d("USERCOUNT", Integer.toString(user_count));

        myRef.child("UserDetails"+user_count).child("Entries").child("entry" + String.valueOf(entrynum)).setValue("");
        myRef.child("UserDetails"+user_count).child("Entries").child("entry" + String.valueOf(entrynum)).child("date").setValue(date);
        myRef.child("UserDetails"+user_count).child("Entries").child("entry" + String.valueOf(entrynum)).child("level").setValue(glustr);

        //increase number of entries by one
        entrynum++;
        pref.edit().remove("entrynum");
        pref.edit().putInt("entrynum", entrynum).commit();

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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        //TODO: replace with updated UI for logged in user? test first
    }//end onStart

}//end class
