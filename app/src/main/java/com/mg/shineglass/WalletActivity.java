package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.adapters.OrdersAdapter;
import com.mg.shineglass.adapters.WalletAdapter;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.MyOrders;
import com.mg.shineglass.models.Wallet;
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

public class WalletActivity extends AppCompatActivity {

    private ImageView backBtnImg;
    private CompositeSubscription mSubscriptions;
    private RecyclerView rvItem;
    private TextView amount,empty;
    private ViewDialog viewDialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_fragment);

        backBtnImg=findViewById(R.id.back_btn_img);
        backBtnImg.setOnClickListener(view -> finish());
        
        amount=findViewById(R.id.wallet_cash_value_txt);
        rvItem=findViewById(R.id.history_container);
        empty=findViewById(R.id.empty);

        SwipeRefreshLayout refreshLayout; refreshLayout=findViewById(R.id.refresh);



        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FETCH_DATA();
                refreshLayout.setRefreshing(false);
            }
        });

        viewDialog = new ViewDialog(this);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        
        
        if(sharedPreferences.getString("wallet",null)==null)
        {
            FETCH_DATA();
        }
        else
        {
            amount.setText(sharedPreferences.getString("wallet",null));
            if(sharedPreferences.getString("wallet_transaction",null).equals("empty"))
            {
                rvItem.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
            else
            {
                rvItem.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);

                Gson gson=new Gson();
                List<Wallet.Transaction> list=new ArrayList<>();
                Type type=new TypeToken<List<Wallet.Transaction>>(){}.getType();
                list=gson.fromJson(sharedPreferences.getString("wallet_transaction", null),type);

                WalletAdapter walletAdapter = new  WalletAdapter(list);
                LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
                rvItem.setLayoutManager(LinearLayout);
                rvItem.setAdapter(walletAdapter);
            }
        }
        
    }

    private void FETCH_DATA() {

        rvItem.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);

        viewDialog.showDialog();
        mSubscriptions.add(
                networkUtils.getRetrofit(sharedPreferences.getString("token", null))
                        .GET_WALLET()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse,this::handleError)
        );
    }


    private void handleResponse(Wallet response) {
        viewDialog.hideDialog();

        amount.setText(String.valueOf(response.getWallet()));

        if(response.getWallet_transaction().size()>0 && response.getWallet_transaction().get(0) !=null)
        {
            rvItem.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            Gson gson=new Gson();
            String s=gson.toJson(response.getWallet_transaction());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("wallet_transaction", s);
            editor.putString("wallet",response.getWallet().toString());
            editor.apply();

            WalletAdapter walletAdapter = new  WalletAdapter(response.getWallet_transaction());
            LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
            rvItem.setLayoutManager(LinearLayout);
            rvItem.setAdapter(walletAdapter);
        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("wallet_transaction", "empty");
            editor.putString("wallet",response.getWallet().toString());
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
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }
}