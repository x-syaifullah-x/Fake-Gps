package id.fake.gps.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import id.fake.gps.db.local.DatabaseContract;
import id.fake.gps.favorite.FavoriteEntity;
import id.fake.gps.utils.async.GeoCoderAsync;

import static java.lang.String.valueOf;

public class DialogEditDataFragment extends DialogAddDataFragment {

    private FavoriteEntity values;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null)
            values = getArguments().getParcelable("data");
        if (binding != null) {
            binding.inputName.setEnabled(true);
            binding.inputLatitude.setEnabled(true);
            binding.inputLongitude.setEnabled(true);
            binding.pbLoading.setVisibility(View.GONE);
            binding.setEntity(values);
            binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
            binding.btnSave.setOnClickListener(v -> {
                if (!valueOf(binding.getEntity().getLatitude()).equals(valueOf(binding.inputLatitude.getText())) | !valueOf(binding.getEntity().getLongitude()).equals(valueOf(binding.inputLongitude.getText()))) {
                    new GeoCoderAsync(getContext(), binding.inputLatitude.getText() + "," + binding.inputLongitude.getText(), (address, message) -> {
                        if (address != null) {
                            Toast.makeText(v.getContext(), isUpdateData(v.getContext(), address.getAddressLine(0)) ? "success edit" : "filed edit", Toast.LENGTH_LONG).show();
                        }
                    }).execute();
                } else {
                    Toast.makeText(v.getContext(), isUpdateData(v.getContext(), values.getAddress()) ? "success edit" : "filed edit", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            });
        }
    }

    private boolean isUpdateData(Context context, String address) {
        return 0 != context.getContentResolver().update(
                DatabaseContract.CONTENT_URI_FAVORITE,
                FavoriteEntity.insertData(
                        valueOf(binding.inputName.getText()),
                        address,
                        Double.parseDouble(valueOf(binding.inputLatitude.getText())),
                        Double.parseDouble(valueOf(binding.inputLongitude.getText()))),
                "id = ?",
                new String[]{valueOf(values.getId())}
        );
    }
}