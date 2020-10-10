package id.fake.gps.history;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import id.fake.gps.base.BaseAdapter;

public class HistoryAdapter extends BaseAdapter<HistoryEntity, HistoryViewHolder> {

    public HistoryAdapter(int listLayout, ArrayList<HistoryEntity> databaseEntities) {
        super(listLayout, databaseEntities);
    }

    @Override
    public void bind(HistoryViewHolder historyViewHolder, HistoryEntity databaseEntity, int position) {
        historyViewHolder.bind(databaseEntity);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.listLayout, parent, false));
    }
}
