package com.mg.shineglass.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mg.shineglass.Interface.DeleteCartItem;
import com.mg.shineglass.Quotation_Activity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.MyQuotation;
import com.mg.shineglass.models.Quotation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyRequestAdapter extends RecyclerView.Adapter< MyRequestAdapter. MyRequestItemHolder>  {

    private List<MyQuotation> list;

    public  MyRequestAdapter(List<MyQuotation> list) {
        this.list=list;

    }


    @NonNull
    @Override
    public  MyRequestAdapter. MyRequestItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_request_item, parent, false);
        return new  MyRequestAdapter.MyRequestItemHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRequestAdapter.MyRequestItemHolder  MyRequestItemHolder, int i) {
    MyQuotation quotation=list.get(i);

    MyRequestItemHolder.quotationNo.setText(quotation.getQuotationNo());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
    MyRequestItemHolder.date.setText(simpleDateFormat.format(quotation.getDate()));

    if(quotation.getValuation())
    {
        MyRequestItemHolder.reject.setVisibility(View.VISIBLE);
        MyRequestItemHolder.accept.setVisibility(View.VISIBLE);
        MyRequestItemHolder.line.setVisibility(View.VISIBLE);
        MyRequestItemHolder.view.setVisibility(View.VISIBLE);
    }
    else
    {
        MyRequestItemHolder.reject.setVisibility(View.GONE);
        MyRequestItemHolder.accept.setVisibility(View.GONE);
        MyRequestItemHolder.line.setVisibility(View.GONE);
        MyRequestItemHolder.view.setVisibility(View.GONE);
    }


    MyRequestItemHolder.view.setOnClickListener(v -> {
        Intent intent=new Intent(MyRequestItemHolder.itemView.getContext(), Quotation_Activity.class);
        intent.putExtra("quotationNo",quotation.getQuotationNo());
        intent.putExtra("url",quotation.getUrl());
        MyRequestItemHolder.itemView.getContext().startActivity(intent);
    });


        MyRequestItemHolder.accept.setOnClickListener(v -> {
            Toast.makeText(MyRequestItemHolder.itemView.getContext(), "Accepted", Toast.LENGTH_SHORT).show();
        });

        MyRequestItemHolder.reject.setOnClickListener(v -> {
            Toast.makeText(MyRequestItemHolder.itemView.getContext(), "Rejected", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null)
        {
            return list.size();
        }
        return 0;

    }

    class  MyRequestItemHolder extends RecyclerView.ViewHolder {

        TextView quotationNo,date;
        RelativeLayout view,reject,accept;
        View line;


        MyRequestItemHolder (View itemView) {
            super(itemView);
            quotationNo=itemView.findViewById(R.id.request_no);
            date=itemView.findViewById(R.id.date_value);
            view=itemView.findViewById(R.id.request_quotation_btn);
            line=itemView.findViewById(R.id.line);
            accept=itemView.findViewById(R.id.accept_btn);
            reject=itemView.findViewById(R.id.reject_btn);


        }
    }












}
