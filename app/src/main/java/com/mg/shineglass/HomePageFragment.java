package com.mg.shineglass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.adapters.BannerAdapter;
import com.mg.shineglass.models.Banners;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.Category;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.Rates;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment {
    private CompositeSubscription mSubscriptions;
    private RecyclerView rates,banners;
    private ViewPager viewPager;
    private BannerAdapter bannerAdapter;


    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.homepage_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSubscriptions = new CompositeSubscription();
        rates=view.findViewById(R.id.rates_container);
        banners=view.findViewById(R.id.categories_container);
        viewPager =  view.findViewById(R.id.banners_container);

        FETCH_DATA();
    }




    private void FETCH_DATA()
    {
        mSubscriptions.add(

//                networkUtils.getRetrofit().GET_RATES()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.io())
//                        .subscribe(this::handleResponse1,this::handleError),

                networkUtils.getRetrofit().GET_IMAGES()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse2,this::handleError));
    }



    private void handleResponse1(Response<Rates> response) {



    }

    private void handleResponse2(Response<List<Banners>> response) {
        Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();
        bannerAdapter=new BannerAdapter(Objects.requireNonNull(getContext()),response.body());
        bannerAdapter.setTimer(viewPager,5,5,1);
        viewPager.setAdapter(bannerAdapter);

    }

    private void handleError(Throwable error) {

        Log.e("error",error.toString());

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response<BasicResponse> response = gson.fromJson(errorBody,Response.class);
                assert response.body() != null;
                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }


}