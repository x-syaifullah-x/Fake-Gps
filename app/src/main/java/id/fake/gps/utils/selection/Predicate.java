package id.fake.gps.utils.selection;

import androidx.recyclerview.selection.SelectionTracker.SelectionPredicate;

import org.jetbrains.annotations.NotNull;

public class Predicate extends SelectionPredicate<Long> {
    public boolean canSetStateForKey(@NotNull Long key, boolean nextState) {
        return true;
    }

    public boolean canSetStateAtPosition(int position, boolean nextState) {
        return true;
    }

    public boolean canSelectMultiple() {
        return true;
    }
}
