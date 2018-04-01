package com.example.spec.medmedes3;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GlucoseHistory extends AppCompatActivity {

    DatabaseReference myRef;

    View convertView;

    LayoutInflater inflater;

    LinearLayout activity_search_tasks;

    ArrayList<String> taskname;

    int arraylist_count;

    int user_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose_history);

        arraylist_count=0;
        user_count =1;

        //essentially search database & populate the page, doing this in a loop for every result
        //we can probably set this up like the ViewAdapter hw if we want more efficiency
        //but the priority right now is completion, not perfection
        activity_search_tasks = findViewById(R.id.LL_glucose);

        //access messages part of database
        myRef = FirebaseDatabase.getInstance().getReference("User");

        //add fake value to trigger listener
        myRef.child("Dummy").setValue("");

        ValueEventListener userListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                taskname = new ArrayList<>();

                Log.d("DATABASE_USER",""+dataSnapshot.child("UserDetails" + 1).child("username").getValue(String.class));

                //
                for (int i = 1; dataSnapshot.child("UserDetails" + i).child("username").getValue(String.class) != null; i++) {
                    user_count =i;
                    //for all tasks that the user has
                    //will do later: if (username = username in prefs) then do the for
                    for(int j = 1; dataSnapshot.child("UserDetails" + i).child("Entries").child("entry"+j).child("level").getValue(String.class) != null; j++) {

                        Log.d("USER_DETAILS", ""+dataSnapshot.child("UserDetails" + i).child("Entries").child("entry"+j).child("date").getValue(String.class));

                        inflater = getLayoutInflater();

                        arraylist_count = j;

                        //inflate the box
                        convertView = inflater.inflate(R.layout.box, null);
                        activity_search_tasks.addView(convertView);
                        RelativeLayout rl = convertView.findViewById(R.id.box_task_layout);

                        //set level
                        TextView date = convertView.findViewById(R.id.tv_date);
                        date.setText(dataSnapshot.child("UserDetails"+i).child("Entries").child("entry"+j).child("date").getValue(String.class));

                        //set level
                        TextView level = convertView.findViewById(R.id.tv_level);
                        String lvl = dataSnapshot.child("UserDetails"+i).child("Entries").child("entry"+j).child("level").getValue(String.class);

                        //choose color based on how healthy the level is
                        if(Integer.parseInt(lvl) > 180 || Integer.parseInt(lvl) < 80){
                            rl.setBackgroundColor(0xFFc13a3a);
                            date.setTextColor(0xFFffffff);
                            level.setTextColor(0xFFffffff);
                        } else if(Integer.parseInt(lvl)>130){
                            rl.setBackgroundColor(0xFFe5b060);
                        }

                        //add the level content
                        level.setText(lvl);


                    }//end entry loop
                } //end users loop

            }//end onDataChanged

            @Override
            public void onCancelled (DatabaseError databaseError){
                // Getting Post failed, log a message
                Log.d("Canceled", "loadPost:onCancelled", databaseError.toException());
                // ...
            }

        }; //end event listener

        myRef.addListenerForSingleValueEvent(userListener);

    }//end onCreate

}//end class

