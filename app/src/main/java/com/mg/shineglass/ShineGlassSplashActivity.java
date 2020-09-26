package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.adapters.BannerAdapter;
import com.mg.shineglass.adapters.CategoryAdapter;
import com.mg.shineglass.adapters.RatesAdapter;
import com.mg.shineglass.models.Banners;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.Category;
import com.mg.shineglass.models.Rates;
import com.mg.shineglass.models.SpanningGridLayoutManager;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ShineGlassSplashActivity extends AppCompatActivity {

    private static int TIME_OUT = 2000;
    private CompositeSubscription mSubscriptions;
    private Handler handler;
    private  Runnable runnable;
    private SharedPreferences mSharedPreferences;
    private Intent i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mSubscriptions = new CompositeSubscription();

        runnable= () -> {
            if(mSharedPreferences.getString("token", null) == null)
            {
                i= new Intent(ShineGlassSplashActivity.this,LoginSignUpActivity.class);
            }
            else
            {
                i= new Intent(ShineGlassSplashActivity.this,mSharedPreferences.getString("userType", null).equals("user")?MainActivity.class:DeliveryBoyMainActivity.class);
            }

            startActivity(i);
            finish();
        };

        handler=new Handler();
        handler.postDelayed(runnable,TIME_OUT);

       FETCH_DATA();

    }

    private void FETCH_DATA()
    {
        mSubscriptions.addAll(

                networkUtils.getRetrofit().GET_RATES()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse1,this::handleError),

                networkUtils.getRetrofit().GET_IMAGES()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse2,this::handleError),
                networkUtils.getRetrofit().GET_CATEGORY()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse3,this::handleError)
        );
    }


    private void handleResponse1(Response<List<Rates>> response) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson=new Gson();
        String s=gson.toJson(response.body());
        editor.putString("rates",s);
        editor.apply();

    }



    private void handleResponse3(List<Category> response) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson=new Gson();
        String s=gson.toJson(response);
        editor.putString("category",s);
        editor.apply();


    }

    private void handleResponse2(Response<List<Banners>> response) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson=new Gson();
        String s=gson.toJson(response.body());
        editor.putString("banner",s);
        editor.apply();
    }

    private void handleError(Throwable error) {

        Log.e("error",error.toString());

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response<BasicResponse> response = gson.fromJson(errorBody,Response.class);
                assert response.body() != null;
                Toast.makeText(this, response.body().getMessage(), Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Network Error !", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSubscriptions!=null)
        {
            mSubscriptions.unsubscribe();
        }
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mSubscriptions!=null)
        {
            mSubscriptions.unsubscribe();
        }
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mSubscriptions!=null)
        {
            mSubscriptions.unsubscribe();
        }
        handler.removeCallbacks(runnable);
    }
}