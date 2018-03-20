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
import com.google.android.gms.tasks.OnSuccessListener;
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

        //check content, give warnings for mixed/incorrect content
        if(username.getText().toString().equals("")){
            welcome.setText(R.string.uname_warning);
        } else if(email.getText().toString().equals("")){
            welcome.setText(R.string.email_warning);
        } else if(password1.getText().toString().equals("")){
            //TODO: require secure passwords
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
                                //TODO: get this to run? change to onSuccess?
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("DBYES", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("DBFAIL", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AccountCreation.this,"Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }//end else

                            // ...
                        }//end onComplete
                    }); //end createUser

            //save username? TODO: remove all prefs
            //prefs.edit().putString("username", username.getText().toString()).commit();

            //then go ahead and take us to the main menu
            Intent i = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(i);
        }//end else

    }//end CreateAccount

    public void KnownAccount(View v){
        //TODO: if account is known, then let them log in using similar layout
        //maybe just hide some stuff: username, 'create account', password confirmation

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //grab welcome TV and the edit texts
        username = findViewById(R.id.et_UserName);
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
                            prefs.edit().putString("username", username.getText().toString()).commit();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);

                            startActivity(i);
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
