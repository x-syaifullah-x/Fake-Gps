package id.fake.gps.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public abstract class BaseActivity<ActivityBinding extends ViewDataBinding> extends AppCompatActivity {

    public interface ILocalReceiver {
        void onLocalReceiver(Intent intent);
    }

    protected ActivityBinding binding;
    protected ILocalReceiver iLocalReceiver;
    private boolean isLocalReceiver = false;

    protected final BaseReceiver localReceiver = new BaseReceiver() {
        @Override
        protected void onReceive(ContextWrapper context, Intent intent) {
            iLocalReceiver.onLocalReceiver(intent);
        }
    };

    protected void setLocalReceiver(String action) {
        if (this instanceof BaseActivity.ILocalReceiver) {
            iLocalReceiver = (ILocalReceiver) this;
            LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, new IntentFilter(action));
            isLocalReceiver = true;
        } else
            throw new UnsupportedOperationException("Not yet implemented" + ILocalReceiver.class.getName());
    }

    protected ActivityBinding getBinding() {
        return binding;
    }

    protected void setBinding(int LayoutActivity) {
        binding = DataBindingUtil.setContentView(this, LayoutActivity);
    }

    protected boolean isServiceRunning(String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isLocalReceiver) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
        }
    }
}
