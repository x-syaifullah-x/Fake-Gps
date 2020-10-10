package id.fake.gps.utils.selection;

import androidx.recyclerview.selection.ItemKeyProvider;

public class KeyProvider extends ItemKeyProvider<Long> {


    public KeyProvider() {
        super(SCOPE_MAPPED);
    }

    public Long getKey(int position) {
        return (long) position;
    }

    public int getPosition(Long key) {
        return (int) key.longValue();
    }
}
