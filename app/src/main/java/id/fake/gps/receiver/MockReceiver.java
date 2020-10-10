package id.fake.gps.receiver;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Locale;

import id.fake.gps.base.BaseReceiver;
import id.fake.gps.db.local.DatabaseContract;
import id.fake.gps.history.HistoryEntity;
import id.fake.gps.main.MainActivity;
import id.fake.gps.utils.async.GeoCoderAsync;

import static android.content.Context.LOCATION_SERVICE;
import static id.fake.gps.service.FakeGpsService.ACTION_FAKE_GPS_START;
import static id.fake.gps.service.FakeGpsService.ACTION_FAKE_GPS_STOP;
import static id.fake.gps.service.FakeGpsService.ACTION_MOCK_LOCATION_DISABLE;

public abstract class MockReceiver extends BaseReceiver {
    public static final String TAG = MockReceiver.class.getName();

    @Override
    public void onReceive(ContextWrapper context, Intent intent) {
        LocationManager locationManagerCompat = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (ACTION_MOCK_LOCATION_DISABLE.equals(intent.getAction())) {
            Toast.makeText(context, intent.getStringExtra("message"), Toast.LENGTH_LONG).show();
        } else if (ACTION_FAKE_GPS_START.equals(intent.getAction())) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainActivity.ACTION_FAKE_STARTED));
            Toast.makeText(context, "Mock Gps Start", Toast.LENGTH_LONG).show();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onReceive: permission location granted");
                if (locationManagerCompat != null) {
                    locationManagerCompat.requestSingleUpdate(LocationManager.GPS_PROVIDER, new MockLocationListener(context), Looper.getMainLooper());
                }
            }

            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude = intent.getDoubleExtra("longitude", 0);

            new MockNotification(context).showNotification(
                    String.format(Locale.getDefault(), "%.4f , %.4f", latitude, longitude)
            );

            new GeoCoderAsync(context, latitude + "," + longitude, (address, message) -> {
                context.getContentResolver().insert(
                        DatabaseContract.CONTENT_URI_HISTORY,
                        address != null ? HistoryEntity.insertData(address) : HistoryEntity.insertData("no name", "no address", latitude, longitude)
                );
            }).execute();
        } else if (ACTION_FAKE_GPS_STOP.equals(intent.getAction())) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainActivity.ACTION_FAKE_STOP));
            NotificationManagerCompat.from(context).cancel(MockNotification.ID_MOCK_NOTIFICATION);
            Toast.makeText(context, "Mock Gps Stop", Toast.LENGTH_LONG).show();
        }
    }
}