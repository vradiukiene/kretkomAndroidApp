package lt.kvk.i11.radiukiene.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import lt.kvk.i11.radiukiene.R;
import lt.kvk.i11.radiukiene.controller.CalendarActivity;


/**
 * Created by Vita on 4/23/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";
    String street_id;
    DatabaseHandler databaseHandler;
    ArrayList<GS> notif = new ArrayList<>();
    SessionManager sessionManager;
    HashMap<String, String> user;
    ReminderData reminderData;
    @Override
    public void onReceive(Context context, Intent intent) {

        databaseHandler = new DatabaseHandler(context);
        sessionManager = new SessionManager(context);
        user = sessionManager.getStreetDetails();
        street_id = user.get(sessionManager.KEY_ID);
        notif = databaseHandler.getRemainderDb(street_id);
        reminderData = new ReminderData(context);

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");

                if (notif.size() > 0){
                    for (int i = 0; i<notif.size();i++){
                        NotificationScheduler.setReminder(context, AlarmReceiver.class, reminderData.get_hour(), reminderData.get_min());
                    }
                }
                return;
            }
        }
        Log.d(TAG, "onReceive: ");

        //Trigger the notification
        if (notif.size() > 0){

            Calendar c = Calendar.getInstance();

            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            c.add(Calendar.DATE, 1);
            String formattedDate = df.format(c.getTime());

            for (int i = 0; i<notif.size();i++){

                if (notif.get(i).wasteCollect_date.contains(formattedDate)) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        String id = "id_atliekos";
                        // The user-visible name of the channel.
                        CharSequence name = "Atliekos";
                        // The user-visible description of the channel.
                        String description = "Pranešimai apie atliekų išvežimą";
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                        // Configure the notification channel.
                        mChannel.setDescription(description);
                        mChannel.enableLights(true);
                        // Sets the notification light color for notifications posted to this
                        // channel, if the device supports this feature.
                        mChannel.setLightColor(Color.RED);
                        notificationManager.createNotificationChannel(mChannel);

                        Intent intent1 = new Intent(context, CalendarActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 123, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,"id_product")
                                .setSmallIcon(R.drawable.logo_round) //your app icon
                                .setBadgeIconType(R.drawable.logo_round) //your app icon
                                .setChannelId(id)
                                .setContentTitle("Kretingos komunalininkas")
                                .setAutoCancel(true).setContentIntent(pendingIntent)
                                .setNumber(1)
                                .setColor(255)
                                .setContentText("Ryt išvežamos " + notif.get(i).title + " atliekos.")
                                .setWhen(System.currentTimeMillis());
                        notificationManager.notify(1, notificationBuilder.build());
                    }
                    else {
                        NotificationScheduler.showNotification(context, CalendarActivity.class, "Kretingos komunalininkas", "Ryt išvežamos " + notif.get(i).title + " atliekos.");
                    }
                }
            }
        }
    }
}
