package com.greece.nasiakouts.babysitterfinder.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SittersResultRvAdapter extends RecyclerView.Adapter<SittersResultRvAdapter.SittersResultHolder> {
    private WeakReference<Activity> activityWeakReference;
    private ArrayList<Babysitter> availableSitters;

    public SittersResultRvAdapter(Activity activity, ArrayList<Babysitter> availableSitters) {
        if (availableSitters == null) this.availableSitters = new ArrayList<>();
        else this.availableSitters = availableSitters;

        this.activityWeakReference = new WeakReference<Activity>(activity);
    }

    public class SittersResultHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.photo)
        ImageView photo;

        @BindViews({R.id.name, R.id.price, R.id.currency})
        List<TextView> info;

        public SittersResultHolder(View view){
            super(view);
            ButterKnife.bind(this, view);

            if (activityWeakReference == null) return;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View dialogView = activityWeakReference.get()
                            .getLayoutInflater().inflate(R.layout.dialog_arrange_sitter, null);

                    Button cancel = dialogView.findViewById(R.id.cancel_button);
                    Button arrange = dialogView.findViewById(R.id.arrange_button);

                    final AlertDialog alertDialog =
                            new AlertDialog.Builder(activityWeakReference.get())
                                    .setView(dialogView)
                                    .setCancelable(false)
                                    .create();

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    arrange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO
                        }
                    });

                    alertDialog.show();
                }
            });
        }
    }

    @NonNull
    @Override
    public SittersResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sitters_result_item, parent, false);

        return new SittersResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SittersResultHolder holder, int position) {
        Babysitter sitter = availableSitters.get(position);
        // todo photo
        holder.info.get(Constants.INDEX_NAME).setText(sitter.getFullName());
        holder.info.get(Constants.INDEX_PRICE).setText(String.valueOf(sitter.getCharges()));
        holder.info.get(Constants.INDEX_CURRENCY).setText(sitter.getCurrency());
    }

    @Override
    public int getItemCount() {
        return availableSitters == null ? 0 : availableSitters.size();
    }

}
