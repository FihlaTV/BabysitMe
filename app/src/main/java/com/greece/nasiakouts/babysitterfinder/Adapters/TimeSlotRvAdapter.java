package com.greece.nasiakouts.babysitterfinder.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeSlotRvAdapter extends RecyclerView.Adapter<TimeSlotRvAdapter.WorkingHoursHolder>{
        private ArrayList<TimeSlot> timeSlots;

    @Override
    public void onBindViewHolder(@NonNull WorkingHoursHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);
        holder.workingPeriod.setText(timeSlot.toString());
        if (timeSlot.isAllDay()) holder.allDayCheckBox.setVisibility(View.VISIBLE);
        else holder.allDayCheckBox.setVisibility(View.GONE);
        if (timeSlot.isForever()) holder.weeklyCheckBox.setVisibility(View.VISIBLE);
        else holder.weeklyCheckBox.setVisibility(View.GONE);
        }

        public TimeSlotRvAdapter(ArrayList<TimeSlot> timeSlots){
            if(timeSlots == null) this.timeSlots = new ArrayList<>();
            else this.timeSlots = timeSlots;
        }

        public void insertTimeSlot(TimeSlot timeSlot){
            if(timeSlot == null) return;

            if(timeSlots == null) timeSlots = new ArrayList<>();

            timeSlots.add(timeSlot);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public WorkingHoursHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.time_slot_item, parent, false);

            return new WorkingHoursHolder(itemView);
        }

    public class WorkingHoursHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.working_period_tv)
        TextView workingPeriod;

        @BindView(R.id.all_day_checkbox)
        CheckBox allDayCheckBox;

        @BindView(R.id.weekly_checkbox)
        CheckBox weeklyCheckBox;

        public WorkingHoursHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            }
        }

        @Override
        public int getItemCount() {
            return timeSlots == null ? 0 : timeSlots.size();
        }

        public ArrayList<TimeSlot> getData(){
            return timeSlots;
        }
    }