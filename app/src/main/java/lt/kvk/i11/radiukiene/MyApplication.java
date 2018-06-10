package lt.kvk.i11.radiukiene;

import android.app.Application;
import android.content.Context;

/**
 * Created by Vita on 5/24/2018.
 */

public class MyApplication extends Application {
    public static Context getApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationContext = this;
    }
}
