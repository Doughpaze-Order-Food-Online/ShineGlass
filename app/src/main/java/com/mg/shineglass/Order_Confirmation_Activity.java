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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.FinalOrder;
import com.mg.shineglass.models.PaymentDetails;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.constants;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import java.io.IOException;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class Order_Confirmation_Activity extends Activity {

    private TextView quotation,date,address,total;
    private RelativeLayout view;
    private String Quotation,Date,Address,Total,url;
    private RadioGroup radioGroup;
    private RelativeLayout confirm;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private long mLastClickTime = 0;
    private String  TAG ="order_confirm_activity";
    private Integer ActivityRequestCode = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_and_payment_details_fragment);
        mSubscriptions = new CompositeSubscription();

        Intent intent=getIntent();
        Quotation=intent.getStringExtra("quotation");
        url=intent.getStringExtra("url");
        Total=intent.getStringExtra("total");
        Address=intent.getStringExtra("address");
        Date=intent.getStringExtra("date");

        quotation=findViewById(R.id.request_no_value);
        date=findViewById(R.id.date_value);
        address=findViewById(R.id.name_txt);
        total=findViewById(R.id.total_charge_value);
        view=findViewById(R.id.request_quotation_btn);
        radioGroup=findViewById(R.id.mode);
        confirm=findViewById(R.id.place_order_btn);

        quotation.setText(Quotation);
        date.setText(Date);
        total.setText(Total);
        address.setText(Address);

        view.setOnClickListener(v -> {
            Intent i=new Intent(Order_Confirmation_Activity.this, Quotation_Activity.class);
            i.putExtra("quotationNo",Quotation);
            i.putExtra("url",url);
            i.putExtra("total",Total);
            i.putExtra("date",Date);
            startActivity(i);
            finish();

        });

        confirm.setOnClickListener(v -> {
            if(radioGroup.getCheckedRadioButtonId()==-1)
            {
                Toast.makeText(this, "Select Mode Of Payment!", Toast.LENGTH_SHORT).show();
            }
            else
            {
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

            }
        });


    }



    private void OFFLINE() {
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        FinalOrder finalOrder=new FinalOrder();
        finalOrder.setAddress(Address);
        finalOrder.setQuotationNo(Quotation);
        mSubscriptions.add(networkUtils.getRetrofit( mSharedPreferences.getString(constants.TOKEN, null))
                .PLACE_OFFLINE_ORDER(finalOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }

    private void GET_TOKEN() {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        FinalOrder finalOrder=new FinalOrder();
        finalOrder.setAddress(Address);
        finalOrder.setQuotationNo(Quotation);
        finalOrder.setAmount(Total);
        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .GET_TOKEN(constants.MID,finalOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse2,this::handleError));
    }

    private void ONLINE() {


    }

    private void handleResponse(Integer status) {

        if(status==1)
        {
            Intent i=new Intent(Order_Confirmation_Activity.this,MainActivity.class);
            startActivity(i);
            finish();
            Toast.makeText(this, "Order Placed!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Something Went Wrong, Try Again Sometime!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void handleResponse2(String token) {

        String txnAmountString = total.getText().toString();
        String midString = constants.MID ;
        String orderIdString = Quotation;
        String txnTokenString = token;

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

                if(bundle.get("RESPCODE").equals("01") || bundle.get("RESPCODE").equals("400"))
                {
                    paymentDetails.setBankname(bundle.getString("BANKNAME"));
                    paymentDetails.setQuotationNo(bundle.getString("ORDERID"));
                    paymentDetails.setAmountpaid(Double.parseDouble(bundle.getString("TXNAMOUNT")));
                    paymentDetails.setDate(bundle.getString("TXNDATE"));
                    paymentDetails.setTransactionId(bundle.getString("TXNID"));
                    paymentDetails.setPaymentType(bundle.getString("PAYMENTMODE"));
                    paymentDetails.setBankTransactionId(bundle.getString("BANKTXNID"));
                    paymentDetails.setPayment_status(true);


                    PLACE_ORDER(paymentDetails);
                }
                else
                {
                    paymentDetails.setBankname(bundle.getString("BANKNAME"));
                    paymentDetails.setQuotationNo(bundle.getString("ORDERID"));
                    paymentDetails.setAmountpaid(Double.parseDouble(bundle.getString("TXNAMOUNT")));
                    paymentDetails.setDate(bundle.getString("TXNDATE"));
                    paymentDetails.setTransactionId(bundle.getString("TXNID"));
                    paymentDetails.setPaymentType(bundle.getString("PAYMENTMODE"));
                    paymentDetails.setBankTransactionId(bundle.getString("BANKTXNID"));
                    paymentDetails.setPayment_status(false);

                    PLACE_ORDER(paymentDetails);
                }



            }

            @Override
            public void networkNotAvailable() {
                Log.e(TAG, "network not available ");
            }

            @Override
            public void onErrorProceed(String s) {
                Log.e(TAG, " onErrorProcess "+s.toString());
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth "+s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error "+s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web "+s+"--"+s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel "+s);
                PaymentDetails paymentDetails=new PaymentDetails();
                paymentDetails.setPayment_status(false);
                paymentDetails.setStatus("Payment Transaction Cancelled");
                paymentDetails.setQuotationNo(bundle.getString("ORDERID"));
                PLACE_ORDER(paymentDetails);
            }
        });

        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, ActivityRequestCode);

    }

    private void handleError(Throwable error) {

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

    private void PLACE_ORDER(PaymentDetails paymentDetails) {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);



        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .PLACE_ONLINE_ORDER(constants.MID,paymentDetails,Quotation)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }
}
