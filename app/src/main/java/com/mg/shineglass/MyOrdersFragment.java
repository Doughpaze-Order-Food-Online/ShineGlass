package com.mg.shineglass;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.Interface.FetchData;
import com.mg.shineglass.adapters.OrdersAdapter;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.MyOrders;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MyOrdersFragment extends Fragment implements FetchData {

    private CompositeSubscription mSubscriptions;
    private RecyclerView rvItem;
    private TextView empty;
    private ViewDialog viewDialog;
    private SharedPreferences sharedPreferences;



    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_my_orders, container, false);
        mSubscriptions = new CompositeSubscription();

        rvItem=view.findViewById(R.id.orders_container);
        empty=view.findViewById(R.id.empty_text);
        viewDialog = new ViewDialog(getActivity());
        SwipeRefreshLayout refreshLayout=view.findViewById(R.id.refresh);



        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FETCH_DATA();
                refreshLayout.setRefreshing(false);
            }
        });

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        if(sharedPreferences.getString("orders",null)==null)
        {
            FETCH_DATA();
        }
        else
        {
            if(sharedPreferences.getString("orders", null).equals("empty"))
            {
                rvItem.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);

            }
            else {

                rvItem.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);

                Gson gson=new Gson();
                List<MyOrders> list=new ArrayList<>();
                Type type=new TypeToken<List<MyOrders>>(){}.getType();
                list=gson.fromJson(sharedPreferences.getString("orders", null),type);

                OrdersAdapter ordersAdapter = new OrdersAdapter(list,getActivity(),this);
                LinearLayoutManager LinearLayout = new LinearLayoutManager(getContext());
                rvItem.setLayoutManager(LinearLayout);
                rvItem.setAdapter(ordersAdapter);
            }

        }



        return view;
    }


    private void FETCH_DATA()
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

    private void handleResponse(List<MyOrders> response) {
        viewDialog.hideDialog();

        if(response.size()>0 && response.get(0) !=null)
        {
            Gson gson=new Gson();
            String s=gson.toJson(response);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("orders", s);
            editor.apply();

            OrdersAdapter ordersAdapter = new OrdersAdapter(response,getActivity(),this);
            LinearLayoutManager LinearLayout = new LinearLayoutManager(getContext());
            rvItem.setLayoutManager(LinearLayout);
            rvItem.setAdapter(ordersAdapter);
        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("orders", "empty");
            editor.apply();
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

    @Override
    public void FETCH() {
        FETCH_DATA();
    }
}