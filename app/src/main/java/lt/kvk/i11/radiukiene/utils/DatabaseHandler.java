package lt.kvk.i11.radiukiene.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Vita on 4/23/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    public static final String DATABASE_NAME = "remainder_database";
    // Remainders table
    private static final String TABLE_REMAINDER = "remainder";
    // NewsFeed Table Columns names
    private static final String KEY_STREET_ID = "street_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_title = "title";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //NEWS FEED Table
        String CREATE_REMINDER_TABLE = "CREATE TABLE " + TABLE_REMAINDER + "("
                + KEY_STREET_ID + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_title + " TEXT" + ")";

        db.execSQL(CREATE_REMINDER_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMAINDER);
        // Create tables again
        onCreate(db);
    }

    // Adding remainder
    public void addRemainderDb(String id, String date, String title) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STREET_ID, id);
        values.put(KEY_DATE, date);
        values.put(KEY_title, title);

        // Inserting Row
        db.insert(TABLE_REMAINDER, null, values);
        db.close(); // Closing database connection
    }


    public ArrayList<GS> getRemainderDb(String id){
        ArrayList<GS> dataList = new ArrayList<GS>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REMAINDER + " WHERE " + KEY_STREET_ID + " = '" + id +"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GS gs = new GS();
                gs.id = (cursor.getString(0));
                gs.wasteCollect_date = (cursor.getString(1));
                gs.title = (cursor.getString(2));
                dataList.add(gs);
            } while (cursor.moveToNext());
        }

        // return list
        cursor.close();
        db.close();
        return dataList;
    }
}
