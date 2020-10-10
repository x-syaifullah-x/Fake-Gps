package id.fake.gps.history;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import id.fake.gps.databinding.HistoryItemBinding;

public class HistoryViewHolder extends RecyclerView.ViewHolder {

    private HistoryItemBinding binding;
    public static final int HISTORY_RESULT_CODE = 203;
    public static final String HISTORY_DATA_EXTRA = "HISTORY_DATA_EXTRA";


    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        this.binding = DataBindingUtil.bind(itemView);
    }

    void bind(HistoryEntity entity) {
        binding.setData(entity);
        this.binding.getRoot().setOnClickListener(v -> {
            ((AppCompatActivity) v.getContext()).setResult(Activity.RESULT_OK, new Intent().putExtra(HISTORY_DATA_EXTRA, entity));
            ((AppCompatActivity) v.getContext()).finish();
        });
    }
}
