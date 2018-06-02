package lt.kvk.i11.radiukiene.controller;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.HashMap;

import lt.kvk.i11.radiukiene.R;
import lt.kvk.i11.radiukiene.utils.AlarmReceiver;
import lt.kvk.i11.radiukiene.utils.ReminderData;
import lt.kvk.i11.radiukiene.utils.NotificationScheduler;
import lt.kvk.i11.radiukiene.utils.SessionManager;

public class ReminderActivity extends AppCompatActivity {

    String TAG = "RemindMe";
    Button btn_save;
    TextView note;
    SessionManager sessionManager;
    HashMap<String, String> user;
    SwitchCompat reminderSwitch;
    ReminderData reminderData;
    int hour, min;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        sessionManager = new SessionManager(ReminderActivity.this);
        user = sessionManager.getStreetDetails();
        reminderData = new ReminderData(getApplicationContext());

        getIdS();

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = timePicker.getCurrentHour();
                min = timePicker.getCurrentMinute();
                reminderData.set_hour(hour);
                reminderData.set_min(min);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set notification
                NotificationScheduler.setReminder(ReminderActivity.this, AlarmReceiver.class, reminderData.get_hour(), reminderData.get_min());
                startActivity(new Intent(ReminderActivity.this, CalendarActivity.class));
            }

        });
    }

    private void getIdS() {

        String customFont = "fonts/Bold.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), customFont);
        btn_save = (Button) findViewById(R.id.btn_setReminder);
        btn_save.setTypeface(typeface);

        note = (TextView) findViewById(R.id.note);
        note.setTypeface(typeface);

        timePicker = (TimePicker) findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        reminderSwitch = (SwitchCompat) findViewById(R.id.timerSwitch);

        hour = reminderData.get_hour();
        min = reminderData.get_min();

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminderData.setReminderStatus(isChecked);
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: true");
                } else {
                    Log.d(TAG, "onCheckedChanged: false");
                    NotificationScheduler.cancelReminder(ReminderActivity.this, AlarmReceiver.class);
                }
            }
        });
    }
}
