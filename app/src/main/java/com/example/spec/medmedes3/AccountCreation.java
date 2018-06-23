package com.example.spec.medmedes3;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountCreation extends AppCompatActivity {

    SharedPreferences prefs;

    TextView welcome;

    TextView login;

    EditText username;

    EditText email;

    EditText password1;

    EditText password2;

    AlertDialog dialog;

    private FirebaseAuth mAuth; //authorization database

    private FirebaseDatabase database; //Firebase database to store values

    private DatabaseReference myRef; //reference to above

    //TODO: option to change account info (esp password) recaptcha for changing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        mAuth = FirebaseAuth.getInstance();

    }//end onCreate


    public void CreateAccount(View v){
        //access preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //grab welcome TV and the edit texts
        welcome = findViewById(R.id.welcome);
        email = findViewById(R.id.et_Email);
        username = findViewById(R.id.et_UserName);
        password1 = findViewById(R.id.et_Password);
        password2 = findViewById(R.id.et_Password2);

        //check content, give warnings for mixed/incorrect content
        if(username.getText().toString().equals("")){
            welcome.setText(R.string.uname_warning);
        } else if(email.getText().toString().equals("")){
            welcome.setText(R.string.email_warning);
        } else if(password1.getText().toString().equals("") || password1.getText().toString().length() < 6){
            //TODO: require more secure passwords?
            //TODO: configure database to require authentication
            //TODO: Google's recaptcha library-thing for making sure we don't get bot accounts
            welcome.setText(R.string.pass_warning);
        } else if(!password1.getText().toString().equals(password2.getText().toString())){
            welcome.setText(R.string.pass_match_warning);
        } else {

            //place into database
            Log.d("email",email.getText().toString());
            Log.d("pw",password1.getText().toString());
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password1.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("DBYES", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                //save the user's username so it can be referenced later
                                database =  FirebaseDatabase.getInstance();
                                myRef = database.getReference("User");
                                myRef.child(mAuth.getCurrentUser().getUid()).child("username").setValue(username.getText().toString());

                                //then go back to main, logging the user in
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);

                                startActivity(i);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("DBFAIL", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AccountCreation.this,"Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }//end else

                        }//end onComplete
            }); //end createUser

        }//end else

    }//end CreateAccount


    public void KnownAccount(View v){

        username = findViewById(R.id.et_UserName);
        welcome = findViewById(R.id.welcome);
        email = findViewById(R.id.et_Email);
        password1 = findViewById(R.id.et_Password);
        password2 = findViewById(R.id.et_Password2);
        login = findViewById(R.id.btn_known_account);

        //Change welcome message and hide things we don't need
        welcome.setText(R.string.account_creation_known_msg);
        password2.setVisibility(View.GONE);
        login.setVisibility(View.GONE);
        username.setVisibility(View.GONE);

        TextView temp = findViewById(R.id.tv_UserName);
        temp.setVisibility(View.GONE);
        temp = findViewById(R.id.tv_Password2);
        temp.setVisibility(View.GONE);


        //change the button to say submit and use LogIn as its action
        Button submit = findViewById(R.id.btn_make_request);
        submit.setText(R.string.account_creation_known_submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs = PreferenceManager.getDefaultSharedPreferences(AccountCreation.this);

                //grab welcome TV and the edit texts

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password1.getText().toString())
                        .addOnCompleteListener(AccountCreation.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("SIGNINYES", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);

                                    startActivity(i);
                                } else {
                                    //log the error
                                    Log.w("SIGNINFAIL", "signInWithEmail:failure", task.getException());

                                    //If signing fails, let the user know that there was an issue
                                    //but don't tell them specifics in case that reveals info about users & the database
                                    dialog = new AlertDialog.Builder(AccountCreation.this, R.style.badDialog).create();
                                    dialog.setTitle(getResources().getString(R.string.account_creation_known_fail_title));
                                    dialog.setMessage(getResources().getString(R.string.account_creation_known_fail));
                                    dialog.show();

                                }//end else

                            }//end onComplete
                        });//end signIn
            }//end onClick
        });//end listener

    }//end KnownAccount


    public void onBackPressed() {//deal with back-button
        //do nothing, that way we can avoid people skipping login
    } //end onBackPressed

}//end class
