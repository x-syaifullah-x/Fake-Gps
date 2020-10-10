package id.fake.gps.favorite;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import id.fake.gps.databinding.FavoriteItemBinding;
import id.fake.gps.utils.selection.Details;

public class FavoriteViewHolder extends RecyclerView.ViewHolder {

    private FavoriteItemBinding binding;
    private Details details;

    public FavoriteViewHolder(@NonNull View itemView) {
        super(itemView);
        this.binding = DataBindingUtil.bind(itemView);
        details = new Details();
    }

    void bind(FavoriteEntity entity, int position, SelectionTracker<Long> selectionTracker) {
        details.position = position;
        binding.getRoot().setActivated(selectionTracker.isSelected(details.position));
        binding.setData(entity);
    }

    public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
        return details;
    }
}
