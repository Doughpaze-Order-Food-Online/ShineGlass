package com.mg.shineglass.OTP_Acitvities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.mg.shineglass.DeliveryBoyMainActivity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.OrderDeliver;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DeliveryBoyOtp_Activity extends AppCompatActivity {  private User user;

    private EditText E1;
    //    private EditText E2;
//    private EditText E3;
//    private EditText E4;
    private RelativeLayout button;
    private TextView timer, resend,enter_otp,sec_otp;
    private CompositeSubscription mSubscriptions;
    private RelativeLayout Resend_block;
    private ImageView backImgBtn;
    private ViewDialog viewDialog;
    private String orderId,id;
    private SharedPreferences sharedPreferences;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_otp_activity);

        backImgBtn=findViewById(R.id.back_btn_img);
        backImgBtn.setOnClickListener(v -> finish());

        Intent i=getIntent();
        orderId = i.getStringExtra("orderId");
        id = i.getStringExtra("_id");
        mSubscriptions = new CompositeSubscription();


        //textView = (TextView) findViewById(R.id.valid_invalid_otp);
        resend = (TextView) findViewById(R.id.signup_txt);
        button = findViewById(R.id.verify_btn);
        E1 = (EditText) findViewById(R.id.edt_1);
//        E2 = (EditText) findViewById(R.id.edt_2);
//        E3 = (EditText) findViewById(R.id.edt_3);
//        E4 = (EditText) findViewById(R.id.edt_4);
        timer = findViewById(R.id.time_txt);
        Resend_block = findViewById(R.id.resend_otp_block);
        enter_otp = findViewById(R.id.enter_otp_txt);
        sec_otp = findViewById(R.id.sec_txt);
        resend = findViewById(R.id.signup_txt);
        enter_otp.setVisibility(View.GONE);
        sec_otp.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);


//        E1.addTextChangedListener(new DeliveryBoyOtp_Activity.GenericTextWatcher(E1));
//        E2.addTextChangedListener(new DeliveryBoyOtp_Activity.GenericTextWatcher(E2));
//        E3.addTextChangedListener(new DeliveryBoyOtp_Activity.GenericTextWatcher(E3));
//        E4.addTextChangedListener(new DeliveryBoyOtp_Activity.GenericTextWatcher(E4));

        button.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            VERIFY();
        });

        Resend_block.setVisibility(View.GONE);

        viewDialog = new ViewDialog(this);


    }

    private void VERIFY() {

        String enteredOtp = E1.getText().toString();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        viewDialog.showDialog();

        OrderDeliver orderDeliver=new OrderDeliver();
        orderDeliver.setOrderNo(orderId);
        orderDeliver.set_id(id);
        orderDeliver.setOtp(Integer.parseInt(enteredOtp));
        mSubscriptions.add(networkUtils.getRetrofit(sharedPreferences.getString("token", null))
                .VERIFY_OTP(orderDeliver)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse2,this::handleError));
    }



    private void handleResponse2(Integer response) {

        viewDialog.hideDialog();

        if(response==1)
        {
            Toast.makeText(this, "Order Delivered!", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(DeliveryBoyOtp_Activity.this, DeliveryBoyMainActivity.class);
            sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("previous",null);
            editor.putString("current",null);
            editor.apply();
            startActivity(i);
            finish();
        }
        else
        {
            Toast.makeText(this, "Order Delivered!", Toast.LENGTH_SHORT).show();
        }


    }



    private void handleError(Throwable error) {

        viewDialog.hideDialog();

        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response<BasicResponse> response = gson.fromJson(errorBody,Response.class);
                assert response.body() != null;
                showSnackBarMessage(response.body().getMessage());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error",error.toString());
            showSnackBarMessage( "Network Error !");

        }
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(getRootView(),message,Snackbar.LENGTH_SHORT).show();

    }

    private View getRootView() {
        final ViewGroup contentViewGroup = (ViewGroup) findViewById(android.R.id.content);
        View rootView = null;

        if(contentViewGroup != null)
            rootView = contentViewGroup.getChildAt(0);

        if(rootView == null)
            rootView = getWindow().getDecorView().getRootView();

        return rootView;
    }


//    public class GenericTextWatcher implements TextWatcher {
//        private View view;
//
//        private GenericTextWatcher(View view) {
//            this.view = view;
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//            String text = editable.toString();
//            switch (view.getId()) {
//
//                case R.id.edt_1:
//                    if (text.length() == 1)
//                        E2.requestFocus();
//                    break;
//                case R.id.edt_2:
//                    if (text.length() == 1)
//                        E3.requestFocus();
//                    else if (text.length() == 0)
//                        E1.requestFocus();
//                    break;
//                case R.id.edt_3:
//                    if (text.length() == 1)
//                        E4.requestFocus();
//                    else if (text.length() == 0)
//                        E2.requestFocus();
//                    break;
//                case R.id.edt_4:
//                    if (text.length() == 0)
//                        E3.requestFocus();
//                    break;
//            }
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//        }
//    }
}

