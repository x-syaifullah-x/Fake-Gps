package id.fake.gps.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

public abstract class BaseReceiver extends BroadcastReceiver {
    protected abstract void onReceive(ContextWrapper context, Intent intent);

    @Override
    public void onReceive(Context context, Intent intent) {
        onReceive((ContextWrapper) context, intent);
    }
}
