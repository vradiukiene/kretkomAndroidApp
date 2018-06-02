package lt.kvk.i11.radiukiene.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import lt.kvk.i11.radiukiene.controller.CalendarActivity;
import lt.kvk.i11.radiukiene.controller.LoginActivity;

/**
 * Created by Vita on 4/23/2018.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor1;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // Street name (variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";


    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor1 = pref.edit();
    }

     // Create login session
    public void createLoginSession(String name, String id){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ID, id);
        // commit changes
        editor.commit();
    }


    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);
        }else {
            _context.startActivity(new Intent(_context, CalendarActivity.class));
        }
    }

     // Get stored session data
    public HashMap<String, String> getStreetDetails(){
        HashMap<String, String> street = new HashMap<String, String>();
        // street name
        street.put(KEY_NAME, pref.getString(KEY_NAME, null));

        street.put(KEY_ID, pref.getString(KEY_ID, null));
        // return street
        return street;
    }

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}