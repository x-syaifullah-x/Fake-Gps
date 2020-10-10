package id.fake.gps.favorite;

import android.app.Application;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import id.fake.gps.db.local.DatabaseContract;
import id.fake.gps.utils.async.LoadDatabaseAsync;

import static id.fake.gps.db.local.DatabaseContract.CONTENT_URI_FAVORITE;

public class FavoriteViewModel extends AndroidViewModel {
    private MutableLiveData<Cursor> cursorMutableLiveData;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        cursorMutableLiveData = new MutableLiveData<>(application.getContentResolver().query(DatabaseContract.CONTENT_URI_FAVORITE, null, null, null, "id DESC"));
        application.getContentResolver().registerContentObserver(CONTENT_URI_FAVORITE, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                loadData();
            }
        });
    }

    public MutableLiveData<Cursor> getCursorMutableLiveData() {
        return cursorMutableLiveData;
    }

    private void loadData() {
        new LoadDatabaseAsync(getApplication(), cursor -> {
            if (cursor != null) {
                cursorMutableLiveData.setValue(cursor);
            }
        }, null, null, null, "id DESC").execute(CONTENT_URI_FAVORITE);
    }
}
