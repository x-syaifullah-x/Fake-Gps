package id.fake.gps.utils.async;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class LoadDatabaseAsync extends AsyncTask<Uri, Void, Cursor> {
    private WeakReference<Context> context;
    private WeakReference<OnLoadData> onLoadData;
    private String[] projection;
    private String selection;
    private String[] selectionArg;
    private String sortOrder;

    public LoadDatabaseAsync(Context context, OnLoadData onLoadData, String[] projection2, String selection2, String[] selectionArg2, String sortOrder2) {
        this.context = new WeakReference<>(context);
        this.onLoadData = new WeakReference<>(onLoadData);
        this.projection = projection2;
        this.selection = selection2;
        this.selectionArg = selectionArg2;
        this.sortOrder = sortOrder2;
    }

    public Cursor doInBackground(Uri... uris) {
        return this.context.get().getContentResolver().query(uris[0], this.projection, this.selection, this.selectionArg, this.sortOrder);

    }

    public void onPostExecute(Cursor cursor) {
        super.onPostExecute(cursor);
        onLoadData.get().onLoadData(cursor);
    }

    public interface OnLoadData {
        void onLoadData(Cursor cursor);
    }
}
