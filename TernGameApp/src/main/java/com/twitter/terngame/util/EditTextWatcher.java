package com.twitter.terngame.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by jchong on 1/12/14.
 */
public class EditTextWatcher implements TextWatcher {

    private EditText mEditText;
    private EditTextWatcher mSisterWatcher;
    private boolean mContainsText;
    private Button mButton;

    public EditTextWatcher(EditText toWatch, Button button)
    {
        mEditText = toWatch;
        mSisterWatcher = null;
        mContainsText = false;
        mButton = button;
    }

    public void setSisterWatcher(EditTextWatcher sister) {
        mSisterWatcher = sister;
    }

    @Override
    public void afterTextChanged(Editable text) {
        mContainsText = text.length() > 0;

        if (mContainsText && mSisterWatcher != null && mSisterWatcher.mContainsText) {
            mButton.setEnabled(true);
        } else {
            mButton.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

}
