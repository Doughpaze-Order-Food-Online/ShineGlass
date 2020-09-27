package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mg.shineglass.utils.ViewDialog;

import rx.subscriptions.CompositeSubscription;

public class WalletActivity extends AppCompatActivity {

    private ImageView backBtnImg;
    private CompositeSubscription mSubscriptions;
    private RecyclerView rvItem;
    private TextView amount;
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

        viewDialog = new ViewDialog(this);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        
        
        if(sharedPreferences.getString("wallet",null)==null)
        {
            FETCH_DATA();
        }
        else
        {
            
        }
        
    }

    private void FETCH_DATA() {
    }
}