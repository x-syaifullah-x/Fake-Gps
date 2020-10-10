package id.fake.gps.service;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import xxx.FGS;
import xxx.MR;

import static java.lang.Double.parseDouble;
import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;

public abstract class FakeGpsService extends Service implements Fake.Callback {
    public final String TAG = FakeGpsService.class.getName();

    public static final String ACTION_MOCK_LOCATION_DISABLE = "ACTION_MOCK_LOCATION_DISABLE";
    public static final String ACTION_FAKE_GPS_START = "ACTION_FAKE_GPS_START";
    public static final String ACTION_FAKE_GPS_STOP = "ACTION_FAKE_GPS_STOP";

    private final Fake fake = new Fake(this);
    private LocationManager locationManager;
    private double locationLatitude = -0.0d;
    private double locationLongitude = -0.0d;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return new FakeGpsServiceBinder();
    }

    public class FakeGpsServiceBinder extends Binder {
        public FakeGpsService getService() {
            return FakeGpsService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (this.locationManager != null) {
            fake.setUp(locationManager);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        if (intent != null) {
            if (intent.getBooleanExtra("stop", false)) {
                stopSelf();
            }

            /* put last position on shared preferences */
            getSharedPreferences("position", MODE_PRIVATE).edit()
                    .putString("latitude", valueOf(intent.getDoubleExtra("latitude", 0)))
                    .putString("longitude", valueOf(intent.getDoubleExtra("longitude", 0)))
                    .apply();
            /* put last position on shared preferences */

            locationLatitude = intent.getDoubleExtra("latitude", 0);
            locationLongitude = intent.getDoubleExtra("longitude", 0);
        }

        fake.start(locationManager, locationLatitude, locationLongitude);
        return START_STICKY;
    }

    @Override
    public void onSetup(int onSetup) {
        Log.i(TAG, "onSetup: ");
        if (onSetup == Fake.SECURITY_EXCEPTION) {
            sendBroadcast(new Intent(this, MR.class).setAction(ACTION_MOCK_LOCATION_DISABLE).putExtra("message", "SECURITY_EXCEPTION"));
            startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            stopSelf();
        }
    }

    @Override
    public void onStart(boolean isRunning) {
        Log.i(TAG, "onStart: ");
        if (isRunning) {
            sendBroadcast(new Intent(this, MR.class).setAction(ACTION_FAKE_GPS_START)
                    .putExtra("latitude", locationLatitude)
                    .putExtra("longitude", locationLongitude));
        } else {
            stopSelf();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        startService(
                new Intent(this, FGS.class)
                        .putExtra("latitude", parseDouble(requireNonNull(getSharedPreferences("position", MODE_PRIVATE).getString("latitude", "0"))))
                        .putExtra("longitude", parseDouble(requireNonNull(getSharedPreferences("position", MODE_PRIVATE).getString("longitude", "0"))))
        );
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        fake.stop(locationManager);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop: ");
        sendBroadcast(new Intent(this, MR.class).setAction(ACTION_FAKE_GPS_STOP));
    }
}
