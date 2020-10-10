package id.fake.gps.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import id.fake.gps.R;
import id.fake.gps.base.BaseActivity;
import id.fake.gps.databinding.ActivitySearchBinding;
import id.fake.gps.utils.async.GeoCoderAsync;

public abstract class SearchActivity extends BaseActivity<ActivitySearchBinding> {
    public static final String TAG = SearchActivity.class.getName();
    public static final int SEARCH_RESULT_CODE = 202;
    public static final String EXTRA_DATA_LATITUDE = "EXTRA_DATA_LATITUDE";
    public static final String EXTRA_DATA_LONGITUDE = "EXTRA_DATA_LONGITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_search);
        getBinding().searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getBinding().pbSearch.setVisibility(View.VISIBLE);
                new GeoCoderAsync(getBaseContext(), query, (address, message) -> {
                    if (address != null) {
                        setResult(
                                Activity.RESULT_OK,
                                new Intent().putExtra(EXTRA_DATA_LATITUDE, address.getLatitude()).putExtra(EXTRA_DATA_LONGITUDE, address.getLongitude())
                        );
                        overridePendingTransition(0, 0);
                        finish();
                    } else {
                        Toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG).show();
                        getBinding().pbSearch.setVisibility(View.GONE);
                    }
                }).execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
