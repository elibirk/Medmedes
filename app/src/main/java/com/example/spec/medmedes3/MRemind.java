package com.example.spec.medmedes3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MRemind extends AppCompatActivity {

    LayoutInflater inflater;
    View convertView;

    LinearLayout activity_search_tasks;

    EditText newMedicine;

    SharedPreferences pref;

    DatabaseReference myRef;

    int remnum;

    int user_count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mremind);

        //get number of reminders
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        remnum = pref.getInt("remnum", 1);

        myRef = FirebaseDatabase.getInstance().getReference("User");


        activity_search_tasks = (LinearLayout) findViewById(R.id.reminders);
        newMedicine = (EditText) findViewById(R.id.newMedicine);

        listener();

    }

    private void listener(){
        //add content to trigger
        myRef.child("Dummy").setValue("");

        ValueEventListener userListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("DATABASE_USER",""+dataSnapshot.child("UserDetails" + 1).child("username").getValue(String.class));


                //users?
                for (int i = 1; dataSnapshot.child("UserDetails" + i).child("username").getValue(String.class) != null; i++) {
                    user_count =i;
                    //for all tasks that the user has
                    //TO DO: if (username = username in prefs) then do the for
                    for(int j = 1; dataSnapshot.child("UserDetails" + i).child("Reminders").child("reminder"+j).child("name").getValue(String.class) != null; j++) {

                        Log.d("USER_DETAILS", ""+dataSnapshot.child("UserDetails" + i).child("Reminders").child("reminder"+j).child("date").getValue(String.class));

                        inflater = getLayoutInflater();

                        //inflate the box
                        convertView = inflater.inflate(R.layout.box, null);
                        activity_search_tasks.addView(convertView);
                        RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.box_task_layout);

                        //set level
                        TextView date = (TextView) convertView.findViewById(R.id.tv_date);
                        date.setText(dataSnapshot.child("UserDetails"+i).child("Reminders").child("reminder"+j).child("date").getValue(String.class));

                        //set level
                        TextView name = (TextView) convertView.findViewById(R.id.tv_level);
                        String lvl = dataSnapshot.child("UserDetails"+i).child("Reminders").child("reminder"+j).child("name").getValue(String.class);


                        name.setText(lvl);


                    }//end entries
                }


            }//end users

            @Override
            public void onCancelled (DatabaseError databaseError){
                // Getting Post failed, log a message
                Log.d("Canceled", "loadPost:onCancelled", databaseError.toException());
                // ...
            }

        }; //end event listener

        myRef.addValueEventListener(userListener);
    }



    public void addReminder(View v){
        //if chosen, go to add a reminder
        Intent i = new Intent(getApplicationContext(), NewReminder.class);

        startActivity(i);
    }

    public void onBackPressed(){
        //go back to main
        Intent i = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(i);
    }


}//end MRemind
