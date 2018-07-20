package com.greece.nasiakouts.babysitterfinder.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Appointment;
import com.greece.nasiakouts.babysitterfinder.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.AppointmentsHolder> {
    private WeakReference<Activity> activityWeakReference;
    private ArrayList<Appointment> appointments;
    private int mode;

    public AppointmentListAdapter(Activity activity, ArrayList<Appointment> appointments, int mode) {
        if (appointments == null) this.appointments = new ArrayList<>();
        else this.appointments = appointments;
        this.activityWeakReference = new WeakReference<>(activity);
        this.mode = mode;
    }

    @NonNull
    @Override
    public AppointmentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sitters_result, parent, false);

        return new AppointmentsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        // todo photo
        holder.info.get(Constants.INDEX_PERIOD).setText(appointment.getSlot().toString());
        holder.info.get(Constants.INDEX_ADDRESS).setText(appointment.getStreetAddress());
        String otherEndInfo = "";
        if (mode == Constants.SITTER_MODE) {
            // get customer object and get name and phone
        } else {
            // todo get sitter object and get name and phone
        }
        holder.info.get(Constants.INDEX_INFO).setText(otherEndInfo);
    }

    @Override
    public int getItemCount() {
        return appointments == null ? 0 : appointments.size();
    }

    public class AppointmentsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.accept_appointment)
        ImageView accept;

        @BindView(R.id.delete_appointment)
        ImageView delete;

        @BindViews({R.id.appointment_period,
                R.id.appointment_address,
                R.id.appointment_other_end_info})
        List<TextView> info;

        public AppointmentsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            if (activityWeakReference == null) return;

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
