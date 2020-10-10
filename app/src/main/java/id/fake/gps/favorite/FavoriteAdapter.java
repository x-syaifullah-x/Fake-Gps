package id.fake.gps.favorite;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;

import java.util.ArrayList;

import id.fake.gps.base.BaseAdapter;
import lombok.Getter;
import lombok.Setter;

public class FavoriteAdapter extends BaseAdapter<FavoriteEntity, FavoriteViewHolder> {

    @Getter
    @Setter
    private SelectionTracker<Long> selectionTracker;

    public FavoriteAdapter(int listLayout, ArrayList<FavoriteEntity> databaseEntities) {
        super(listLayout, databaseEntities);
    }

    @Override
    public void bind(FavoriteViewHolder favoriteViewHolder, FavoriteEntity favoriteEntity, int position) {
        favoriteViewHolder.bind(favoriteEntity, position, getSelectionTracker());
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.listLayout, parent, false));
    }
}
