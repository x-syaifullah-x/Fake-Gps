package id.fake.gps.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment<FragmentBinding extends ViewDataBinding> extends Fragment {
    private FragmentBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBinding(view);
    }

    protected FragmentBinding getBinding() {
        return binding;
    }

    protected void setBinding(View view) {
        this.binding = DataBindingUtil.bind(view);
    }
}