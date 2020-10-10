package id.fake.gps.history;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.fake.gps.R;
import id.fake.gps.base.BaseActivity;
import id.fake.gps.databinding.ActivityHistoryBinding;
import id.fake.gps.utils.async.GeoCoderAsync;

import static id.fake.gps.db.local.DatabaseContract.CONTENT_URI_HISTORY;

public abstract class HistoryActivity extends BaseActivity<ActivityHistoryBinding> {
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_history);

        setSupportActionBar(getBinding().toolbar);

        adapter = new HistoryAdapter(R.layout.history_item, new ArrayList<>());
        adapter.setHasStableIds(true);

        getBinding().recyclerView.setHasFixedSize(true);
        getBinding().recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getBinding().recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        getBinding().recyclerView.setAdapter(adapter);
        new HistoryViewModel(getApplication()).getCursorMutableLiveData().observe(this, cursor -> {
            ArrayList<HistoryEntity> models = new ArrayList<>();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        HistoryEntity model = new HistoryEntity();
                        model.setId(Integer.parseInt(cursor.getString(0)));
                        model.setName(cursor.getString(1));
                        model.setAddress(cursor.getString(2));
                        model.setLatitude(Double.parseDouble(cursor.getString(3)));
                        model.setLongitude(Double.parseDouble(cursor.getString(4)));
                        model.setDate(new Date(cursor.getLong(5)));
                        models.add(model);
                        if ("no address".equals(model.getAddress())) {
                            if ("no address".equals(model.getAddress())) {
                                new GeoCoderAsync(this, model.getLatitude() + "," + model.getLongitude(), (address, message) -> {
                                    if (address != null) {
                                        getContentResolver().update(
                                                CONTENT_URI_HISTORY,
                                                HistoryEntity.insertData(model.getName(), address.getAddressLine(0), address.getLatitude(), address.getLongitude()), "id=?", new String[]{String.valueOf(model.getId())}
                                        );
                                    }
                                }).execute();
                            }
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            adapter.setModels(models);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        if (adapter.getModels().size() == 0)
            menu.findItem(R.id.clear_all_history).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_all_history:
                new AlertDialog.Builder(this)
                        .setTitle("Clear All History")
                        .setMessage("Are you sure you want to clear all history ?")
                        .setCancelable(false)
                        .setPositiveButton("delete", (dialog, which) -> {
                            getContentResolver().delete(CONTENT_URI_HISTORY, null, null);
                            item.setVisible(false);
                        })
                        .setNegativeButton("cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create().show();
                return true;
            default:
                return false;
        }
    }

    @BindingAdapter("android:text_date_history")
    public static void setText(TextView view, Date date) {
        view.setText(new SimpleDateFormat("HH:mm:ss  EEE, dd MMM yyyy", Locale.getDefault()).format(date));
    }
}