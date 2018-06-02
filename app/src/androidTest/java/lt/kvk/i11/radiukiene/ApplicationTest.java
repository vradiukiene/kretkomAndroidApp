package lt.kvk.i11.radiukiene;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

import lt.kvk.i11.radiukiene.utils.DatabaseHandler;

/**
 * Created by Vita on 4/25/2018.
 */

public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest(){
        super(Application.class);
    }
    public void testCreateDb(){
        mContext.deleteDatabase(DatabaseHandler.DATABASE_NAME);
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }
}

