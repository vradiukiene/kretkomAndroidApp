package lt.kvk.i11.radiukiene.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Vita on 4/23/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    public static final String DATABASE_NAME = "remainder_database";
    // Remainders, waste collections, wastes tables
    private static final String TABLE_REMAINDER = "remainder";
    private static final String TABLE_WASTE_COLLECTIONS = "wasteCollections";
    private static final String TABLE_WASTES = "wastes";
    // REMINDER Table Columns names
    private static final String KEY_STREET_ID = "street_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_title = "title";

    // WASTES Table Columns names
    private static final String KEY_WASTES_ID = "id";
    private static final String KEY_WASTES_NAME = "waste_name";

    // WASTE_COLLECTIONS Table Columns names
    private static final String KEY_WASTE_COLLECTIONS_ID = "id";
    private static final String KEY_WASTE_COLLECTIONS_NAME = "waste_name";
    private static final String KEY_WASTE_COLLECTIONS_STREET_ID = "street_id";
    private static final String KEY_WASTE_COLLECTIONS_WASTES_ID = "waste_id";
    private static final String KEY_WASTE_COLLECTIONS_DATE = "wasteCollect_date";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //REMINDER Table
        String CREATE_REMINDER_TABLE = "CREATE TABLE " + TABLE_REMAINDER + "("
                + KEY_STREET_ID + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_title + " TEXT" + ")";

        db.execSQL(CREATE_REMINDER_TABLE);

        //WASTES Table
        String CREATE_WASTES_TABLE = "CREATE TABLE " + TABLE_WASTES + "("
                + KEY_WASTES_ID + " TEXT,"
                + KEY_WASTES_NAME + " TEXT" + ")";

        db.execSQL(CREATE_WASTES_TABLE);

        //wasteCollections Table
        String CREATE_WASTE_COLLECTIONS_TABLE = "CREATE TABLE " + TABLE_WASTE_COLLECTIONS + "("
                + KEY_WASTE_COLLECTIONS_ID + " TEXT,"
                + KEY_WASTE_COLLECTIONS_NAME + " TEXT,"
                + KEY_WASTE_COLLECTIONS_STREET_ID + " TEXT,"
                + KEY_WASTE_COLLECTIONS_WASTES_ID + " TEXT,"
                + KEY_WASTE_COLLECTIONS_DATE + " TEXT" + ")";

        db.execSQL(CREATE_WASTE_COLLECTIONS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMAINDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WASTE_COLLECTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WASTES);
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

    // check WastesList is exist or not
    public Boolean isExistWastesList() {
        int count = 0;
        Boolean isAvalable = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_WASTES;
        Cursor cursor = db.rawQuery(selectQuery, null);
        count = cursor.getCount();
        if (count > 0) {
            isAvalable = true;
        } else {
            isAvalable = false;
        }
        db.close(); // Closing database connection
        return isAvalable;
    }

    // Adding wastes
    public void addWastesDb(String id, String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WASTES_ID, id);
        values.put(KEY_WASTES_NAME, name);

        // Inserting Row
        db.insert(TABLE_WASTES, null, values);
        db.close(); // Closing database connection
    }

    //get all WasteList Data
    public ArrayList<GS> getAllWasteDb(){
        ArrayList<GS> wasteList = new ArrayList<GS>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WASTES ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GS waste = new GS();
                waste.waste_id = (cursor.getString(0));
                waste.name = (cursor.getString(1));
                wasteList.add(waste);
            } while (cursor.moveToNext());
        }

        // return list
        cursor.close();
        db.close();
        return wasteList;
    }

    //Delete All WasteList Data
    public void deleteAllWasteListData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WASTES, null, null);
        Log.i("TABLE_WASTES Delete", TABLE_WASTES);
        db.close(); // Closing database connection
    }

    // check WasteCollectionsList By StreetId is exist or not
    public Boolean isExistWasteCollectionsListByStreetId(String streetId) {
        int count = 0;
        Boolean isAvalable = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_WASTE_COLLECTIONS + " WHERE " + KEY_WASTE_COLLECTIONS_STREET_ID + " = '" + streetId +"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        count = cursor.getCount();
        if (count > 0) {
            isAvalable = true;
        } else {
            isAvalable = false;
        }
        db.close(); // Closing database connection
        return isAvalable;
    }

    // Adding WasteCollections
    public void addWasteCollectionDb(String id, String waste_name, String street_id, String waste_id, String wasteCollect_date) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WASTE_COLLECTIONS_ID, id);
        values.put(KEY_WASTE_COLLECTIONS_NAME, waste_name);
        values.put(KEY_WASTE_COLLECTIONS_STREET_ID, street_id);
        values.put(KEY_WASTE_COLLECTIONS_WASTES_ID, waste_id);
        values.put(KEY_WASTE_COLLECTIONS_DATE, wasteCollect_date);

        // Inserting Row
        db.insert(TABLE_WASTE_COLLECTIONS, null, values);
        db.close(); // Closing database connection
    }

    //get all WasteCollectionList Data By StreetId
    public ArrayList<GS> getAllWasteCollectionDb(String streetId){
        ArrayList<GS> wasteCollectionList = new ArrayList<GS>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WASTE_COLLECTIONS + " WHERE " + KEY_WASTE_COLLECTIONS_STREET_ID + " = '" + streetId +"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GS wasteCollection = new GS();
                wasteCollection.id = (cursor.getString(0));
                wasteCollection.name = (cursor.getString(1));
                wasteCollection.street_id = (cursor.getString(2));
                wasteCollection.waste_id = (cursor.getString(3));
                wasteCollection.wasteCollect_date = (cursor.getString(4));
                wasteCollectionList.add(wasteCollection);
            } while (cursor.moveToNext());
        }

        // return list
        cursor.close();
        db.close();
        return wasteCollectionList;
    }

    //Delete All WasteCollectionList Data By StreetId
    public void deleteAllCollectionsListByStreetId(String streetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_WASTE_COLLECTIONS_STREET_ID + " = '" + streetId + "' ";
        db.delete(TABLE_WASTE_COLLECTIONS, where, null);
        Log.i("TABLE_WASTE_COLLECTIONS", TABLE_WASTE_COLLECTIONS);
        db.close(); // Closing database connection
    }
}
