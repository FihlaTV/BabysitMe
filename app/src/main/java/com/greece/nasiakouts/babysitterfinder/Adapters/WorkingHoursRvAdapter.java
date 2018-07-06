package com.greece.nasiakouts.babysitterfinder.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkingHoursRvAdapter extends RecyclerView.Adapter<WorkingHoursRvAdapter.WorkingHoursHolder>{
        private ArrayList<TimeSlot> timeSlots;

        public class WorkingHoursHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.working_item_day)
            TextView day;

            @BindView(R.id.working_item_hours)
            TextView hours;

            @BindView(R.id.all_day_checkbox)
            CheckBox allDayCheckBox;

            public WorkingHoursHolder(View view){
                super(view);
                ButterKnife.bind(this, view);
            }
        }

        public WorkingHoursRvAdapter(ArrayList<TimeSlot> timeSlots){
            if(timeSlots == null) this.timeSlots = new ArrayList<>();
            else this.timeSlots = timeSlots;
        }

        public void insertTimeslot(TimeSlot timeSlot){
            if(timeSlot == null) return;

            if(timeSlots == null) timeSlots = new ArrayList<>();

            timeSlots.add(timeSlot);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public WorkingHoursHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.working_hours_item, parent, false);

            return new WorkingHoursHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull WorkingHoursHolder holder, int position) {
            TimeSlot timeSlot = timeSlots.get(position);
            holder.day.setText(timeSlot.getDay());
            if(timeSlot.isAllDay()) {
                holder.allDayCheckBox.setVisibility(View.VISIBLE);
                holder.hours.setVisibility(View.GONE);
            }
            else {
                holder.allDayCheckBox.setVisibility(View.GONE);
                holder.hours.setVisibility(View.VISIBLE);
                holder.hours.setText(timeSlot.getHours());
            }
        }

        @Override
        public int getItemCount() {
            return timeSlots == null ? 0 : timeSlots.size();
        }
    }