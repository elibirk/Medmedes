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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GlucoseHistory extends AppCompatActivity {

    private FirebaseAuth mAuth; //reference to Firebase database for user authentication

    private FirebaseDatabase database; //Firebase database to store values

    private DatabaseReference myRef; //reference to above

    View convertView;

    LayoutInflater inflater;

    LinearLayout activity_glucose_history;

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
        activity_glucose_history = findViewById(R.id.LL_glucose);

        //access the database for authentication to get UID
        mAuth = FirebaseAuth.getInstance();

        //access user's part of database
        database =  FirebaseDatabase.getInstance();
        myRef = database.getReference("User").child(mAuth.getCurrentUser().getUid());

        //add fake value to trigger listener
        myRef.child("Dummy").setValue("");

        ValueEventListener userListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.child("Entries").getChildren()) {
                    //for all entries that the user has
                    inflater = getLayoutInflater();

                    //inflate the box
                    convertView = inflater.inflate(R.layout.box, null);
                    activity_glucose_history.addView(convertView);
                    RelativeLayout rl = convertView.findViewById(R.id.box_level_layout);

                    //set date TODO: make readable
                    TextView date = convertView.findViewById(R.id.tv_date);
                    date.setText(ds.child("date").getValue(String.class));

                    //set level
                    TextView level = convertView.findViewById(R.id.tv_level);
                    Integer lvl = ds.child("glucose").getValue(Integer.class);

                    //choose color based on how healthy the level is
                    if(lvl > 180 || lvl < 80){
                        rl.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                        date.setTextColor(getResources().getColor(android.R.color.white));
                        level.setTextColor(getResources().getColor(android.R.color.white));
                    } else if(lvl > 130){
                        rl.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                    }//end else if

                    //add the level content
                    level.setText(String.valueOf(lvl));
                }//end entry for loop

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

