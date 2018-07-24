package com.greece.nasiakouts.babysitterfinder.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

public class AccountInfoFragment extends RegisterComponentFragment {

    @BindViews({R.id.mail_input,
            R.id.password_input,
            R.id.confirm_password_input})
    List<TextInputEditText> mAccountInfoList;

    @BindViews({R.id.main_input_wrapper,
            R.id.password_input_wrapper,
            R.id.confirm_password_input_wrapper})
    List<TextInputLayout> mAccountInfoWrapperList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account_info, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public int getPosition() {
        return Constants.ACCOUNT_INFO_FRAGMENT_SEQ;
    }

    @Override
    public User getUser(User user) {
        // region Get User Inputs
        String emailAddress = mAccountInfoList.get(Constants.INDEX_MAIL_INPUT)
                .getText().toString();
        String password = mAccountInfoList.get(Constants.INDEX_PASSWORD_INPUT)
                .getText().toString();
        String confirmedPassword = mAccountInfoList.get(Constants.INDEX_CONFIRM_PASSWORD_INPUT)
                .getText().toString();
        // endregion

        if (areValidAndFilled(emailAddress, password, confirmedPassword)) {
            return new User(emailAddress, password);
        }

        return null;
    }

    private boolean areValidAndFilled(String emailAddress,
                                      String password,
                                      String confirmedPassword) {
        if (TextUtils.isEmpty(emailAddress)) {
            mAccountInfoWrapperList.get(Constants.INDEX_MAIL_INPUT)
                    .setError(getString(R.string.not_filled_email_address));
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mAccountInfoWrapperList.get(Constants.INDEX_MAIL_INPUT)
                    .setError(getString(R.string.no_valid_email));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            mAccountInfoWrapperList.get(Constants.INDEX_PASSWORD_INPUT)
                    .setError(getString(R.string.not_filled_password));
            return false;
        }

        if (password.length() < 6) {
            mAccountInfoWrapperList.get(Constants.INDEX_PASSWORD_INPUT)
                    .setError(getString(R.string.no_valid_password));
            return false;
        }

        if (TextUtils.isEmpty(confirmedPassword)) {
            mAccountInfoWrapperList.get(Constants.INDEX_CONFIRM_PASSWORD_INPUT)
                    .setError(getString(R.string.not_filled_confirmation));
            return false;
        }

        if (!password.equals(confirmedPassword)) {
            mAccountInfoWrapperList.get(Constants.INDEX_CONFIRM_PASSWORD_INPUT)
                    .setError(getString(R.string.no_valid_confirmation));
            return false;
        }

        return true;
    }

}
