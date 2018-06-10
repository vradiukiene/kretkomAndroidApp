package lt.kvk.i11.radiukiene.controller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.squareup.leakcanary.LeakCanary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lt.kvk.i11.radiukiene.R;
import lt.kvk.i11.radiukiene.adapter.WastesAdapter;
import lt.kvk.i11.radiukiene.rest.RestApiClient;
import lt.kvk.i11.radiukiene.rest.RestApiInterface;
import lt.kvk.i11.radiukiene.utils.ConnectionDetector;
import lt.kvk.i11.radiukiene.utils.DatabaseHandler;
import lt.kvk.i11.radiukiene.utils.GS;
import lt.kvk.i11.radiukiene.utils.SessionManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity {

    CompactCalendarView compactCalendarView;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    TextView txt_date;
    ArrayList<GS> timelist = new ArrayList<>();
    ArrayList<GS> wastes = new ArrayList<>();
    private String street_id;
    private long timestamp;
    Button btn_changeStreet, btn_setRemainder;
    SessionManager sessionManager;
    HashMap<String, String> street = new HashMap<>();
    RecyclerView recyclerView;
    WastesAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(getApplication());

        setContentView(R.layout.activity_calendar);

        databaseHandler = new DatabaseHandler(CalendarActivity.this);
        sessionManager = new SessionManager(CalendarActivity.this);
        street = sessionManager.getStreetDetails();

        street_id = street.get(sessionManager.KEY_ID);

        getIdS();

        if(ConnectionDetector.getInstance().isConnectingToInternet()) {
            timelist.clear();
            wastes.clear();
            getTimetable();
            getWastes();
        }else {
            timelist.clear();
            wastes.clear();
            getAllWasteCollectionDb();
            getAllWastesFromLocalDb();
        }

        txt_date.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        // define a listener to receive callbacks when certain events happen
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Log.d("TAG", "Day was clicked: " + dateClicked + " with events " + events);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                txt_date.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        compactCalendarView.invalidate();

        btn_changeStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check internet connection for change street
                if(ConnectionDetector.getInstance().isConnectingToInternet()) {
                    //Delete All WasteCollectionList Data By StreetId
                    databaseHandler.deleteAllCollectionsListByStreetId(street_id);
                    //Delete All Wastes
                    databaseHandler.deleteAllWasteListData();
                    startActivity(new Intent(CalendarActivity.this, LoginActivity.class));
                }else {
                    ConnectionDetector.getInstance().show_alert(CalendarActivity.this);
                }
            }
        });

        btn_setRemainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CalendarActivity.this, ReminderActivity.class));
            }
        });
    }

    private void getIdS() {

        String customFont = "fonts/Bold.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), customFont);

        btn_changeStreet = (Button) findViewById(R.id.change_street);
        btn_changeStreet.setTypeface(typeface);

        btn_setRemainder = (Button)findViewById(R.id.btn_set_remainder);
        btn_setRemainder.setTypeface(typeface);

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        txt_date = (TextView) findViewById(R.id.calender_txt_date);
        txt_date.setTypeface(typeface);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(CalendarActivity.this, 3);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void getTimestamp(String wst_date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = (Date) formatter.parse(wst_date);
            timestamp = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //Get street time table
    private void getTimetable() {
        RestApiInterface restApiInterface = RestApiClient.getClient().create(RestApiInterface.class);
        Call<ResponseBody> call = restApiInterface.getTimetable(street_id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String json = "";
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("Time List :", "" + json);

                timelist.clear();

                try {
                    JSONObject object = new JSONObject(json);

                    JSONArray jsonArray = object.getJSONArray("result");

                    if(jsonArray.length() > 0){
                        if(databaseHandler.isExistWasteCollectionsListByStreetId(street_id)) {
                            databaseHandler.deleteAllCollectionsListByStreetId(street_id);
                        }
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);
                        GS map = new GS();
                        map.id = c.getString("street_id");
                        map.waste_id = c.getString("waste_id");
                        map.title = c.getString("waste_name");
                        map.wasteCollect_date = c.getString("wasteCollect_date");
                        map.wasteCollection_id = c.getString("id");
                        timelist.add(map);
                        databaseHandler.addRemainderDb(c.getString("street_id"), c.getString("wasteCollect_date"), /*"",*/ c.getString("waste_name"));

                        // Adding WasteCollections into local db
                        databaseHandler.addWasteCollectionDb(c.getString("id"),c.getString("waste_name"),c.getString("street_id"),c.getString("waste_id"),c.getString("wasteCollect_date"));
                    }

                    if (timelist.size() > 0) {
                        for (int i = 0; i < timelist.size(); i++) {
                            //Convert date to timestamp
                            getTimestamp(timelist.get(i).wasteCollect_date);
                            //Add event on date
                            Event event = null;

                            if (timelist.get(i).waste_id.equals("1"))
                                event = new Event(Color.BLUE, timestamp, "Žaliosios birios");
                            if (timelist.get(i).waste_id.equals("2"))
                                event = new Event(Color.GRAY, timestamp, "Žaliosios šakos");
                            if (timelist.get(i).waste_id.equals("3"))
                                event = new Event(Color.RED, timestamp, "Komunalinės atliekos");
                            if (timelist.get(i).waste_id.equals("4"))
                                event = new Event(Color.YELLOW, timestamp, "Plastikas");
                            if (timelist.get(i).waste_id.equals("5"))
                                event = new Event(Color.GREEN, timestamp, "Stiklas");
                            if (timelist.get(i).waste_id.equals("6"))
                                event = new Event(Color.BLACK, timestamp, "Didžiosios, elektros ir elektronikos įrangos");
                            compactCalendarView.addEvent(event);
                        }
                    }
                    List<Event> events = compactCalendarView.getEvents(1223344); // can also take a Date object
                    Log.d("TAG", "Events: " + events);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    //To get Wastes below calendar
    private void getWastes() {

        RestApiInterface restApiInterface = RestApiClient.getClient().create(RestApiInterface.class);
        Call<ResponseBody> call = restApiInterface.getWastes();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String json = "";
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("Wastes :", "" + json);
                wastes.clear();

                try {
                    JSONObject object = new JSONObject(json);
                    JSONArray jsonArray = object.getJSONArray("result");
                    if(jsonArray.length() > 0){
                        if(databaseHandler.isExistWastesList()){
                            databaseHandler.deleteAllWasteListData();
                        }
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);
                        GS map = new GS();
                        map.waste_id = c.getString("id");
                        map.name = c.getString("waste_name");
                        wastes.add(map);

                        //Adding wastes entry insert into local db
                        databaseHandler.addWastesDb(c.getString("id"),c.getString("waste_name"));
                    }
                    adapter = new WastesAdapter(CalendarActivity.this, wastes);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //get all WasteCollectionList Data By StreetId from local database
    private void getAllWasteCollectionDb()
    {
        if(databaseHandler.isExistWasteCollectionsListByStreetId(street_id)) {
            timelist.clear();
            timelist = databaseHandler.getAllWasteCollectionDb(street_id);
            if (timelist.size() > 0) {
                for (int i = 0; i < timelist.size(); i++) {
                    //Convert date to timestamp
                    getTimestamp(timelist.get(i).wasteCollect_date);
                    //Add event on date
                    Event event = null;

                    if (timelist.get(i).waste_id.equals("1"))
                        event = new Event(Color.BLUE, timestamp, "Žaliosios birios");
                    if (timelist.get(i).waste_id.equals("2"))
                        event = new Event(Color.GRAY, timestamp, "Žaliosios šakos");
                    if (timelist.get(i).waste_id.equals("3"))
                        event = new Event(Color.RED, timestamp, "Komunalinės atliekos");
                    if (timelist.get(i).waste_id.equals("4"))
                        event = new Event(Color.YELLOW, timestamp, "Plastikas");
                    if (timelist.get(i).waste_id.equals("5"))
                        event = new Event(Color.GREEN, timestamp, "Stiklas");
                    if (timelist.get(i).waste_id.equals("6"))
                        event = new Event(Color.BLACK, timestamp, "Didžiosios, elektros ir elektronikos įrangos");
                    compactCalendarView.addEvent(event);
                }
            }
            List<Event> events = compactCalendarView.getEvents(1223344); // can also take a Date object
            Log.d("TAG", "Events: " + events);
        }
    }

    // get all wastes from local database
    private void getAllWastesFromLocalDb()
    {
        if(databaseHandler.isExistWastesList()){
            wastes.clear();
            wastes = databaseHandler.getAllWasteDb();
            if(wastes.size() > 0 ){
                adapter = new WastesAdapter(CalendarActivity.this, wastes);
                recyclerView.setAdapter(adapter);
            }
        }
    }
}
