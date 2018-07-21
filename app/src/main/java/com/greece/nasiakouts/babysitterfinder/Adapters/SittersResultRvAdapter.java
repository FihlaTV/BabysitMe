package com.greece.nasiakouts.babysitterfinder.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.greece.nasiakouts.babysitterfinder.Activities.SittersResultActivity;
import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.R;
import com.makeramen.roundedimageview.RoundedImageView;

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

        this.activityWeakReference = new WeakReference<>(activity);
    }

    @NonNull
    @Override
    public SittersResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sitters_result, parent, false);

        return new SittersResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SittersResultHolder holder, int position) {
        final Babysitter sitter = availableSitters.get(position);
        // todo photo
        holder.info.get(Constants.INDEX_NAME).setText(sitter.getFullName());
        holder.info.get(Constants.INDEX_PRICE).setText(String.valueOf(sitter.getCharges()));
        holder.info.get(Constants.INDEX_CURRENCY).setText(sitter.getCurrency());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityWeakReference == null) return;

                final View dialogView = activityWeakReference.get()
                        .getLayoutInflater().inflate(R.layout.dialog_arrange_sitter, null);

                Button cancel = dialogView.findViewById(R.id.cancel_button);
                Button arrange = dialogView.findViewById(R.id.arrange_button);

                RoundedImageView photo = dialogView.findViewById(R.id.photo_profile);
                TextView name = dialogView.findViewById(R.id.full_name);
                TextView age = dialogView.findViewById(R.id.age);
                TextView phone = dialogView.findViewById(R.id.phone);
                TextView introduction = dialogView.findViewById(R.id.introduction);

                // todo foto

                name.setText(sitter.getFullName());
                age.setText(sitter.getAge() + "");
                phone.setText(sitter.getPhoneNumber());
                introduction.setText(sitter.getIntroduction());

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
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser == null) {
                            if (activityWeakReference == null) {
                                Toast.makeText(activityWeakReference.get()
                                        , activityWeakReference.get().getString(R.string.disconected), Toast.LENGTH_LONG).show();

                            }
                            alertDialog.dismiss();
                            return;
                        }

                        if (activityWeakReference == null) return;
                        ((SittersResultActivity) activityWeakReference
                                .get()).insertAppointmentToDb(firebaseUser.getUid(), sitter.getEmailAddress());
                    }
                });

                alertDialog.show();
            }
        });
    }

    public class SittersResultHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_item_sitter)
        CardView cardView;

        @BindView(R.id.photo)
        ImageView photo;

        @BindViews({R.id.name, R.id.price, R.id.currency})
        List<TextView> info;

        public SittersResultHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemCount() {
        return availableSitters == null ? 0 : availableSitters.size();
    }

    public void swapData(ArrayList<Babysitter> babysitters) {
        if (babysitters == null) return;
        availableSitters = babysitters;
        notifyDataSetChanged();
    }
}
