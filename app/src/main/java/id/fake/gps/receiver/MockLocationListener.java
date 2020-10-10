package id.fake.gps.receiver;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MockLocationListener implements LocationListener {
    public static final String TAG = MockLocationListener.class.getName();
    private Context context;

    public MockLocationListener(Context context) {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: " + location.isFromMockProvider());
        if (location.isFromMockProvider()){
            Toast.makeText(context, "mock detected system", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
