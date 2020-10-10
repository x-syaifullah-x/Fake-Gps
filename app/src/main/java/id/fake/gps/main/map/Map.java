package id.fake.gps.main.map;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Locale;

import id.fake.gps.utils.other.Request;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static java.lang.String.format;

public class Map extends ContextWrapper implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    public static final String TAG = Map.class.getName();
    public static final int REQUEST_CODE_ENABLE_GPS_FROM_MAP = 18;
    public static final int REQUEST_CODE_ACCESS_GPS = 43;

    public final float cameraZoom = 16f;

    public GoogleMap googleMap;
    public Location myLocation;
    public boolean isLocationFake = false;
    public LocationManager locationManager;

    private AppCompatActivity appCompatActivity;
    private Callback callback;

    public Map(Context base, SupportMapFragment supportMapFragment) {
        super(base);
        locationManager = ((LocationManager) base.getSystemService(LOCATION_SERVICE));
        this.callback = (Callback) base;
        appCompatActivity = (AppCompatActivity) getBaseContext();
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ");
        this.googleMap = googleMap;

        enableLocation();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(false);
        if (locationManager == null)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-6.208755648457744d, 106.84559896588326d), cameraZoom));
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMapClickListener(this);
    }

    public void enableLocation() {
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { /* Permission Check */
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                appCompatActivity.requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_GPS);
                return;
            }
        }
        if (provider != null) {
            googleMap.setMyLocationEnabled(true);
            locationManager.requestLocationUpdates(provider, 0, 500.0f, this);
        } else {
            Request.enableGps(appCompatActivity, REQUEST_CODE_ENABLE_GPS_FROM_MAP);
        }
    }

    public <T extends View> void setVewButtonMyPosition(T viewButtonMyPosition) {
        viewButtonMyPosition.setOnClickListener(v -> {
            if (myLocation != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), cameraZoom));
            } else {
                enableLocation();
            }
        });
    }

    public void setButtonMyPosition() {
//        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
//            // Get the button view
//            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//            // and next place it, on bottom right (as Google Maps app)
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//            // position on right bottom
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//            layoutParams.setMargins(0, 250, 0, 0);
//        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: ");
        this.myLocation = location;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), cameraZoom));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged: ");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled: " + provider);
        enableLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled: " + provider);
        if (!isLocationFake) {
            Request.enableGps((AppCompatActivity) getBaseContext(), REQUEST_CODE_ENABLE_GPS_FROM_MAP);
        }
    }

    @Override
    public void onCameraIdle() {
        Log.i(TAG, "onCameraIdle: ");
    }

    @Override
    public void onCameraMove() {
        Log.i(TAG, "onCameraMove: ");
        if (myLocation != null) {
            if (format(Locale.getDefault(), "%.3f", googleMap.getCameraPosition().target.latitude).equals(format(Locale.getDefault(), "%.3f", myLocation.getLatitude()))) {
                if (format(Locale.getDefault(), "%.3f", googleMap.getCameraPosition().target.longitude).equals(format(Locale.getDefault(), "%.3f", myLocation.getLongitude()))) {
                    callback.onCameraMove(true);
                }
            } else {
                callback.onCameraMove(false);
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i(TAG, "onInfoWindowClick: ");
        callback.onInfoWindowClick(marker);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        Log.i(TAG, "onMapClick: ");
        callback.onMapClick(googleMap, latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.i(TAG, "onMapLongClick: ");
        callback.onMapLongClick(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i(TAG, "onMarkerClick: ");
        marker.remove();
        return true;
    }

    public interface Callback {
        void onCameraMove(boolean isMyLocation);

        void onMapClick(GoogleMap googleMap, LatLng latLng);

        void onMapLongClick(LatLng latLng);

        void onInfoWindowClick(Marker marker);
    }
}
