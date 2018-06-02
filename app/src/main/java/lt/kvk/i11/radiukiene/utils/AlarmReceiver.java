package lt.kvk.i11.radiukiene.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import lt.kvk.i11.radiukiene.controller.ReminderActivity;

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
    ReminderData localData;
    @Override
    public void onReceive(Context context, Intent intent) {

        databaseHandler = new DatabaseHandler(context);
        sessionManager = new SessionManager(context);
        user = sessionManager.getStreetDetails();
        street_id = user.get(sessionManager.KEY_ID);
        notif = databaseHandler.getRemainderDb(street_id);
        localData = new ReminderData(context);

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");

                if (notif.size() > 0){
                    for (int i = 0; i<notif.size();i++){
                        NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min());
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
                    NotificationScheduler.showNotification(context, ReminderActivity.class, "Kretingos komunalininkas", "Ryt išvežamos " + notif.get(i).title + " atliekos.");
                }
            }
        }
    }
}
