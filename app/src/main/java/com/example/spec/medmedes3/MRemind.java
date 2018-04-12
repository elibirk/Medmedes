package com.example.spec.medmedes3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MRemind extends AppCompatActivity {

    LayoutInflater inflater;

    View convertView;

    LinearLayout activity_reminders;

    EditText newMedicine;

    private FirebaseAuth mAuth; //reference to Firebase database for user authentication

    private FirebaseDatabase database; //Firebase database to store values

    private DatabaseReference myRef; //reference to above

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mremind);

        //access the database for authentication
        mAuth = FirebaseAuth.getInstance();

        //get database reference
        database =  FirebaseDatabase.getInstance();
        myRef = database.getReference("User").child(mAuth.getCurrentUser().getUid());

        activity_reminders = findViewById(R.id.reminders);
        newMedicine = findViewById(R.id.newMedicine);

        //add content to trigger listener
        myRef.child("Dummy").setValue("");

        //listen for changes in order to update the average
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //fetch reminders
                for (DataSnapshot ds : dataSnapshot.child("Reminders").getChildren()) {
                    //iterate through the entries, only calculate average if both date and glucose are present
                    if (ds.child("name").getValue(String.class)!=null) {

                        inflater = getLayoutInflater();

                        //inflate the box
                        convertView = inflater.inflate(R.layout.box, null);
                        activity_reminders.addView(convertView);
                        RelativeLayout rl = convertView.findViewById(R.id.box_level_layout);

                        //set the time in the box
                        TextView time = convertView.findViewById(R.id.tv_date);
                        time.setText(ds.child("time").getValue(String.class));

                        //set the name in the box
                        TextView name = convertView.findViewById(R.id.tv_level);
                        name.setText(ds.child("name").getValue(String.class));

                    }//end if
                }//end for loop

            }//end on DataChanged

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }//end onCancelled
        }); //end listener

    }//end onCreate


    public void addReminder(View v){
        //if chosen, go to add a reminder
        Intent i = new Intent(getApplicationContext(), NewReminder.class);

        startActivity(i);
    }//end addReminder


    public void onBackPressed(){
        //go back to main
        Intent i = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(i);
    }//end onBackPressed


}//end MRemind
