package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.adapters.Address_Adapter;
import com.mg.shineglass.models.Address;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class My_Account_Address_Activity extends AppCompatActivity {
    private RecyclerView rvItem;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private Button add;
    private String Quotation,Date,Total,url;
    private ViewDialog viewDialog;
    private long mLastClickTime = 0;

    private ImageView backBtnImg;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_addresses);

        Intent intent=getIntent();
        Quotation=intent.getStringExtra("quotation");
        url=intent.getStringExtra("url");
        Total=intent.getStringExtra("total");
        Date=intent.getStringExtra("date");
        add=findViewById(R.id.add_new_button);
        rvItem=findViewById(R.id.saved_addresses_container);

        backBtnImg=findViewById(R.id.back_btn_img_addresses);

        backBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(My_Account_Address_Activity.this, My_Account_New_Address.class);
                startActivity(i);
            }
        });
        mSubscriptions = new CompositeSubscription();
        viewDialog = new ViewDialog(this);
        viewDialog.showDialog();
        FetchAddress();

    }


    private void FetchAddress() {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);



        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .GET_ADDRESS()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(BasicResponse response) {

        viewDialog.hideDialog();
        List<Address> list = new ArrayList<>();
        list=response.getAddressList();

        Address_Adapter Adapter = new Address_Adapter(list,Quotation,url,Total,Date,false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(My_Account_Address_Activity.this);
        rvItem.setAdapter(Adapter);
        rvItem.setLayoutManager(layoutManager);


    }

    private void handleError(Throwable error) {

        viewDialog.hideDialog();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {




        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FetchAddress();
    }
}

