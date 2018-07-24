package com.greece.nasiakouts.babysitterfinder.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    @BindView(R.id.file_uploaded)
    TextView fileUploadedTv;

    @BindView(R.id.upload_photo_button)
    Button upload;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sitter_additonal_info, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public User getUser(User user) {
        String maxKids = mSitterAdditionalInfoList.get(Constants.INDEX_MAX_KIDS_INPUT)
                .getText().toString();
        String minAge = mSitterAdditionalInfoList.get(Constants.INDEX_MIN_AGE_INPUT)
                .getText().toString();
        String introduction = mSitterAdditionalInfoList.get(Constants.INDEX_INTRODUCTION_INPUT)
                .getText().toString();

        if(TextUtils.isEmpty(maxKids)) {
            mSitterAdditionalInfoList.get(Constants.INDEX_MAX_KIDS_INPUT)
                    .setError(getResources().getString(R.string.not_filled_max_kids));
            return null;
        }

        if(TextUtils.isEmpty(minAge)) {
            mSitterAdditionalInfoList.get(Constants.INDEX_MIN_AGE_INPUT)
                    .setError(getResources().getString(R.string.not_filled_min_age));
            return null;
        }

        ((Babysitter)user).setMaxKids(Integer.parseInt(maxKids));
        ((Babysitter)user).setMinAge(Double.parseDouble(minAge));
        ((Babysitter)user).setIntroduction(introduction);
        return user;
    }


    /* Establish communication between this fragment and the wrapper activity
     * Handle click on fab add new timeslot
     */

    // Define a new interface OnAddWorkingSlotListener that triggers
    // a callback in the host activity
    OnUploadPhotoEvent uploadPhotoEventCallback;

    @OnClick(R.id.upload_photo_button)
    public void uploadButonPressed(){
        uploadPhotoEventCallback.showUploadPhotoDialog(fileUploadedTv);
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
                    + " must implement OnAddTimeSlotListener");
        }
    }

    @Override
    public int getPosition() {
        return Constants.SITTER_ADDITIONAL_INFO_FRAGMENT_SEQ;
    }
}
