package lt.kvk.i11.radiukiene.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import lt.kvk.i11.radiukiene.MyApplication;

/**
 * Created by Vita on 5/24/2018.
 */

public class ConnectionDetector {

    private static ConnectionDetector mConnectionDetector = new ConnectionDetector();

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private ConnectionDetector() {
    }

    /* Static 'instance' method */
    public static ConnectionDetector getInstance() {
        if (mConnectionDetector == null) {
            mConnectionDetector = new ConnectionDetector();
        }
        return mConnectionDetector;
    }

    /**
     * To check that user has enabled internet connection or not
     *
     * @return
     */
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) MyApplication.getApplicationContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    /**
     * To show the alert dialog if user don't have internet connection with custom title and message
     *
     * @param title title to be displayed in alert dialog
     * @param msg   message to be displayed in alert dialog
     * @param act   current context in which dialog will display
     */
    public void show_alert(String title, String msg, Activity act) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
        // set title
        alertDialogBuilder.setTitle(title);
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Gerai", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    /**
     * To show the alert dialog if user don't have internet connection
     *
     * @param activity current context in which dialog will display
     */
    public void show_alert(Activity activity) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        // set title
        alertDialogBuilder.setTitle("Ryšio klaida");
        // set dialog message
        alertDialogBuilder
                .setMessage("Oi !! Jūs praradote savo interneto ryšį.")
                .setCancelable(false)
                .setPositiveButton("Gerai", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}
