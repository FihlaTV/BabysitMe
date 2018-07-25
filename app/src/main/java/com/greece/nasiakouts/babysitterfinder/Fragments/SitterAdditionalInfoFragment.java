package com.greece.nasiakouts.babysitterfinder.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.OnUploadPhotoEvent;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SitterAdditionalInfoFragment  extends RegisterComponentFragment{

    @BindViews({R.id.max_kids,
            R.id.min_age,
            R.id.introduction})
    List<TextInputEditText> mSitterAdditionalInfoList;

    @BindViews({R.id.max_kids_wrapper,
            R.id.min_age_wrapper,
            R.id.introduction_wrapper})
    List<TextInputLayout> mSitterAdditionalInfoWrapperList;

    @BindView(R.id.file_uploaded)
    TextView fileUploadedTv;

    @BindView(R.id.upload_photo_button)
    Button upload;
    // Define a new interface OnUploadPhotoEvent that triggers
    // a callback in the host activity
    OnUploadPhotoEvent uploadPhotoEventCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sitter_additonal_info, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    private boolean hasSelectedPhoto = false;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(TextView.class.getName(), hasSelectedPhoto);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.containsKey(TextView.class.getName())) {
            hasSelectedPhoto = savedInstanceState.getBoolean(TextView.class.getName());
            if (hasSelectedPhoto) {
                fileUploadedTv.setVisibility(View.VISIBLE);
            } else {
                fileUploadedTv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public User getUser(User user) {
        // region Get User Input
        String maxKids = mSitterAdditionalInfoList.get(Constants.INDEX_MAX_KIDS_INPUT)
                .getText().toString();
        String minAge = mSitterAdditionalInfoList.get(Constants.INDEX_MIN_AGE_INPUT)
                .getText().toString();
        String introduction = mSitterAdditionalInfoList.get(Constants.INDEX_INTRODUCTION_INPUT)
                .getText().toString();
        // endregion

        if (areValidAndFilled(maxKids, minAge)) {
            ((Babysitter) user).setMaxKids(Integer.parseInt(maxKids));
            ((Babysitter) user).setMinAge(Double.parseDouble(minAge));
            ((Babysitter) user).setIntroduction(introduction);
            return user;
        }

        return null;
    }

    private boolean areValidAndFilled(String maxKids, String minAge) {
        if (TextUtils.isEmpty(maxKids)) {
            mSitterAdditionalInfoWrapperList.get(Constants.INDEX_MAX_KIDS_INPUT)
                    .setError(getResources().getString(R.string.not_filled_max_kids));
            return false;
        }

        if (TextUtils.isEmpty(minAge)) {
            mSitterAdditionalInfoWrapperList.get(Constants.INDEX_MIN_AGE_INPUT)
                    .setError(getResources().getString(R.string.not_filled_min_age));
            return false;
        }

        return true;
    }

    public void setHasSelectedPhoto(boolean hasSelectedPhoto) {
        this.hasSelectedPhoto = hasSelectedPhoto;
        fileUploadedTv.setVisibility(View.VISIBLE);
    }

    @Override
    public int getPosition() {
        return Constants.SITTER_ADDITIONAL_INFO_FRAGMENT_SEQ;
    }


    // region Upload Photo
    /* Establish communication between this fragment and the wrapper activity
     * Handle click on upload photo button
     */

    @OnClick(R.id.upload_photo_button)
    public void uploadButtonPressed() {
        uploadPhotoEventCallback.showUploadPhotoDialog();
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            uploadPhotoEventCallback = (OnUploadPhotoEvent) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnUploadPhotoEvent");
        }
    }
    // endregion

}
