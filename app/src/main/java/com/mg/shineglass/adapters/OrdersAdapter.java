package com.mg.shineglass.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.Interface.FetchData;
import com.mg.shineglass.Invoice_Activity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.MyOrders;
import com.mg.shineglass.models.PaymentDetails;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersItemHolder>  {

    private List<MyOrders> list;
    private Activity activity;
    private ViewDialog viewDialog;
    private SharedPreferences sharedPreferences;
    private CompositeSubscription mSubscriptions;
    private FetchData fetchData;

    public  OrdersAdapter(List<MyOrders> list,Activity activity,FetchData fetchData) {
        this.list=list;
        this.activity=activity;
        this.fetchData=fetchData;

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

       if(orders.getInvoice_generated())
       {OrderItemHolder.invoice.setVisibility(View.VISIBLE);

           OrderItemHolder.invoice.setOnClickListener(v -> {
               Intent intent=new Intent(OrderItemHolder.itemView.getContext(), Invoice_Activity.class);
               intent.putExtra("invoice",orders.getInvoice());
               OrderItemHolder.itemView.getContext().startActivity(intent);
           });

       }
       else
       {
           OrderItemHolder.invoice.setVisibility(View.GONE);
       }

       if(orders.getStatus().equals("Payment Initiated"))
       {
           OrderItemHolder.check_status.setVisibility(View.VISIBLE);
           OrderItemHolder.check_status.setOnClickListener(v -> {
               CHECK_STATUS(orders.getQuotationNo());
           });
       }

       else
       {
           OrderItemHolder.check_status.setVisibility(View.GONE);
       }








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
        RelativeLayout invoice,check_status;



        OrdersItemHolder (View itemView) {
            super(itemView);
            quotationNo=itemView.findViewById(R.id.order_no__txt);
            date=itemView.findViewById(R.id.date_txt);
            status=itemView.findViewById(R.id.status_txt);
            invoice=itemView.findViewById(R.id.request_quotation_btn);
            check_status=itemView.findViewById(R.id.check_payment_status_btn);

            sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(itemView.getContext());
            viewDialog = new ViewDialog(activity);
            mSubscriptions = new CompositeSubscription();


        }
    }





private void CHECK_STATUS(String Quotation)
{

    viewDialog.showDialog();
    mSubscriptions.add(networkUtils.getRetrofit(sharedPreferences.getString(constants.TOKEN, null))
            .PLACE_ONLINE_ORDER(constants.MID,Quotation)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(this::handleResponse,this::handleError));


}

    private void handleResponse(Integer integer) {
        viewDialog.hideDialog();
        fetchData.FETCH();
        Toast.makeText(activity, "Payment Status is checked! Refreshing", Toast.LENGTH_SHORT).show();
    }


    private void handleError(Throwable error) {
        viewDialog.hideDialog();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody,BasicResponse.class);
                Toast.makeText(activity, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }






}

