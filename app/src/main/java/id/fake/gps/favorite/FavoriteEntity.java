package id.fake.gps.favorite;

import android.content.ContentValues;
import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteEntity implements Parcelable {
    /* Content Values */
    private static ContentValues contentValues = new ContentValues();

    /* KEY TABLE */
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_DATE = "date";

    /* COLUMN TABLE  */
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_NAME = 1;
    public static final int COLUMN_ADDRESS = 2;
    public static final int COLUMN_LATITUDE = 3;
    public static final int COLUMN_LONGITUDE = 4;
    public static final int COLUMN_DATE = 5;

    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private Date date;

    public FavoriteEntity(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    protected FavoriteEntity(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Creator<FavoriteEntity> CREATOR = new Creator<FavoriteEntity>() {
        @Override
        public FavoriteEntity createFromParcel(Parcel source) {
            return new FavoriteEntity(source);
        }

        @Override
        public FavoriteEntity[] newArray(int size) {
            return new FavoriteEntity[size];
        }
    };

    public static ContentValues insertData(Address address) {
        contentValues.put(KEY_NAME, address.getFeatureName());
        contentValues.put(KEY_ADDRESS, address.getAddressLine(0));
        contentValues.put(KEY_LATITUDE, address.getLatitude());
        contentValues.put(KEY_LONGITUDE, address.getLongitude());
        contentValues.put(KEY_DATE, System.currentTimeMillis());
        return contentValues;
    }

    public static ContentValues insertData(String name, String address, double latitude, double longitude) {
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_ADDRESS, address);
        contentValues.put(KEY_LATITUDE, latitude);
        contentValues.put(KEY_LONGITUDE, longitude);
        contentValues.put(KEY_DATE, System.currentTimeMillis());
        return contentValues;
    }
}
