package com.example.spec.medmedes3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountCreation extends AppCompatActivity {

    SharedPreferences prefs;

    TextView welcome;

    EditText username;

    EditText email;

    EditText password1;

    EditText password2;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        mAuth = FirebaseAuth.getInstance();

        //OLD connect to database
        //myRef = FirebaseDatabase.getInstance().getReference("message");
    }


    //TODO: swap with Firebase-d account creation
    public void CreateAccount(View v){
        //access preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //grab welcome TV and the edit texts
        welcome = findViewById(R.id.welcome);
        email = findViewById(R.id.et_Email);
        username = findViewById(R.id.et_UserName);
        password1 = findViewById(R.id.et_Password);
        password2 = findViewById(R.id.et_Password2);

        //by default, assume no username exists since we've gotten to this page
        //username_exists = false;

        /*OLD CODE
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
END OLD CODE*/

        //check content, give warnings for mixed/incorrect content
        if(username.getText().toString().equals("")){
            welcome.setText(R.string.uname_warning);
        } else if(email.getText().toString().equals("")){
            welcome.setText(R.string.email_warning);
        } else if(password1.getText().toString().equals("")){
            welcome.setText(R.string.pass_warning);
        } else if(!password1.getText().toString().equals(password2.getText().toString())){
            welcome.setText(R.string.pass_match_warning);
        } else {

            //place into database
            mAuth.createUserWithEmailAndPassword(username.getText().toString(), password1.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("DBYES", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                //TODO: Replace with screen change
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("DBFAIL", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AccountCreation.this,"Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                //TODO: Replace
                            }//end else

                            // ...
                        }//end onComplete
                    }); //end createUser


            //TODO: Remove once database is functioning
            //first put the username into shared preferences
            prefs.edit().remove("username");
            prefs.edit().putString("username", username.getText().toString()).apply();
            prefs.edit().commit();

            //then go ahead and take us to the main menu
            Intent i = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(i);
        }//end else

    }//end CreateAccount

    public void KnownAccount(View v){
        //TODO: if account is known, then let them log in using similar layout
        //maybe just hide some stuff: username, 'create account', password confirmation

        //grab welcome TV and the edit texts
        welcome = findViewById(R.id.welcome);
        email = findViewById(R.id.et_Email);
        password1 = findViewById(R.id.et_Password);

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password1.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SIGNINYES", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            //TODO: Replace
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGNINFAIL", "signInWithEmail:failure", task.getException());
                            Toast.makeText(AccountCreation.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            //TODO: Replace
                        }//end else

                        // ...
                    }//end onComplete
                });//end signIn
    }//end KnownAccount

    public void onBackPressed() {//deal with backbutton
        //do nothing, that way we can avoid people skipping login
    } //end onBackPressed

}//end class
