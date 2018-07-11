package com.greece.nasiakouts.babysitterfinder.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SittersResultRvAdapter extends RecyclerView.Adapter<SittersResultRvAdapter.SittersResultHolder> {
    private ArrayList<Babysitter> availableSitters;

    public class SittersResultHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.photo)
        ImageView photo;

        @BindViews({R.id.name, R.id.price, R.id.currency})
        List<TextView> info;

        public SittersResultHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SittersResultRvAdapter(ArrayList<Babysitter> availableSitters){
        if(availableSitters == null) this.availableSitters = new ArrayList<>();
        else this.availableSitters = availableSitters;
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
