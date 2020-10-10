package id.fake.gps.main;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import id.fake.gps.R;
import id.fake.gps.base.BaseActivity;
import id.fake.gps.base.BaseActivity.ILocalReceiver;
import id.fake.gps.databinding.ActivityMainBinding;
import id.fake.gps.dialog.DialogAddDataFragment;
import id.fake.gps.favorite.FavoriteEntity;
import id.fake.gps.history.HistoryEntity;
import id.fake.gps.main.map.Map;
import id.fake.gps.utils.other.Request;
import xxx.FGS;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static id.fake.gps.favorite.FavoriteActivity.FAVORITE_DATA_EXTRA;
import static id.fake.gps.history.HistoryViewHolder.HISTORY_DATA_EXTRA;
import static id.fake.gps.main.map.Map.REQUEST_CODE_ACCESS_GPS;
import static id.fake.gps.main.map.Map.REQUEST_CODE_ENABLE_GPS_FROM_MAP;
import static id.fake.gps.search.SearchActivity.EXTRA_DATA_LATITUDE;
import static id.fake.gps.search.SearchActivity.EXTRA_DATA_LONGITUDE;
import static java.util.Objects.requireNonNull;

public abstract class MainActivity extends BaseActivity<ActivityMainBinding> implements Map.Callback, ILocalReceiver {
    public static final String TAG = MainActivity.class.getName();
    public static final String ACTION_FAKE_STARTED = "FAKE_STARTED";
    public static final String ACTION_FAKE_STOP = "FAKE_STOP";
    private static final short REQUEST_CODE_ENABLE_GPS_FROM_START_FAKE = 10;

    private Map map;
    private LatLng latLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_main);
        getBinding().setEventClick(new EventMain());
        map = new Map(
                this, ((SupportMapFragment) requireNonNull(getSupportFragmentManager().findFragmentById(R.id.map)))
        );
        map.setVewButtonMyPosition(getBinding().ibMyPosition);

        if (isServiceRunning(FGS.class.getName())) {
            getBinding().ibStop.setVisibility(VISIBLE);
            setLocalReceiver(ACTION_FAKE_STOP);
        } else {
            /* Handle Application Not Auto start & Mock location Disable */
            if (map.locationManager != null) {
                try {
                    map.locationManager.removeTestProvider(GPS_PROVIDER);
                } catch (IllegalArgumentException | SecurityException e) {
                    Log.e(TAG, "_onCreate: line 58 " + e.getMessage());
                }
            }
            getBinding().ibStop.setVisibility(GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE_ACCESS_GPS == requestCode) {
            if (grantResults[0] != 0 || grantResults[1] != 0) {
                getBinding().ibMyPosition.setVisibility(GONE);
                Toast.makeText(this, "Application not access location", Toast.LENGTH_LONG).show();
            } else {
                map.enableLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ENABLE_GPS_FROM_START_FAKE:
                    startFakeService(latLng);
                    break;
                case REQUEST_CODE_ENABLE_GPS_FROM_MAP:
                    map.enableLocation();
                    break;
                case EventMain.REQUEST_CODE_FROM_FAVORITE:
                    assert data != null;
                    FavoriteEntity favoriteEntity = data.getParcelableExtra(FAVORITE_DATA_EXTRA);
                    getBinding().ibAdd.setVisibility(GONE);
                    latLng = favoriteEntity != null ? new LatLng(favoriteEntity.getLatitude(), favoriteEntity.getLongitude()) : null;
                    addSingleMaker(map.googleMap, latLng);
                    break;
                case EventMain.REQUEST_CODE_FROM_HISTORY:
                    assert data != null;
                    HistoryEntity historyEntity = data.getParcelableExtra(HISTORY_DATA_EXTRA);
                    latLng = historyEntity != null ? new LatLng(historyEntity.getLatitude(), historyEntity.getLongitude()) : null;
                    addSingleMaker(map.googleMap, latLng);
                    break;
                case EventMain.REQUEST_CODE_FROM_SEARCH:
                    assert data != null;
                    latLng = new LatLng(data.getDoubleExtra(EXTRA_DATA_LATITUDE, 0), data.getDoubleExtra(EXTRA_DATA_LONGITUDE, 0));
                    addSingleMaker(map.googleMap, latLng);
                    break;
            }
        }
    }

    private void visibilityBtnMyPosition(int visibility) {
        if (getBinding().ibMyPosition.getVisibility() != visibility)
            getBinding().ibMyPosition.setVisibility(visibility);
    }

    public void startFakeService(LatLng latLng) {
        map.googleMap.clear();
        if (map.locationManager.getBestProvider(new Criteria(), true) != null) {
            startService(new Intent(this, FGS.class).putExtra("latitude", latLng.latitude).putExtra("longitude", latLng.longitude));
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
            setLocalReceiver(ACTION_FAKE_STARTED);
            setLocalReceiver(ACTION_FAKE_STOP);
        } else {
            this.latLng = latLng;
            Request.enableGps(this, REQUEST_CODE_ENABLE_GPS_FROM_START_FAKE);
        }
    }

    private void addSingleMaker(GoogleMap googleMap, LatLng latLng) {
        googleMap.clear();
        googleMap.setOnMarkerClickListener(map);
        googleMap.setOnInfoWindowClickListener(map);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
        googleMap.addMarker(new MarkerOptions().title("show location").position(latLng)).showInfoWindow();
    }

    @Override
    public void onCameraMove(boolean isMyLocation) {
        visibilityBtnMyPosition(isMyLocation ? GONE : VISIBLE);
    }

    @Override
    public void onMapClick(GoogleMap googleMap, LatLng latLng) {
        addSingleMaker(googleMap, latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        startFakeService(latLng);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        DialogFragment dialogAddFragment = new DialogAddDataFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", marker.getPosition().latitude);
        bundle.putDouble("longitude", marker.getPosition().longitude);
        dialogAddFragment.setArguments(bundle);
        dialogAddFragment.show(fragmentTransaction, "test");
    }

    @Override
    public void onLocalReceiver(Intent intent) {
        Log.i(TAG, "onReceive: ");
        if (ACTION_FAKE_STARTED.equals(intent.getAction())) {
            map.isLocationFake = true;
            getBinding().ibStop.setVisibility(VISIBLE);
        } else if (ACTION_FAKE_STOP.equals(intent.getAction())) {
            map.isLocationFake = false;
            getBinding().ibStop.setVisibility(GONE);
        }
    }
}