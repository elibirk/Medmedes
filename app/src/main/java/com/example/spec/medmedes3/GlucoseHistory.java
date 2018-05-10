package com.example.spec.medmedes3;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class GlucoseHistory extends AppCompatActivity {

    private FirebaseAuth mAuth; //reference to Firebase database for user authentication

    private FirebaseDatabase database; //Firebase database to store values

    private DatabaseReference myRef; //reference to above

    View convertView;

    LayoutInflater inflater;

    LinearLayout activity_glucose_history;

    int arraylist_count;

    int user_count;

    //TODO: option to delete specific (Accidential? entry)
    //TODO: entry notes?

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

                    //TODO: reverse-chronological? so users see most recent activity first
                    //TODO: charts?
                    //TODO: options for weekly/monthly/yearly?
                    //more than yearly only for premium users lol

                    //inflate the box
                    convertView = inflater.inflate(R.layout.box, null);
                    activity_glucose_history.addView(convertView);
                    RelativeLayout rl = convertView.findViewById(R.id.box_level_layout);

                    //set date
                    //first get the date value from the databaase and reconvert it
                    Long dateLong = Long.parseLong(ds.child("date").getValue(String.class));
                    Date dt = new Date(dateLong);

                    //convert the Date to String using formatting
                    DateFormat dateFormat = DateFormat.getDateTimeInstance();
                    String dateStr = dateFormat.format(dt);

                    //finally set the value in the box
                    TextView date = convertView.findViewById(R.id.tv_date);
                    date.setText(dateStr);

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
            }//end onCancelled

        }; //end event listener

        myRef.addListenerForSingleValueEvent(userListener);

    }//end onCreate

}//end class

