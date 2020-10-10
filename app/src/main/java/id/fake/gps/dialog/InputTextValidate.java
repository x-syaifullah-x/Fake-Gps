package id.fake.gps.dialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

import id.fake.gps.R;

public class InputTextValidate implements TextWatcher {
    private TextInputEditText textInputEditText;

    public InputTextValidate(TextInputEditText textInputEditText) {
        if (TextUtils.isEmpty(textInputEditText.getText())) {
            textInputEditText.getRootView().findViewById(R.id.btn_save).setEnabled(false);
        }
        this.textInputEditText = textInputEditText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            textInputEditText.setError("field not empty");
            textInputEditText.getRootView().findViewById(R.id.btn_save).setEnabled(false);
        } else {
            textInputEditText.getRootView().findViewById(R.id.btn_save).setEnabled(true);
        }
    }
}
