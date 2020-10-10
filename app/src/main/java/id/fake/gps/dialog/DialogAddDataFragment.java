package id.fake.gps.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.databinding.DataBindingUtil;

import id.fake.gps.R;
import id.fake.gps.databinding.DialogAddBinding;
import id.fake.gps.db.local.DatabaseContract;
import id.fake.gps.favorite.FavoriteEntity;
import id.fake.gps.utils.async.GeoCoderAsync;

public class DialogAddDataFragment extends AppCompatDialogFragment {
    protected DialogAddBinding binding;
    Dialog dialog;
    private String values;
    private double latitude;
    private double longitude;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NO_TITLE, R.style.Dialog_Add_Data);
        return (dialog = super.onCreateDialog(savedInstanceState));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.bind(inflater.inflate(R.layout.dialog_add, container, false));
        if (binding != null) {
            if (binding.inputName.requestFocus() && dialog.getWindow() != null) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
            binding.inputName.addTextChangedListener(new InputTextValidate(binding.inputName));

            return binding.getRoot();
        }
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null)
            values = (latitude = getArguments().getDouble("latitude")) + "," + (longitude = getArguments().getDouble("longitude"));

        new GeoCoderAsync(view.getContext(), values, (address, message) -> {
            binding.pbLoading.setVisibility(View.GONE);
            if (address != null) {
                binding.setEntity(new FavoriteEntity(null, address.getAddressLine(0), address.getLatitude(), address.getLongitude()));
            } else {
                binding.setEntity(new FavoriteEntity(null, "no address", latitude, longitude));
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        }).execute();

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        binding.btnSave.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), isInsertData(v.getContext()) ? "success add" : "filed add", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });
    }

    private boolean isInsertData(Context context) {
        Uri uri = context.getContentResolver().insert(DatabaseContract.CONTENT_URI_FAVORITE, FavoriteEntity.insertData(
                String.valueOf(binding.inputName.getText()),
                String.valueOf(binding.inputAddress.getText()),
                Double.parseDouble(String.valueOf(binding.inputLatitude.getText())),
                Double.parseDouble(String.valueOf(binding.inputLongitude.getText()))
        ));
        return uri != null && !"0".equals(uri.getLastPathSegment());
    }
}
