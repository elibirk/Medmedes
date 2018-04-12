package com.example.spec.medmedes3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import java.util.Calendar;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewReminder extends AppCompatActivity {

    Calendar targetCal2;

    Button buttonstartSetDialog;

    private FirebaseAuth mAuth; //reference to Firebase database for user authentication

    private FirebaseDatabase database; //Firebase database to store values

    private DatabaseReference myRef; //reference to above

    EditText newMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        //access the database for authentication
        mAuth = FirebaseAuth.getInstance();

        //get database reference
        database =  FirebaseDatabase.getInstance();
        myRef = database.getReference("User").child(mAuth.getCurrentUser().getUid());

        newMedicine = findViewById(R.id.newMedicine);

        //button code
        buttonstartSetDialog = findViewById(R.id.newRem);
        buttonstartSetDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openTimePickerDialog(false);

            }
        });
    }//end onCreate

    //allows user to choose the time of the alarm
    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(NewReminder.this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle(R.string.newremind_alarm_title);

        timePickerDialog.show();

    }//end openTimePickerDialog

    //once time is chosen, set alarm
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            //make a calendar
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            //set calendar unit to chosen time and minute
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                // Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }//end if

            setAlarm(calSet);
        }
    };//end OnTimeSetListener

    private void setAlarm(Calendar targetCal) {

        targetCal2 = targetCal;

        //unworking code is commented

       /* Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(MRemind.this,AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis() + 1000, pendingIntent);
AlarmReceiver.setAlarm(this);*/


        AlarmManager alarmManager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 5);

        Intent myIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent,0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);


        myRef.child("Dummy").setValue("");
        String tcal2 = targetCal.getTime() + "";
        String time = tcal2.substring(11, 23);

        //store the reminder, use time instead of name to allow for multiple of the same medicine
        myRef.child("Reminders").child(time).child("time").setValue(time);
        myRef.child("Reminders").child(time).child("name").setValue(newMedicine.getText().toString());
        //TODO: maybe add notes section? like 'take with water' or for a list of different meds


        //if chosen, go to add a reminder
        Intent i = new Intent(getApplicationContext(), MRemind.class);

        startActivity(i);

    }//end setAlarm
}//end NewReminder
