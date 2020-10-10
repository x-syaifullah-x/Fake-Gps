package id.fake.gps.utils.other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

public class NetWorkInfo {
    private static final String TAG = NetWorkInfo.class.getName();

    public static boolean isNetWorkAvailableNow(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } else {
            return false;
        }
    }

    private static boolean isOnline() {
        try {
            Process ipProcess = Runtime.getRuntime().exec("/system/bin/ping -w 2 8.8.8.8");
            return ipProcess.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            Log.i(TAG, "isOnline: " + e.getMessage());
            return false;
        }
    }
}
