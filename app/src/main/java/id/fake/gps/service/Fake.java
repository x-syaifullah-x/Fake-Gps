package id.fake.gps.service;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import static android.location.LocationManager.GPS_PROVIDER;

public class Fake {
    public static final int SECURITY_EXCEPTION = -1;
    private Fake.Callback callback;
    private Handler handler = new Handler();
    private Runnable runnable;

    public Fake(Callback callback) {
        this.callback = callback;
    }

    public void setUp(LocationManager locationManager) {
        try {
            locationManager.removeTestProvider(GPS_PROVIDER);
        } catch (IllegalArgumentException | SecurityException e) {
            if (e instanceof SecurityException) {
                Log.e("Fake", "setUp: SECURITY_EXCEPTION" + e.getMessage());
                callback.onSetup(SECURITY_EXCEPTION);
                return;
            } else {
                Log.i("Fake", "setUp: IllegalArgumentException : " + e.getMessage());
            }
        }
        try {
            locationManager.addTestProvider(GPS_PROVIDER, true, true, false, false, true, true, true, 1, 0);
            locationManager.setTestProviderEnabled(GPS_PROVIDER, true);
        } catch (RuntimeException e) {
            Log.e("setup", "RuntimeException : " + e.getMessage());
        }

        callback.onSetup(1);
    }

    public void start(LocationManager locationManager, double latitude, double longitude) {
        AtomicInteger post = new AtomicInteger();
        handler.removeCallbacks(runnable);
        handler.post(runnable = () -> { /* start mock */
            if (latitude != -0.0d && longitude != -0.0d) {
                Location location = new Location(GPS_PROVIDER);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                location.setAccuracy(0f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    location.setTime(SystemClock.elapsedRealtimeNanos());
                    location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                } else {
                    location.setTime(System.currentTimeMillis());
                }
                locationManager.setTestProviderLocation(GPS_PROVIDER, location);
                if (post.get() <= 3) post.addAndGet(1);
                if (post.get() == 3) {
                    callback.onStart(true);
                }
            } else {
                callback.onStart(false);
            }
            handler.postDelayed(runnable, 300);
        });
    }

    public void stop(LocationManager locationManager) {
        try {
            if (locationManager.getProvider("gps") != null) {
                locationManager.removeTestProvider("gps");
            }
        } catch (RuntimeException e) {
            Log.e("Fake", "stop: " + e.getMessage());
        }

        if (this.handler != null && this.runnable != null) {
            this.handler.removeCallbacks(this.runnable);
            this.handler = null;
            this.runnable = null;
        }
        callback.onStop();
    }

    public interface Callback {
        void onStart(boolean isRunning);

        void onStop();

        void onSetup(int onSetup);
    }
}
