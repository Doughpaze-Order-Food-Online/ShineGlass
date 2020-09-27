package com.mg.shineglass.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.Order_Confirmation_Activity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.Address;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.constants;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class Address_Adapter extends RecyclerView.Adapter<Address_Adapter.AddressItemHolder>  {

    private CompositeSubscription mSubscriptions;
    private List<Address> list;
    private SharedPreferences mSharedPreferences;
    Context context;
    private int pos=-1;
    private String Quotation, url, total, date;
    private Boolean flag;


    public Address_Adapter(List<Address> list,String Quotation,String url,String total,String date,Boolean flag) {
        this.list=list;
        this.Quotation=Quotation;
        this.url=url;
        this.total=total;
        this.date=date;
        this.flag=flag;
    }


    @NonNull
    @Override
    public AddressItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_address_item, parent, false);
        return new AddressItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressItemHolder addressItemHolder, int i) {
        Address newaddress=list.get(i);

        addressItemHolder.address.setText(newaddress.getAddress());
        addressItemHolder.no.setText(String.valueOf(i+1));

       if(flag)
       {
           addressItemHolder.parent.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {



                   Intent intent=new Intent(addressItemHolder.itemView.getContext(), Order_Confirmation_Activity.class);
                   intent.putExtra("quotation",Quotation);
                   intent.putExtra("total",total);
                   intent.putExtra("url",url);
                   intent.putExtra("date",date);
                   intent.putExtra("address",newaddress.getAddress());
                   intent.putExtra("latitude",newaddress.getLatitude());
                   intent.putExtra("longitude",newaddress.getLongitude());
                   addressItemHolder.itemView.getContext().startActivity(intent);
               }
           });
       }

        addressItemHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(addressItemHolder.itemView.getContext())
                        .setTitle("Are You Sure??")
                        .setMessage("Do you want to delete the address?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                Delete_Address(newaddress.get_id(),addressItemHolder.itemView.getContext());
                                pos=i;

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void Delete_Address(String id,Context context) {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .DELETE_ADDRESS(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    class AddressItemHolder extends RecyclerView.ViewHolder {
        private TextView no,address;
        private FrameLayout delete;
        private CardView parent;



        AddressItemHolder (View itemView) {
            super(itemView);
            no=itemView.findViewById(R.id.address_no_txt);
            address=itemView.findViewById(R.id.address_txt);
            delete=itemView.findViewById(R.id.delete_btn);
            parent=itemView.findViewById(R.id.parent);
            mSubscriptions = new CompositeSubscription();


        }
    }


    private void handleResponse(BasicResponse response) {

        if(response.getMessage().equals("success"))
        {
            update(pos);
        }


    }

    private void handleError(Throwable error) {



        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody,BasicResponse.class);
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
            Log.e("error",error.toString());

        }
    }

    private void update(int i)
    {   list.remove(i);
        notifyDataSetChanged();
    }


}
