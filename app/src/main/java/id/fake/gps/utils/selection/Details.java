package id.fake.gps.utils.selection;

import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;

import org.jetbrains.annotations.NotNull;

public class Details extends ItemDetailsLookup.ItemDetails<Long> {
    public long position;
    public static boolean isLongClick = true;

    public int getPosition() {
        return (int) this.position;
    }

    public Long getSelectionKey() {
        return this.position;
    }

    public boolean inSelectionHotspot(@NotNull MotionEvent e) {
        return !isLongClick;//don't consider taps as selections => Similar to google photos.
        // if true then consider click as selection
    }

    @Override
    public boolean inDragRegion(@NonNull MotionEvent e) {
        return true;
    }
}
