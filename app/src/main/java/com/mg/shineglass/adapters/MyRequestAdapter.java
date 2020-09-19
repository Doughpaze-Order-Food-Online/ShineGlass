package com.mg.shineglass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mg.shineglass.Interface.DeleteCartItem;
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


        MyRequestItemHolder (View itemView) {
            super(itemView);
            quotationNo=itemView.findViewById(R.id.request_no);
            date=itemView.findViewById(R.id.date_value);


        }
    }












}
