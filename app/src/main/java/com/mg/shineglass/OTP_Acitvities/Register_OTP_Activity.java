package com.mg.shineglass.OTP_Acitvities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.MainActivity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.constants;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class Register_OTP_Activity extends Activity {

    private User user;
    private String otp,type;
    private EditText E1;
    private EditText E2;
    private EditText E3;
    private EditText E4;
    private RelativeLayout button;
    private TextView textView, resend;
    private CompositeSubscription mSubscriptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_otp_activity);

        mSubscriptions = new CompositeSubscription();

        Intent intent=getIntent();
        user=new User(intent.getStringExtra("name"),
                intent.getStringExtra("email"),
                intent.getStringExtra("password"),
                intent.getStringExtra("phone"));
        type=intent.getStringExtra("type");
        otp=intent.getStringExtra("otp");

        //textView = (TextView) findViewById(R.id.valid_invalid_otp);
        resend=(TextView)findViewById(R.id.signup_txt);
        button =  findViewById(R.id.verify_btn);
        E1 = (EditText) findViewById(R.id.edt_1);
        E2 = (EditText) findViewById(R.id.edt_2);
        E3 = (EditText) findViewById(R.id.edt_3);
        E4 = (EditText) findViewById(R.id.edt_4);

        button.setOnClickListener(view -> register());
    }

    private void register() {

        String enteredOtp = E1.getText().toString() + E2.getText().toString() + E3.getText().toString() + E4.getText().toString();

        if (enteredOtp.equals(otp)) {

            RegisterUser(user);
        } else {
            showSnackBarMessage("Enter Valid Otp");
            textView.setVisibility(View.VISIBLE);

        }
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

        private void showSnackBarMessage(String message) {
            Snackbar.make(getRootView(),message,Snackbar.LENGTH_LONG).show();

        }


    private void RegisterUser(User user) {

        mSubscriptions.add(networkUtils.getRetrofit().REGISTER(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response<LoginResponse> response) {


        Toast.makeText(Register_OTP_Activity.this, "SignUp success!", Toast.LENGTH_SHORT).show();
        showSnackBarMessage(response.body().getMessage());
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("type","local");
        editor.putString(constants.TOKEN,response.headers().get("jwt"));
        editor.putString(constants.EMAIL,response.body().getUser().getEmail());
        editor.putString(constants.USERNAME,response.body().getUser().getUsername());
        editor.putString(constants.PHONE,response.body().getUser().getMobile());
        editor.putString(constants.TYPE,response.body().getType());
        editor.apply();
        goToHome();

    }

    private void handleError(Throwable error) {



        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.message());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error", error.toString());
            showSnackBarMessage(error.toString());
        }
    }


    private void goToHome() {
        Intent intent = new Intent(Register_OTP_Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void CountDownTime(){
        new CountDownTimer(60000,1000)
        {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    }

