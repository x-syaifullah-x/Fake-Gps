package id.fake.gps.utils.selection;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import id.fake.gps.history.HistoryViewHolder;

public class ExampleLookup extends ItemDetailsLookup<Long> {
    private RecyclerView recyclerView;

    public ExampleLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof HistoryViewHolder) {
//                return ((HistoryViewHolder) viewHolder).getItemDetails();
            }
        }
        return null;
    }
}
