package com.greece.nasiakouts.babysitterfinder.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.Adapters.TimeSlotRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Utils.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.Interfaces.OnAddTimeSlotListener;
import com.greece.nasiakouts.babysitterfinder.R;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkingInfoFragment  extends RegisterComponentFragment
        implements View.OnFocusChangeListener{

    @BindViews({R.id.street_address,
                R.id.charges,
                R.id.currency})
    List<TextInputEditText> mWorkingInfoList;

    @BindView(R.id.working_slots_rv)
    RecyclerView mWorkingSlotsRv;

    @BindView(R.id.addWorkingSlot)
    FloatingActionButton mAddWorkingSlotFab;

    private TimeSlotRvAdapter mAdapter;
    private CurrencyPicker mCurrencyPicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_working_info, container, false);

        ButterKnife.bind(this, root);

        mAdapter = new TimeSlotRvAdapter(null);
        mWorkingSlotsRv.setAdapter(mAdapter);
        mWorkingSlotsRv.addItemDecoration(new DividerItemDecoration(mWorkingSlotsRv.getContext(),
                DividerItemDecoration.VERTICAL));
        if(getContext() != null) mWorkingSlotsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        mWorkingInfoList.get(Constants.INDEX_CURRENCY_INPUT).setOnFocusChangeListener(this);

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable listState = mWorkingSlotsRv.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RecyclerView.class.getName(), listState);
        outState.putParcelableArrayList(TimeSlot.class.getName(), mAdapter.getData());
        if (mCurrencyPicker != null && mCurrencyPicker.isVisible()) {
            mCurrencyPicker.dismiss();
            outState.putBoolean(CurrencyPicker.class.getName(), true);
        } else {
            outState.putBoolean(CurrencyPicker.class.getName(), false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) return;
        if (savedInstanceState.containsKey(RecyclerView.class.getName())
                && savedInstanceState.containsKey(TimeSlot.class.getName())) {
            mAdapter.swapData(savedInstanceState
                    .<TimeSlot>getParcelableArrayList(TimeSlot.class.getName()));

            mWorkingSlotsRv.getLayoutManager().onRestoreInstanceState(
                    savedInstanceState.getParcelable(RecyclerView.class.getName()));
        }
        if (savedInstanceState.containsKey(CurrencyPicker.class.getName())) {
            if (savedInstanceState.getBoolean(CurrencyPicker.class.getName())) {
                openCurrencyPicker();
            }
        }
    }

    @Override
    public User getUser(User user) {
        if(user == null) return null;

        // region Get User Input
        String streetAddress = mWorkingInfoList.get(Constants.INDEX_STREET_ADDRESS_INPUT)
                .getText().toString();
        String charges = mWorkingInfoList.get(Constants.INDEX_CHARGES_INPUT)
                .getText().toString();
        String currency = mWorkingInfoList.get(Constants.INDEX_CURRENCY_INPUT)
                .getText().toString();
        // endregion

        if (areValidAndFilled(streetAddress, charges, currency)) {
            return new Babysitter(user, streetAddress,
                    Double.parseDouble(charges), currency, mAdapter.getData());
        }

        return null;
    }

    private boolean areValidAndFilled(String streetAddress,
                                      String charges,
                                      String currency) {
        if (TextUtils.isEmpty(streetAddress)) {
            mWorkingInfoList.get(Constants.INDEX_STREET_ADDRESS_INPUT)
                    .setError(getString(R.string.not_filled_street));
            return false;
        }

        if (TextUtils.isEmpty(charges)) {
            mWorkingInfoList.get(Constants.INDEX_CHARGES_INPUT)
                    .setError(getString(R.string.not_filled_charges));
            return false;
        }

        if (TextUtils.isEmpty(currency)) {
            mWorkingInfoList.get(Constants.INDEX_CURRENCY_INPUT)
                    .setError(getString(R.string.not_filled_currency));
            return false;
        }

        if (mAdapter.getItemCount() == 0) {
            if (getContext() == null) return false;
            Toast.makeText(getContext(),
                    R.string.no_time_slot_added,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public int getPosition() {
        return Constants.WORKING_INFO_FRAGMENT_SEQ;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            openCurrencyPicker();
        }
    }

    private void openCurrencyPicker() {
        if (getActivity() == null) return;
        if (mCurrencyPicker == null) {
            mCurrencyPicker = CurrencyPicker.
                    newInstance(getString(R.string.select_currency_prompt));
            mCurrencyPicker.setListener(new CurrencyPickerListener() {
                @Override
                public void onSelectCurrency(String name,
                                             String code,
                                             String dialCode,

                                             int flagDrawableResID) {
                    mWorkingInfoList.get(Constants.INDEX_CURRENCY_INPUT).setText(code);
                    mCurrencyPicker.dismiss();
                    mAddWorkingSlotFab.requestFocus();
                }
            });
        }

        mCurrencyPicker.show(getActivity().getSupportFragmentManager(),
                getString(R.string.currency_picker));
    }

    // region Add Time Slot
    /* Establish communication between this fragment and the wrapper activity
     * Handle click on fab add new time slot
     */

    // Define a new interface OnAddWorkingSlotListener that triggers
    // a callback in the host activity
    OnAddTimeSlotListener addTimeSlotCallback;

    @OnClick(R.id.addWorkingSlot)
    public void addWorkingSlot() {
        addTimeSlotCallback.addTimeSlot(mAdapter);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            addTimeSlotCallback = (OnAddTimeSlotListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnAddTimeSlotListener");
        }
    }
    // endregion
}
