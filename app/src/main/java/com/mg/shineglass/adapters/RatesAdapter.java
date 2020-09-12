package com.mg.shineglass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mg.shineglass.R;
import com.mg.shineglass.models.Rates;

import java.util.List;

public class RatesAdapter extends RecyclerView.Adapter<RatesAdapter.RatesItemHolder>  {

    private List<Rates> list;

    public RatesAdapter(List<Rates> list) {
        this.list=list;
    }


    @NonNull
    @Override
    public RatesAdapter.RatesItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_item, parent, false);
        return new RatesAdapter.RatesItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatesAdapter.RatesItemHolder RatesItemHolder, int i) {
        Rates Rate=list.get(i);









    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    class RatesItemHolder extends RecyclerView.ViewHolder {

        TextView rate;
        TextView price;

        RatesItemHolder (View itemView) {
            super(itemView);




        }
    }












}
