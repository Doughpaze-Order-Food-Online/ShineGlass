package com.mg.shineglass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mg.shineglass.R;
import com.mg.shineglass.models.Quotation;
import com.mg.shineglass.models.Rates;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoItemHolder>  {

    private List<Quotation> list;

    public InfoAdapter(List<Quotation> list) {
        this.list=list;
    }


    @NonNull
    @Override
    public InfoAdapter.InfoItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotation_details_item, parent, false);
        return new InfoAdapter.InfoItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoAdapter.InfoItemHolder RatesItemHolder, int i) {
        Quotation quotation=list.get(i);

        RatesItemHolder.category.setText(quotation.getCategory());
        RatesItemHolder.sub.setText(quotation.getSubcategory());
        RatesItemHolder.height.setText(quotation.getHeight());
        RatesItemHolder.quantity.setText(quotation.getQuantity());
        RatesItemHolder.scale.setText(quotation.getScale());
        RatesItemHolder.thickness.setText(quotation.getThickness());
        RatesItemHolder.width.setText(quotation.getWidth());

    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    class InfoItemHolder extends RecyclerView.ViewHolder {

        TextView category,sub,thickness,height,width,scale,quantity;

        InfoItemHolder (View itemView) {
            super(itemView);

            category=itemView.findViewById(R.id.category);
            sub=itemView.findViewById(R.id.sub_cat);
            thickness=itemView.findViewById(R.id.thickness);
            height=itemView.findViewById(R.id.height);
            quantity=itemView.findViewById(R.id.quantity);
            width=itemView.findViewById(R.id.width);
            scale=itemView.findViewById(R.id.scale);



        }
    }












}
