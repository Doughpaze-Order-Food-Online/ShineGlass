package com.mg.shineglass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
    private EditText redeem_checkbox;
    private Double Amount,Wallet,Total;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_and_payment_details_fragment);
        mSubscriptions = new CompositeSubscription();

        backBtnImg=findViewById(R.id.back_btn_img);
        backBtnImg.setOnClickListener(view -> finish());

        Intent intent=getIntent();
        Quotation=intent.getStringExtra("quotation");
        url=intent.getStringExtra("url");
        Total=Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("total")));
        Amount=Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("total")));
        Wallet=0.0;
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
        radioGroup=findViewById(R.id.mode);
        confirm=findViewById(R.id.place_order_btn);
        amount=findViewById(R.id.wallet_cash_value_txt);
        redeem_checkbox=findViewById(R.id.redeem_amount_edt);
        quotation.setText(Quotation);
        date.setText(Date);
        total.setText(String.valueOf(Total));
        address.setText(Address);
        redeem_checkbox.setText(String.valueOf(0.00));

        amount.setVisibility(View.GONE);
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

            if(Wallet>0 && !validateFields(redeem_checkbox.getText().toString()))
            {
                Toast.makeText(this, "Enter Amount to Redeem!", Toast.LENGTH_SHORT).show();
                return;
            }
            Amount=Total-Double.parseDouble(redeem_checkbox.getText().toString());

            if(radioGroup.getCheckedRadioButtonId()==-1)
            {
                Toast.makeText(this, "Select Mode Of Payment!", Toast.LENGTH_SHORT).show();
                return;
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
                                    if(radioGroup.getCheckedRadioButtonId()==R.id.offline)
                                    {
                                        OFFLINE();
                                    }
                                    else
                                    {
                                        GET_TOKEN();
                                    }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


        });

        FETCH_DATA();

    }



    private void OFFLINE() {
        FinalOrder finalOrder=new FinalOrder();
        finalOrder.setAddress(newaddres);
        finalOrder.setQuotationNo(Quotation);
        finalOrder.setTotal(Total);
        finalOrder.setAmount(Amount);
        finalOrder.setWallet(Double.parseDouble(redeem_checkbox.getText().toString()));

        viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit( mSharedPreferences.getString(constants.TOKEN, null))
                .PLACE_OFFLINE_ORDER(finalOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }

    private void GET_TOKEN() {



        FinalOrder finalOrder=new FinalOrder();
        finalOrder.setAddress(newaddres);
        finalOrder.setQuotationNo(Quotation);
        finalOrder.setTotal(Total);
        finalOrder.setAmount(Amount);
        finalOrder.setWallet(Wallet);
        viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .GET_TOKEN(constants.MID,finalOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse2,this::handleError));
    }


    private void handleResponse(Integer status) {
        viewDialog.hideDialog();
        if(status==1)
        {
            Intent i=new Intent(Order_Confirmation_Activity.this,MainActivity.class);
            startActivity(i);
            finish();
            Toast.makeText(this, "Order Placed!", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("request",null);
            editor.putString("orders",null);
            editor.apply();
        }
        else
        {

            Alert();

        }

    }

    private void handleResponse2(BasicResponse response) {
        viewDialog.hideDialog();
        String txnAmountString = Amount.toString();
        String midString = constants.MID ;
        String orderIdString = response.getOrderNo();
        String txnTokenString = response.getToken();

        // for test mode use it
        String host = "https://securegw-stage.paytm.in/";
        // for production mode use it
        //String host = "https://securegw.paytm.in/";
        String orderDetails = "MID: " + midString + ", OrderId: " + orderIdString + ", TxnToken: " + txnTokenString
                + ", Amount: " + txnAmountString;
        Log.e(TAG, "order details "+ orderDetails);

        String callBackUrl = host + "theia/paytmCallback?ORDER_ID="+orderIdString;
        Log.e(TAG, " callback URL "+callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(orderIdString, midString, txnTokenString, txnAmountString, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback(){
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Log.e(TAG, "Response (onTransactionResponse) : "+bundle.toString());
                PaymentDetails paymentDetails=new PaymentDetails();
                    PLACE_ORDER(response.getOrderNo());




            }

            @Override
            public void networkNotAvailable() {

                Alert();

                Log.e(TAG, "network not available ");
            }

            @Override
            public void onErrorProceed(String s) {
                Alert();
                Log.e(TAG, " onErrorProcess "+s.toString());
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Alert();
                Log.e(TAG, "Clientauth "+s);
            }

            @Override
            public void someUIErrorOccurred(String s)
            { Alert();
                Log.e(TAG, " UI error "+s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Alert();
                Log.e(TAG, " error loading web "+s+"--"+s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {

                Alert();
                Log.e(TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel "+s);
            }
        });

        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, ActivityRequestCode);

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

    private void PLACE_ORDER(String orderNo) {
        viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .PLACE_ONLINE_ORDER(constants.MID,orderNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }

    private void FETCH_DATA() {

        viewDialog.showDialog();
        mSubscriptions.add(
                networkUtils.getRetrofit(mSharedPreferences.getString("token", null))
                        .GET_WALLET()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse3,this::handleError)
        );
    }


    private void handleResponse3(Wallet response) {

        Wallet=response.getWallet();
        viewDialog.hideDialog();
        amount.setVisibility(View.VISIBLE);
        amount.setText(String.valueOf(response.getWallet()));

        if(response.getWallet()>0)
        {
            redeem_checkbox.setEnabled(true);
        }
        else
        {
            redeem_checkbox.setEnabled(false);
        }
    }


    private void Alert()
    {

        new AlertDialog.Builder(Order_Confirmation_Activity.this)
                .setTitle("Something Went Wrong!")
                .setMessage("If Amount is Debited from your bank account, Check the status of the payment in My Orders, else place the order again after sometime :) ")
                .setNeutralButton("Okay", (DialogInterface.OnClickListener) (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("request",null);
                    editor.putString("orders",null);
                    editor.apply();
                    Intent i=new Intent(Order_Confirmation_Activity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();





}



}
