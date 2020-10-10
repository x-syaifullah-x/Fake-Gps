package id.fake.gps.utils.async;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.StringTokenizer;

import static java.lang.Double.parseDouble;

/* class for Search location with name or lat lang */
public class GeoCoderAsync extends AsyncTask<Void, Void, Address> {
    public static final String LOCATION_NOT_FOUND = "location not found";
    public static final String NO_CONNECTION = "no connection";
    private final String TAG = GeoCoderAsync.class.getName();
    private OnListenerGeoCoderAsync onListenerGeoCoderAsync;
    private WeakReference<Context> weakReferenceContext;
    private String values;
    private String message;

    public GeoCoderAsync(Context context, String values, OnListenerGeoCoderAsync onListenerGeoCoderAsync) {
        this.weakReferenceContext = new WeakReference<>(context);
        this.values = values;
        this.onListenerGeoCoderAsync = onListenerGeoCoderAsync;
    }

    @Override
    protected Address doInBackground(Void... voids) {
        return getAddress(values);
    }

    private Address getAddress(String values) {
        double[] latLang = null;

        /* handle location with latitude dan longitude */
        if (values.trim().matches("[-]?[0-9]*[.]?[0-9]*[,][-]?[0-9]*[.]?[0-9]*")) {
            StringTokenizer stringTokenizer = new StringTokenizer(values.trim(), ",");
            latLang = new double[]{parseDouble(stringTokenizer.nextToken()), parseDouble(stringTokenizer.nextToken())};
        }
        try {
            if (latLang != null) {
                return new Geocoder(weakReferenceContext.get()).getFromLocation(latLang[0], latLang[1], 1).get(0);
            } else {
                return new Geocoder(weakReferenceContext.get()).getFromLocationName(values, 1).get(0);
            }
        } catch (IOException | IndexOutOfBoundsException | IllegalArgumentException e) {
            Log.e(TAG, "getAddress: " + e.getMessage());
            if (e instanceof IndexOutOfBoundsException) {
                message = LOCATION_NOT_FOUND;
            } else if (e instanceof IOException) {
                message = NO_CONNECTION;
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(Address address) {
        super.onPostExecute(address);
        if (onListenerGeoCoderAsync != null) {
            onListenerGeoCoderAsync.onListenerGeoCoderAsync(address, message);
        } else {
            throw new UnsupportedOperationException("please implements " + OnListenerGeoCoderAsync.class.getName());
        }
    }

    public interface OnListenerGeoCoderAsync {
        void onListenerGeoCoderAsync(Address address, String message);
    }
}
