package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.adapters.MyRequestAdapter;
import com.mg.shineglass.adapters.OrdersAdapter;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.FinalOrder;
import com.mg.shineglass.models.MyOrders;
import com.mg.shineglass.models.MyQuotation;
import com.mg.shineglass.network.networkUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class My_Orders_Activity extends Fragment {

    private CompositeSubscription mSubscriptions;
    private RecyclerView rvItem;
//    private LinearLayout login;
//    private TextView empty;
//    private RelativeLayout login_btn;


    public My_Orders_Activity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_my_orders, container, false);

        mSubscriptions = new CompositeSubscription();

        rvItem=view.findViewById(R.id.orders_container);
//        login=view.findViewById(R.id.empty_login_block);
//        empty=view.findViewById(R.id.empty_text);
//        login_btn=view.findViewById(R.id.login_btn);
//
//        login_btn.setOnClickListener(v->{
//            Intent intent=new Intent(getContext(),LoginSignUpActivity.class);
//            startActivity(intent);
//        });

        FETCH_DATA();

        return view;
    }


    private void FETCH_DATA()
    {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        if(sharedPreferences.getString("token", null)==null)
        {
//            login.setVisibility(View.VISIBLE);
//            rvItem.setVisibility(View.GONE);
//            empty.setVisibility(View.GONE);
        }
        else
        {
//            login.setVisibility(View.GONE);
//            rvItem.setVisibility(View.VISIBLE);
//            empty.setVisibility(View.GONE);


            mSubscriptions.add(
                    networkUtils.getRetrofit(sharedPreferences.getString("token", null))
                            .GET_ORDERS()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(this::handleResponse,this::handleError)
            );
        }
    }

    private void handleResponse(List<MyOrders> response) {

        if(response.size()>0 && response.get(0) !=null)
        {
            OrdersAdapter ordersAdapter = new OrdersAdapter(response);
            LinearLayoutManager LinearLayout = new LinearLayoutManager(getContext());
            rvItem.setLayoutManager(LinearLayout);
            rvItem.setAdapter(ordersAdapter);
        }
        else
        {
//            login.setVisibility(View.GONE);
//            rvItem.setVisibility(View.GONE);
//            empty.setVisibility(View.VISIBLE);

        }


    }

    private void handleError(Throwable error) {

        Log.e("error",error.toString());

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody,BasicResponse.class);
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }

}
