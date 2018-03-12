package com.example.spec.medmedes3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AccountCreation extends AppCompatActivity {

    //DatabaseReference myRef;

    SharedPreferences prefs;

    boolean username_exists;

    TextView welcome;

    EditText username;

    EditText password1;

    EditText password2;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        mAuth = FirebaseAuth.getInstance();

        //connect to database
        //myRef = FirebaseDatabase.getInstance().getReference("message");
    }


    //TODO: swap with Firebase-d account creation
    public void CreateAccount(View v){
        //access preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //grab welcome TV and the edit texts
        welcome = findViewById(R.id.welcome);
        username = findViewById(R.id.et_UserName);
        password1 = findViewById(R.id.et_Password);
        password2 = findViewById(R.id.et_Password2);

        //by default, assume no username exists since we've gotten to this page
        username_exists = false;

        ValueEventListener userListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i;

                //check if username exists in database
                for( i =1; dataSnapshot.child("User").child("UserDetails" + i).child("username").getValue(String.class) != null; i++) {

                    if(username.getText().toString().equals(dataSnapshot.child("User").child("UserDetails" + i).child("username").getValue(String.class))){
                        //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();.makeText(getApplicationContext(), "Please Choose a Different Username.", Toast.LENGTH_LONG).show();
                        username_exists = true;
                    }
                }

                //if everything is in place, add the user to the database
                if(!username_exists && !(username.getText().toString().equals("")) &&
                        !(password1.getText().toString().equals(""))&&
                        password1.getText().toString().equals(password2.getText().toString())) {

                    //myRef.child("User").child("UserDetails" + i).child("username").setValue(username.getText().toString());
                    //myRef.child("User").child("UserDetails" + i).child("password").setValue(password1.getText().toString());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("Canceled", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        //myRef.addValueEventListener(userListener);


        //check content
        if(username.getText().toString().equals("")){
            welcome.setText(R.string.uname_warning);
        } else if(password1.getText().toString().equals("")){
            welcome.setText(R.string.pass_warning);
        } else if(!password1.getText().toString().equals(password2.getText().toString())){
            welcome.setText(R.string.pass_match_warning);
        } else {
            //first put the username into shared preferences
            prefs.edit().remove("username");
            prefs.edit().putString("username", username.getText().toString()).apply();
            prefs.edit().commit();

            //then go ahead and take us to the main menu
            Intent i = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(i);
        }

    }//end CreateAccount

    public void KnownAccount(View v){
        //TODO: if account is known, then let them log in using similar layout
        //maybe just hide some stuff
    }//end KnownAccount

    public void onBackPressed() {//deal with backbutton
        //do nothing, that way we can avoid people skipping login
    } //end onBackPressed

}//end class
