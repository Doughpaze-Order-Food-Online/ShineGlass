package com.mg.shineglass.adapters;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mg.shineglass.DeliveryOrderDetails;
import com.mg.shineglass.Invoice_Activity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.MyOrders;

import java.text.SimpleDateFormat;
import java.util.List;

public class DeliveryOrderAdapter extends RecyclerView.Adapter<DeliveryOrderAdapter.DeliveryOrdersItemHolder> {

    private List<MyOrders> list;


    public DeliveryOrderAdapter(List<MyOrders> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public DeliveryOrderAdapter.DeliveryOrdersItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_item, parent, false);
        return new DeliveryOrderAdapter.DeliveryOrdersItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryOrderAdapter.DeliveryOrdersItemHolder deliveryOrdersItemHolder, int i) {
        MyOrders orders = list.get(i);


        deliveryOrdersItemHolder.text.setText("View Details");
        deliveryOrdersItemHolder.quotationNo.setText(orders.getOrderNo());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        deliveryOrdersItemHolder.date.setText(simpleDateFormat.format(orders.getOrder_date()));
        deliveryOrdersItemHolder.status.setText(orders.getStatus());


          if(!orders.getDelivery_status())
          {
              deliveryOrdersItemHolder.invoice.setVisibility(View.VISIBLE);
              deliveryOrdersItemHolder.invoice.setOnClickListener(v -> {
                  Intent intent=new Intent(deliveryOrdersItemHolder.itemView.getContext(), DeliveryOrderDetails.class);
                  Gson gson=new Gson();
                  intent.putExtra("order",gson.toJson(orders));
                  deliveryOrdersItemHolder.itemView.getContext().startActivity(intent);
              });
          }
          else
          {
              deliveryOrdersItemHolder.invoice.setVisibility(View.GONE);
          }




    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;

    }

    class DeliveryOrdersItemHolder extends RecyclerView.ViewHolder {

        TextView quotationNo,date,status,text;
        RelativeLayout invoice;



        DeliveryOrdersItemHolder(View itemView) {
            super(itemView);
            quotationNo=itemView.findViewById(R.id.order_no__txt);
            date=itemView.findViewById(R.id.date_txt);
            status=itemView.findViewById(R.id.status_txt);
            invoice=itemView.findViewById(R.id.request_quotation_btn);
            text=itemView.findViewById(R.id.request_quotation_txt);


        }
    }
}

