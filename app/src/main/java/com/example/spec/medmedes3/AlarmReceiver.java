package com.example.spec.medmedes3;



import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    //This receier class is meant to receive the time so the alarm can be used for the medicine
    //does not work at the moment, so a lot of code is sittng in comments until I can figure out which to keep

    @Override
    public void onReceive(Context rcontext, Intent intent)
    {
        //should take us to reminders, that way the user can see what medicine to take
        Log.e("Main Activity", "inside on receive of myreceiver");
        Intent service1 = new Intent(rcontext, MRemind.class);
        //rcontext.startService(service1);
        rcontext.startActivity(service1);
    }


   /* @Override
    public void onReceive(Context context, Intent intent) {
/*
        Intent intent1 = new Intent(context, MRemind.class);
        context.startService(intent1);*/
/*
        Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();
        int icon = R.drawable.icon;
        CharSequence tickerText = "Hello you have to take medicine I am Nitin Sharma";
        long when = System.currentTimeMillis();

        //Notification notification = new Notification(icon, tickerText,when );

        CharSequence contentTitle = "My notification";
        CharSequence contentText = "Hello World!";


        //notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        final int NOTIF_ID = 1234;
        NotificationManager notofManager = (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
        // Notification note = new Notification(R.drawable.face,"NEW ACTIVITY", System.currentTimeMillis());
        Intent notificationIntent = new Intent(context, MRemind.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0, notificationIntent, 0);
        Notification notification = new Notification(icon, tickerText,when );
        //Notification notification1 = new Notification(R.drawable.icon, "Wake up alarm", System.currentTimeMillis());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("My Notification")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("yo med time"))
                .setContentText("take your meds").setAutoCancel(true);
        //mBuilder.setSound(Notification.DEFAULT_SOUND);
        mBuilder.setContentIntent(contentIntent);
        notofManager.notify(1234, mBuilder.build());
        //notification.setLatestEventInfo(context, "My Activity", "This will runs on button click", contentIntent);
        notofManager.notify(NOTIF_ID,notification);

        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        //notification.setLatestEventInfo(context, "Context Title", "Context text", contentIntent);
        //notification.flags = Notification.FLAG_INSISTENT;
    }

        @Override
public void onReceive(Context context, Intent intent) {
        String message = "Hellooo, alrm worked ----";
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(context, MRemind.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
        }

public static  void setAlarm(Context context){
        Log.d("Carbon","Alrm SET !!");

        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        // add 30 seconds to the calendar object
        cal.add(Calendar.SECOND, 30);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the AlarmManager service
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
        }*/

}
