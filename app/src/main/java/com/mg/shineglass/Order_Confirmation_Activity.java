package com.mg.shineglass;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import android.view.View;
import android.view.WindowManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.adapters.WalletAdapter;
import com.mg.shineglass.models.Address;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.FinalOrder;
import com.mg.shineglass.models.PaymentDetails;
import com.mg.shineglass.models.Wallet;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mg.shineglass.utils.validation.validateFields;

public class Order_Confirmation_Activity extends AppCompatActivity {

    private TextView quotation,date,address,total,amount;
    private RelativeLayout view;
    private String Quotation,Date,Address,url;
    private RadioGroup radioGroup;
    private RelativeLayout confirm;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private long mLastClickTime = 0;
    private String  TAG ="order_confirm_activity";
    private Integer ActivityRequestCode = 2;
    private ImageView backBtnImg;
    private Double latitude,longitude;
    private Address newaddres;
    private ViewDialog viewDialog;
    private EditText tid;
    private Double Total;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_and_payment_details_fragment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mSubscriptions = new CompositeSubscription();

        backBtnImg=findViewById(R.id.back_btn_img);
        backBtnImg.setOnClickListener(view -> finish());

        Intent intent=getIntent();
        Quotation=intent.getStringExtra("quotation");
        url=intent.getStringExtra("url");
        Total=Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("total")));

        Address=intent.getStringExtra("address");
        Date=intent.getStringExtra("date");
        latitude=Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("latitude")));
        longitude=Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("longitude")));
        viewDialog = new ViewDialog(this);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        newaddres=new Address();
        newaddres.setAddress(Address);
        newaddres.setLatitude(latitude);
        newaddres.setLongitude(longitude);

        quotation=findViewById(R.id.request_no);
        date=findViewById(R.id.date_value);
        address=findViewById(R.id.name_txt);
        total=findViewById(R.id.total_charge_value);
        view=findViewById(R.id.request_quotation_btn);

        confirm=findViewById(R.id.place_order_btn);
        amount=findViewById(R.id.wallet_cash_value_txt);
        quotation.setText(Quotation);
        date.setText(Date);
        total.setText(String.valueOf(Total));
        address.setText(Address);
        tid=findViewById(R.id.transaction_id_value);


        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);




        view.setOnClickListener(v -> {
            Intent i=new Intent(Order_Confirmation_Activity.this, Quotation_Activity.class);
            i.putExtra("quotation",Quotation);
            i.putExtra("url",url);
            i.putExtra("total",Total.toString());
            i.putExtra("date",Date);
            startActivity(i);
            finish();

        });

        confirm.setOnClickListener(v -> {
            if(!validateFields(tid.getText().toString()))
            {
                tid.setError("Enter Transaction Id");
                return ;
            }
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                new AlertDialog.Builder(Order_Confirmation_Activity.this)
                        .setTitle("Are you sure??")
                        .setMessage("Do you want to place the order??")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                OFFLINE();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


        });



    }



    private void OFFLINE() {

        FinalOrder finalOrder=new FinalOrder();
        finalOrder.setAddress(newaddres);
        finalOrder.setQuotationNo(Quotation);
        finalOrder.setTid(tid.getText().toString());
        viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit( mSharedPreferences.getString(constants.TOKEN, null))
                .PLACE_OFFLINE_ORDER(finalOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }



    private void handleResponse(Integer status) {
        Intent i=new Intent(Order_Confirmation_Activity.this,MainActivity.class);
        i.putExtra("callMyOrdersFragment","OrderConfirmationActivity");
        startActivity(i);
        finish();
        Toast.makeText(this, "Order Placed!", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("request",null);
        editor.putString("orders",null);
        editor.apply();

    }


    private void handleError(Throwable error) {
        viewDialog.hideDialog();

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
