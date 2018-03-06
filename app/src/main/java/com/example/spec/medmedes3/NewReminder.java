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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewReminder extends AppCompatActivity {

    Calendar targetCal2;

    Button buttonstartSetDialog;

    DatabaseReference myRef;

    int user_count = 1;

    SharedPreferences pref;

    int remnum;

    EditText newMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);


        //get number of reminders
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        remnum = pref.getInt("remnum", 1);

        //access database
        myRef = FirebaseDatabase.getInstance().getReference("User");

        newMedicine = (EditText) findViewById(R.id.newMedicine);

        //button code
        buttonstartSetDialog = (Button) findViewById(R.id.newRem);
        buttonstartSetDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openTimePickerDialog(false);

            }
        });
    }

    //allows user to choose the time of the alarm
    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(NewReminder.this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();

    }

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
            }

            setAlarm(calSet);
        }
    };

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


        //////////////////////////////////////////////////////
        myRef.child("Dummy").setValue("");
        String tcal2 = targetCal.getTime() + "";
        String time = tcal2.substring(11, 23);

        myRef.child("UserDetails"+user_count).child("Reminders").child("reminder" + String.valueOf(remnum)).setValue("");
        myRef.child("UserDetails"+user_count).child("Reminders").child("reminder" + String.valueOf(remnum)).child("date").setValue(time);
        myRef.child("UserDetails"+user_count).child("Reminders").child("reminder" + String.valueOf(remnum)).child("name").setValue(newMedicine.getText().toString());
        remnum++;
        pref.edit().remove("remnum");
        pref.edit().putInt("remnum", remnum).commit();

        //if chosen, go to add a reminder
        Intent i = new Intent(getApplicationContext(), MRemind.class);

        startActivity(i);

    }//end setAlarm
}
