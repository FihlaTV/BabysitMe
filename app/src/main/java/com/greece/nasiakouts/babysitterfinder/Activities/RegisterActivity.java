package com.greece.nasiakouts.babysitterfinder.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Controls.NoScrollViewPager;
import com.greece.nasiakouts.babysitterfinder.Fragments.AccountInfoFragment;
import com.greece.nasiakouts.babysitterfinder.Fragments.RegisterComponentFragment;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity{

    @BindView(R.id.register_view_pager)
    public NoScrollViewPager mViewPager;

    @BindView(R.id.common_screen_previous)
    public Button mPreviousButton;

    @BindView(R.id.common_screen_next)
    public Button mNextButton;

    private User mUser;
    private int selectedMode;
    private RegisterAdapter mAdapter;
    private RegisterComponentFragment mCurrentFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_activity);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(Constants.INT_CODE)){
            selectedMode = bundle.getInt(Constants.INT_CODE);
        }

        mAdapter = new RegisterAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
    }

    private void updateButtonsVisibility() {
        int maxNumber = mAdapter.getCount();
        int position = mCurrentFragment.getPosition();

        if(position == 0) {
            mNextButton.setVisibility(View.VISIBLE);
            mPreviousButton.setVisibility(View.INVISIBLE);
        }
        else {
            if(position == maxNumber - 1) mNextButton.setText(R.string.ok);
            else mNextButton.setText(R.string.next);

            mNextButton.setVisibility(View.VISIBLE);
            mPreviousButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.common_screen_next)
    public void buttonNextPressed(View view){
        mUser = mCurrentFragment.getUser();

        if(mCurrentFragment.getPosition() == 3)
        {

        }

        mViewPager.setCurrentItem(mCurrentFragment.getPosition() + 1, true);
        updateButtonsVisibility();
    }

    private class RegisterAdapter extends FragmentPagerAdapter{
        RegisterAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (selectedMode){
                case R.id.radio_babysitter:
                    switch (position){
                        case 0:
                            mCurrentFragment = new AccountInfoFragment();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }

            updateButtonsVisibility();

            Bundle args = new Bundle();
            args.putSerializable(User.class.getName(), mUser);
            mCurrentFragment.setArguments(args);
            return mCurrentFragment;
        }

        @Override
        public int getCount() {
            switch (selectedMode){
                case R.id.radio_babysitter:
                    return 1;
                default:
                    return 0;
            }
        }
    }
}
