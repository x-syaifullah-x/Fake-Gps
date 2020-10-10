package id.fake.gps.favorite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.fake.gps.R;
import id.fake.gps.base.BaseActivity;
import id.fake.gps.databinding.ActivityFavoriteBinding;
import id.fake.gps.utils.async.GeoCoderAsync;
import id.fake.gps.utils.selection.Details;
import id.fake.gps.utils.selection.KeyProvider;
import id.fake.gps.utils.selection.Predicate;

import static id.fake.gps.db.local.DatabaseContract.CONTENT_URI_FAVORITE;

public abstract class FavoriteActivity extends BaseActivity<ActivityFavoriteBinding> {

    public static final String FAVORITE_DATA_EXTRA = "FAVORITE_DATA_EXTRA";
    public static final int FAVORITE_RESULT_CODE = 106;
    public static ActionMode actionMode;

    private SelectionTracker<Long> selectionTracker;
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_favorite);
        setSupportActionBar(getBinding().toolbar);

        adapter = new FavoriteAdapter(R.layout.favorite_item, new ArrayList<>());
        adapter.setHasStableIds(true);

        getBinding().recyclerView.setHasFixedSize(true);
        getBinding().recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getBinding().recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        getBinding().recyclerView.setAdapter(adapter);

        new FavoriteViewModel(getApplication()).getCursorMutableLiveData().observe(this, cursor -> {
            ArrayList<FavoriteEntity> models = new ArrayList<>();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        FavoriteEntity model = new FavoriteEntity();
                        model.setId(Integer.parseInt(cursor.getString(0)));
                        model.setName(cursor.getString(1));
                        model.setAddress(cursor.getString(2));
                        model.setLatitude(Double.parseDouble(cursor.getString(3)));
                        model.setLongitude(Double.parseDouble(cursor.getString(4)));
                        model.setDate(new Date(cursor.getLong(5)));
                        models.add(model);
                        if ("no address".equals(model.getAddress())) {
                            new GeoCoderAsync(this, model.getLatitude() + "," + model.getLongitude(), (address, message) -> {
                                if (address != null) {
                                    getContentResolver().update(
                                            CONTENT_URI_FAVORITE,
                                            FavoriteEntity.insertData(model.getName(), address.getAddressLine(0), address.getLatitude(), address.getLongitude()), "id=?", new String[]{String.valueOf(model.getId())}
                                    );
                                }
                            }).execute();
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            adapter.setModels(models);
        });

        selectionTracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                getBinding().recyclerView,
                new KeyProvider(),
                new FavoriteLookup(getBinding().recyclerView),
                StorageStrategy.createLongStorage()
        ).withOnItemActivatedListener((item, e) -> {
            setResult(Activity.RESULT_OK, new Intent().putExtra(FAVORITE_DATA_EXTRA, adapter.getModels().get(item.getPosition())));
            finish();
            return true;
        }).withOnDragInitiatedListener(e -> {
            Log.d("TAG", "onDragInitiated");
            return true;
        }).withSelectionPredicate(new Predicate()).build();
        adapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = startSupportActionMode(new FavoriteSelectionActionMode(adapter.getModels(), FavoriteActivity.this, selectionTracker));
                } else if (selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.setTitle("Select " + selectionTracker.getSelection().size());
                } else if (selectionTracker.getSelection().size() == 0 && actionMode != null) {
                    actionMode.setTitle("No Selected");
                    Details.isLongClick = false;
                }

                if (actionMode != null) {
                    actionMode.getMenu().findItem(R.id.edit_data_selection).setVisible(selectionTracker.getSelection().size() == 1);
                }
            }
        });
    }

    @BindingAdapter("android:text_date_favorite")
    public static void setText(TextView view, Date date) {
        view.setText(new SimpleDateFormat("HH:mm:ss  EEE, dd MMM yyyy", Locale.getDefault()).format(date));
    }
}
