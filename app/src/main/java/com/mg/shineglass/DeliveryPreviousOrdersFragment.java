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
import com.mg.shineglass.adapters.DeliveryOrderAdapter;
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

public class DeliveryPreviousOrdersFragment extends Fragment {

    private CompositeSubscription mSubscriptions;
    private RecyclerView rvItem;
    private TextView empty;
    private ViewDialog viewDialog;
    private SharedPreferences sharedPreferences;


    public DeliveryPreviousOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.previous_orders_fragment, container, false);
        mSubscriptions = new CompositeSubscription();
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        rvItem = rootView.findViewById(R.id.current_orders_container);
        empty = rootView.findViewById(R.id.empty_text);
        viewDialog = new ViewDialog(getActivity());

        SwipeRefreshLayout refreshLayout;
        refreshLayout = rootView.findViewById(R.id.refresh);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FETCH_DATA();
                refreshLayout.setRefreshing(false);
            }
        });


        if (sharedPreferences.getString("previous", null) == null) {
            FETCH_DATA();
        } else {
            if (sharedPreferences.getString("previous", null).equals("empty")) {
                rvItem.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            } else {

                rvItem.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                Gson gson = new Gson();
                List<MyOrders> list = new ArrayList<>();
                Type type = new TypeToken<List<MyOrders>>() {
                }.getType();
                list = gson.fromJson(sharedPreferences.getString("previous", null), type);

                DeliveryOrderAdapter deliveryOrderAdapter = new DeliveryOrderAdapter(list);
                LinearLayoutManager LinearLayout = new LinearLayoutManager(getContext());
                rvItem.setLayoutManager(LinearLayout);
                rvItem.setAdapter(deliveryOrderAdapter);
            }


        }

        return rootView;
    }

    private void FETCH_DATA() {
        if (sharedPreferences.getString("token", null) == null) {

            rvItem.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
        } else {
            rvItem.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);

            viewDialog.showDialog();
            mSubscriptions.add(
                    networkUtils.getRetrofit(sharedPreferences.getString("token", null))
                            .GET_PREVIOUS_ORDERS()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(this::handleResponse, this::handleError)
            );
        }
    }

    private void handleResponse(List<MyOrders> response) {
        viewDialog.hideDialog();

        if (response.size() > 0 && response.get(0) != null) {

            Gson gson = new Gson();
            String s = gson.toJson(response);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("previous", s);
            editor.apply();

            DeliveryOrderAdapter deliveryOrderAdapter = new DeliveryOrderAdapter(response);
            LinearLayoutManager LinearLayout = new LinearLayoutManager(getContext());
            rvItem.setLayoutManager(LinearLayout);
            rvItem.setAdapter(deliveryOrderAdapter);
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("previous", "empty");
            editor.apply();
            rvItem.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);

        }


    }

    private void handleError(Throwable error) {
        viewDialog.hideDialog();

        Log.e("error", error.toString());

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody, BasicResponse.class);
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }

}