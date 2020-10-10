package id.fake.gps.db.local;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import static id.fake.gps.db.local.DatabaseContract.TABLE_NAME_FAVORITE;
import static id.fake.gps.db.local.DatabaseContract.TABLE_NAME_HISTORY;

public abstract class DatabaseProvider extends ContentProvider {
    private static final int URI_INVALID = -404;
    private static final UriMatcher URI_MATCHER = new UriMatcher(-1);
    private static final int URI_TABLE_NAME_HISTORY = 1;
    private static final int URI_TABLE_NAME_FAVORITE = 2;
    private ContentResolver contentResolver;
    private DatabaseHelper db;

    static {
        String str = DatabaseContract.AUTHORITY;
        URI_MATCHER.addURI(str, TABLE_NAME_HISTORY, 1);
        URI_MATCHER.addURI(str, TABLE_NAME_FAVORITE, 2);
    }

    @Override
    public boolean onCreate() {
        db = new DatabaseHelper(getContext());
        if (getContext() != null)
            contentResolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        try {
            int match = URI_MATCHER.match(uri);
            if (match != URI_INVALID) {
                if (match == URI_TABLE_NAME_HISTORY) {
                    cursor = db.getWritableDatabase().query(TABLE_NAME_HISTORY, projection, selection, selectionArgs, null, null, sortOrder);
                } else if (match == URI_TABLE_NAME_FAVORITE) {
                    cursor = db.getWritableDatabase().query(TABLE_NAME_FAVORITE, projection, selection, selectionArgs, null, null, sortOrder);
                }
                return cursor;
            }
            String sb = "Query -- Invalid URI:" + uri;
            throw new IllegalArgumentException(sb);
        } finally {
            if (cursor != null) {
                cursor.setNotificationUri(contentResolver, uri);
            }
        }
    }

    @Override
    public Uri insert(@NotNull Uri uri, ContentValues values) {
        try {
            int match = URI_MATCHER.match(uri);
            if (match == URI_INVALID) {
                String sb = "Query -- Invalid URI:" + uri;
                throw new IllegalArgumentException(sb);
            } else if (match == URI_TABLE_NAME_HISTORY) {
                Uri withAppendedPath = Uri.withAppendedPath(uri, Long.toString(db.getWritableDatabase().insertWithOnConflict(TABLE_NAME_HISTORY, null, values, 5)));
                contentResolver.notifyChange(uri, null);
                return withAppendedPath;
            } else if (match != URI_TABLE_NAME_FAVORITE) {
                return Uri.withAppendedPath(uri, Long.toString(0));
            } else {
                Uri withAppendedPath2 = Uri.withAppendedPath(uri, Long.toString(db.getWritableDatabase().insertWithOnConflict(TABLE_NAME_FAVORITE, null, values, 5)));
                contentResolver.notifyChange(uri, null);
                return withAppendedPath2;
            }
        } finally {
            contentResolver.notifyChange(uri, null);
        }
    }

    @Override
    public int delete(@NotNull Uri uri, String selection, String[] selectionArgs) {
        try {
            int match = URI_MATCHER.match(uri);
            if (match == URI_INVALID) {
                String sb = "Query -- Invalid URI:" + uri;
                throw new IllegalArgumentException(sb);
            } else if (match == URI_TABLE_NAME_HISTORY) {
                int delete = db.getWritableDatabase().delete(TABLE_NAME_HISTORY, selection, selectionArgs);
                contentResolver.notifyChange(uri, null);
                return delete;
            } else if (match != URI_TABLE_NAME_FAVORITE) {
                return 0;
            } else {
                int delete2 = db.getWritableDatabase().delete(TABLE_NAME_FAVORITE, selection, selectionArgs);
                contentResolver.notifyChange(uri, null);
                return delete2;
            }
        } finally {
            contentResolver.notifyChange(uri, null);
        }
    }

    @Override
    public int update(@NotNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        try {
            switch (URI_MATCHER.match(uri)) {
                case URI_TABLE_NAME_HISTORY:
                    return db.getWritableDatabase().update(TABLE_NAME_HISTORY, values, selection, selectionArgs);
                case URI_TABLE_NAME_FAVORITE:
                    return db.getWritableDatabase().update(TABLE_NAME_FAVORITE, values, selection, selectionArgs);
                case URI_INVALID:
                    throw new IllegalArgumentException("Query -- Invalid URI:" + uri);
                default:
                    return 0;
            }
        } finally {
            contentResolver.notifyChange(uri, null);
        }
    }

    @Override
    public String getType(@NotNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
