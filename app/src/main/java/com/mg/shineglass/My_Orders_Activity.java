package com.mg.shineglass;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.adapters.OrdersAdapter;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.MyOrders;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;

import java.io.IOException;
import java.util.List;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class My_Orders_Activity extends Fragment {

    private CompositeSubscription mSubscriptions;
    private RecyclerView rvItem;
    private TextView empty;
    private ViewDialog viewDialog;



    public My_Orders_Activity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_my_orders, container, false);

        mSubscriptions = new CompositeSubscription();

        rvItem=view.findViewById(R.id.orders_container);
        empty=view.findViewById(R.id.empty_text);
        viewDialog = new ViewDialog(getActivity());

        FETCH_DATA();


        return view;
    }


    private void FETCH_DATA()
    {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        if(sharedPreferences.getString("token", null)==null)
        {
            rvItem.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
        }
        else
        {

            rvItem.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);

            viewDialog.showDialog();

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
        viewDialog.hideDialog();

        if(response.size()>0 && response.get(0) !=null)
        {
            OrdersAdapter ordersAdapter = new OrdersAdapter(response);
            LinearLayoutManager LinearLayout = new LinearLayoutManager(getContext());
            rvItem.setLayoutManager(LinearLayout);
            rvItem.setAdapter(ordersAdapter);
        }
        else
        {

            rvItem.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);

        }


    }

    private void handleError(Throwable error) {
        viewDialog.hideDialog();

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
