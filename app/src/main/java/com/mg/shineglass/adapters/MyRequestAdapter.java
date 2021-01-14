package com.mg.shineglass.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.Interface.FetchData;
import com.mg.shineglass.MyRequestDetailsPopUpActivity;
import com.mg.shineglass.My_Address_Activity;
import com.mg.shineglass.Quotation_Activity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.MyQuotation;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MyRequestAdapter extends RecyclerView.Adapter<MyRequestAdapter.MyRequestItemHolder> {

    private final List<MyQuotation> list;
    private final Context context;
    private CompositeSubscription mSubscriptions;
    private ViewDialog viewDialog;
    private final Activity activity;
    private final FetchData fetchData;

    public MyRequestAdapter(List<MyQuotation> list, Context context, Activity activity, FetchData fetchData) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.fetchData = fetchData;

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


    if(quotation.getValuation() && !quotation.getAccept() && !quotation.getReject())
    {

        MyRequestItemHolder.reject.setVisibility(View.VISIBLE);
        MyRequestItemHolder.accept.setVisibility(View.VISIBLE);
        MyRequestItemHolder.line.setVisibility(View.VISIBLE);
        MyRequestItemHolder.view.setVisibility(View.VISIBLE);
        MyRequestItemHolder.imageView.setVisibility(View.GONE);
        MyRequestItemHolder.imageView2.setVisibility(View.GONE);
    }
    else if(quotation.getValuation() && quotation.getAccept() && !quotation.getReject())
    {
        MyRequestItemHolder.reject.setVisibility(View.GONE);
        MyRequestItemHolder.accept.setVisibility(View.GONE);
        MyRequestItemHolder.line.setVisibility(View.GONE);
        MyRequestItemHolder.view.setVisibility(View.VISIBLE);
        MyRequestItemHolder.imageView.setVisibility(View.VISIBLE);
        MyRequestItemHolder.imageView2.setVisibility(View.GONE);
    }
    else if(quotation.getValuation() && !quotation.getAccept() && quotation.getReject())
    {
        MyRequestItemHolder.reject.setVisibility(View.GONE);
        MyRequestItemHolder.accept.setVisibility(View.GONE);
        MyRequestItemHolder.line.setVisibility(View.GONE);
        MyRequestItemHolder.view.setVisibility(View.VISIBLE);
        MyRequestItemHolder.imageView.setVisibility(View.GONE);
        MyRequestItemHolder.imageView2.setVisibility(View.VISIBLE);
    }
    else  if(!quotation.getValuation() && !quotation.getAccept() && !quotation.getReject()){

        MyRequestItemHolder.reject.setVisibility(View.GONE);
        MyRequestItemHolder.accept.setVisibility(View.GONE);
        MyRequestItemHolder.line.setVisibility(View.GONE);
        MyRequestItemHolder.view.setVisibility(View.GONE);
        MyRequestItemHolder.imageView.setVisibility(View.GONE);
        MyRequestItemHolder.imageView2.setVisibility(View.GONE);
    }


    MyRequestItemHolder.view.setOnClickListener(v -> {
        Intent intent=new Intent(MyRequestItemHolder.itemView.getContext(), Quotation_Activity.class);
        intent.putExtra("quotation",quotation.getQuotationNo());
        intent.putExtra("url",quotation.getUrl());
        intent.putExtra("total",String.valueOf(quotation.getTotal()));
        intent.putExtra("date",simpleDateFormat.format(quotation.getDate()));
        intent.putExtra("accept",quotation.getAccept().toString());
        intent.putExtra("valuation",quotation.getReject().toString());
        intent.putExtra("reject",quotation.getReject().toString());
        MyRequestItemHolder.itemView.getContext().startActivity(intent);
    });


        MyRequestItemHolder.accept.setOnClickListener(v -> {
            Intent intent=new Intent(MyRequestItemHolder.itemView.getContext(), My_Address_Activity.class);
            intent.putExtra("quotation",quotation.getQuotationNo());
            intent.putExtra("total",String.valueOf(quotation.getTotal()));
            intent.putExtra("url",quotation.getUrl());
            intent.putExtra("date",simpleDateFormat.format(quotation.getDate()));
            MyRequestItemHolder.itemView.getContext().startActivity(intent);
        });

        MyRequestItemHolder.reject.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Are you sure??")
                    .setMessage("Do you want to reject??")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            REJECT_QUOTATION(quotation.getQuotationNo());
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        });

        MyRequestItemHolder.info_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MyRequestDetailsPopUpActivity.class));
            }
        });
    }

    private void REJECT_QUOTATION(String quotationNo) {


        viewDialog.showDialog();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        mSubscriptions.add(networkUtils.getRetrofit(sharedPreferences.getString("token", null))
                .REJECT_QUOTATION(quotationNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
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
        ImageView imageView, imageView2;
        ImageView info_img_btn;


        MyRequestItemHolder (View itemView) {
            super(itemView);
            quotationNo=itemView.findViewById(R.id.request_no);
            date=itemView.findViewById(R.id.date_value);
            reject=itemView.findViewById(R.id.reject_btn);
            view=itemView.findViewById(R.id.request_quotation_btn);
            line=itemView.findViewById(R.id.line);
            accept=itemView.findViewById(R.id.accept_btn);
            imageView=itemView.findViewById(R.id.accepted_img);
            imageView2=itemView.findViewById(R.id.rejected_img);
            mSubscriptions = new CompositeSubscription();
            viewDialog = new ViewDialog(activity);
            info_img_btn = itemView.findViewById(R.id.quotation_details_btn);

        }
    }



    private void handleResponse(BasicResponse response) {

        viewDialog.hideDialog();
        Toast.makeText(context, "Quotation Rejected! Refreshing", Toast.LENGTH_SHORT).show();
        fetchData.FETCH();
    }

    private void handleError(Throwable error) {

      viewDialog.hideDialog();
        Log.e("error",error.toString());
        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response<BasicResponse> response = gson.fromJson(errorBody,Response.class);
                assert response.body() != null;

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(context, "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }








}
