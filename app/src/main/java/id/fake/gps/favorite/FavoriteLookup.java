package id.fake.gps.favorite;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteLookup extends ItemDetailsLookup<Long> {
    private RecyclerView recyclerView;

    public FavoriteLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof FavoriteViewHolder) {
                return ((FavoriteViewHolder) viewHolder).getItemDetails();
            }
        }
        return null;
    }
}
