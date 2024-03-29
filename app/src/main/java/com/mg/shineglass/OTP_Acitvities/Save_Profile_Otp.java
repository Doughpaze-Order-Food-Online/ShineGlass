package com.mg.shineglass.OTP_Acitvities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.mg.shineglass.R;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class Save_Profile_Otp extends AppCompatActivity {

    private User user;
    private String otp;
    private EditText E1;
    //    private EditText E2;
//    private EditText E3;
//    private EditText E4;
    private RelativeLayout button;
    private TextView timer, resend;
    private CompositeSubscription mSubscriptions;
    private RelativeLayout Resend_block;
    private ImageView backImgBtn;
    private ViewDialog viewDialog;
    private long mLastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_otp_activity);

        backImgBtn=findViewById(R.id.back_btn_img);
        backImgBtn.setOnClickListener(v -> finish());
        
        mSubscriptions = new CompositeSubscription();

        Intent intent=getIntent();
        user=new User();
        user.setMobile(intent.getStringExtra("phone"));
        user.setEmail(intent.getStringExtra("email"));
        user.setUsername(intent.getStringExtra("name"));
        otp = intent.getStringExtra("otp");


        //textView = (TextView) findViewById(R.id.valid_invalid_otp);
        resend = (TextView) findViewById(R.id.signup_txt);
        button = findViewById(R.id.verify_btn);
        E1 = (EditText) findViewById(R.id.edt_1);
//        E2 = (EditText) findViewById(R.id.edt_2);
//        E3 = (EditText) findViewById(R.id.edt_3);
//        E4 = (EditText) findViewById(R.id.edt_4);
        timer = findViewById(R.id.time_txt);
        Resend_block = findViewById(R.id.resend_otp_block);
        resend = findViewById(R.id.signup_txt);

//        E1.addTextChangedListener(new Save_Profile_Otp.GenericTextWatcher(E1));
//        E2.addTextChangedListener(new Save_Profile_Otp.GenericTextWatcher(E2));
//        E3.addTextChangedListener(new Save_Profile_Otp.GenericTextWatcher(E3));
//        E4.addTextChangedListener(new Save_Profile_Otp.GenericTextWatcher(E4));

        button.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            NUMBER_LOGIN();
        });
        resend.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            RESEND_OTP(user);});
        viewDialog = new ViewDialog(this);

        CountDownTime();
    }

    private void NUMBER_LOGIN() {

        String enteredOtp = E1.getText().toString();
        if(enteredOtp.equals(otp))
        {


            SAVE_PROFILE();


        }
        else
        {
            showSnackBarMessage("Enter Valid Otp");
            //textView.setVisibility(View.VISIBLE);
        }
    }

    private void SAVE_PROFILE() {
        viewDialog.showDialog();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mSubscriptions.add(networkUtils.getRetrofit(sharedPreferences.getString("token", null)).SAVE_PROFILE_DETAILS(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse2,this::handleError));

    }

    private void RESEND_OTP(User user)
    {
        viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit()
                .GOOGLE_FACEBOOK_OTP(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse2(LoginResponse response) {

        viewDialog.hideDialog();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(user.getMobile()!=null)
        {
            editor.putString(constants.PHONE,user.getMobile());
        }

        if(user.getEmail()!=null)
        {
            editor.putString(constants.EMAIL,user.getEmail());
        }

        if(user.getUsername()!=null)
        {
            editor.putString(constants.USERNAME,user.getUsername());
        }
        editor.apply();
        finish();

    }

    private void handleResponse(LoginResponse response) {
        viewDialog.hideDialog();
        otp=response.getOtp();

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





    private void CountDownTime(){

        Resend_block.setVisibility(View.GONE);
        new CountDownTimer(60000,1000)
        {

            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf(millisUntilFinished/1000));

            }

            @Override
            public void onFinish() {
                Resend_block.setVisibility(View.VISIBLE);
            }
        }.start();
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

