package com.mg.shineglass.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mg.shineglass.R;
import com.mg.shineglass.models.MyOrders;
import java.text.SimpleDateFormat;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersItemHolder>  {

    private List<MyOrders> list;

    public  OrdersAdapter(List<MyOrders> list) {
        this.list=list;

    }


    @NonNull
    @Override
    public  OrdersAdapter. OrdersItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_item, parent, false);
        return new  OrdersAdapter.OrdersItemHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.OrdersItemHolder  OrderItemHolder, int i) {
        MyOrders orders=list.get(i);

        OrderItemHolder.quotationNo.setText(orders.getOrderNo());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        OrderItemHolder.date.setText(simpleDateFormat.format(orders.getOrder_date()));
        OrderItemHolder.status.setText(orders.getStatus());

        OrderItemHolder.invoice.setVisibility(View.GONE);

//       if(orders.getInvoice_generated())
//       {OrderItemHolder.invoice.setVisibility(View.VISIBLE);
//
//           OrderItemHolder.invoice.setOnClickListener(v -> {
//               Intent intent=new Intent(OrderItemHolder.itemView.getContext(), Invoice_Activity.class);
//               intent.putExtra("invoice",orders.getInvoice());
//               OrderItemHolder.itemView.getContext().startActivity(intent);
//           });
//
//       }
//       else
//       {
//           OrderItemHolder.invoice.setVisibility(View.GONE);
//       }








    }

    @Override
    public int getItemCount() {
        if(list!=null)
        {
            return list.size();
        }
        return 0;

    }

    class  OrdersItemHolder extends RecyclerView.ViewHolder {

        TextView quotationNo,date,status;
        RelativeLayout invoice;


        OrdersItemHolder (View itemView) {
            super(itemView);
            quotationNo=itemView.findViewById(R.id.order_no__txt);
            date=itemView.findViewById(R.id.date_txt);
            status=itemView.findViewById(R.id.status_txt);
            invoice=itemView.findViewById(R.id.request_quotation_btn);



        }
    }












}

