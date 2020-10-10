package id.fake.gps.db.local;

import android.net.Uri;
import android.net.Uri.Builder;

public class DatabaseContract {
    public static final String DATABASE_NAME = "db";
    public static final int DATABASE_VERSION = 1;
    public static final String AUTHORITY = "id.fake.gps.db.local";

    /* TABLE NAME */
    public static final String TABLE_NAME_FAVORITE = "favorite";
    public static final String TABLE_NAME_HISTORY = "history";

    /* CONTENT URI */
    public static final Uri CONTENT_URI_FAVORITE;
    public static final Uri CONTENT_URI_HISTORY;

    static {
        CONTENT_URI_HISTORY = new Builder().scheme("content").authority(AUTHORITY).appendPath(TABLE_NAME_HISTORY).build();
        CONTENT_URI_FAVORITE = new Builder().scheme("content").authority(AUTHORITY).appendPath(TABLE_NAME_FAVORITE).build();
    }

    /* CREATE TABLE */
    public static final String SQL_CREATE_DB_FAVORITE = "CREATE TABLE favorite (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,address TEXT,latitude TEXT,longitude TEXT,date TEXT, UNIQUE (id) ON CONFLICT REPLACE, UNIQUE (name) ON CONFLICT REPLACE)";
    public static final String SQL_CREATE_DB_HISTORY = "CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,address TEXT,latitude TEXT,longitude TEXT,date TEXT, UNIQUE (id) ON CONFLICT REPLACE, UNIQUE (address) ON CONFLICT REPLACE)";
}
