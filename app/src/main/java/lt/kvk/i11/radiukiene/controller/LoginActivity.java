package lt.kvk.i11.radiukiene.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.leakcanary.LeakCanary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import lt.kvk.i11.radiukiene.R;
import lt.kvk.i11.radiukiene.adapter.AutoAdapter;
import lt.kvk.i11.radiukiene.rest.RestApiClient;
import lt.kvk.i11.radiukiene.rest.RestApiInterface;
import lt.kvk.i11.radiukiene.utils.GS;
import lt.kvk.i11.radiukiene.utils.SessionManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    AutoCompleteTextView txt_streetName;
    Button btn_login;
    TextView loginText;
    private String name;
    private ArrayAdapter<GS> adapter;
    ArrayList<GS> names_list = new ArrayList<>();
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        LeakCanary.install(getApplication());
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(LoginActivity.this);

        //get all layouts/labels
        getIds();

        //To get autotextview suggestions
        getStreets();


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getvalues();

                if (TextUtils.isEmpty(name)){
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("Įveskite gatvės pavadinimą")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }else {
                    startActivity(new Intent(LoginActivity.this, CalendarActivity.class));
                }
            }
        });
    }

    private void getIds() {

        String customFont = "fonts/Bold.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), customFont);
        loginText = findViewById(R.id.enterStreet);
        loginText.setTypeface(typeface);

        txt_streetName = (AutoCompleteTextView) findViewById(R.id.autocomplete_street);
        btn_login = (Button)findViewById(R.id.button_loginStreet);
        btn_login.setTypeface(typeface);
    }

    private void getvalues(){
        name = txt_streetName.getText().toString();
    }

    private void getStreets() {
        RestApiInterface restApiInterface = RestApiClient.getClient().create(RestApiInterface.class);
        Call<ResponseBody> call = restApiInterface.getStreets();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String json = "";
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("Names :", "" + json);

                names_list.clear();

                try {
                    JSONObject object = new JSONObject(json);
                    JSONArray jsonArray = object.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        GS map = new GS();
                        map.id = c.getString("id");
                        map.name = c.getString("street_name");

                        names_list.add(map);
                    }

                    txt_streetName.setThreshold(1);

                    adapter = new AutoAdapter(LoginActivity.this, R.layout.row_street, R.id.text_auto, names_list);
                    txt_streetName.setAdapter(adapter);
                    txt_streetName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String id = names_list.get(i).getId();
                            String name = names_list.get(i).getName();

                            //store selected street name and id
                            sessionManager.createLoginSession(name, id);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
}
