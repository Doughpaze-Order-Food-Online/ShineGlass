package com.mg.shineglass.OTP_Acitvities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.mg.shineglass.MainActivity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.constants;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class Google_Otp_Activity extends AppCompatActivity {

    private User user;
    private String otp,type,token;
    private EditText E1;
    private EditText E2;
    private EditText E3;
    private EditText E4;
    private RelativeLayout button;
    private TextView timer, resend;
    private CompositeSubscription mSubscriptions;
    private RelativeLayout Resend_block;
    private ImageView backImgBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_otp_activity);

        backImgBtn=findViewById(R.id.back_btn_img);
        backImgBtn.setOnClickListener(v -> finish());

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mSubscriptions = new CompositeSubscription();

        Intent intent=getIntent();
        user=new User(intent.getStringExtra("name"),
                intent.getStringExtra("email"),
                intent.getStringExtra("password"),
                intent.getStringExtra("phone"));
        type=intent.getStringExtra("type");
        otp=intent.getStringExtra("otp");
        token=intent.getStringExtra("token");



        //textView = (TextView) findViewById(R.id.valid_invalid_otp);
        resend=(TextView)findViewById(R.id.signup_txt);
        button =  findViewById(R.id.verify_btn);
        E1 = (EditText) findViewById(R.id.edt_1);
        E2 = (EditText) findViewById(R.id.edt_2);
        E3 = (EditText) findViewById(R.id.edt_3);
        E4 = (EditText) findViewById(R.id.edt_4);
        timer=findViewById(R.id.time_txt);
        Resend_block=findViewById(R.id.resend_otp_block);
        resend=findViewById(R.id.signup_txt);

        E1.addTextChangedListener(new Google_Otp_Activity.GenericTextWatcher(E1));
        E2.addTextChangedListener(new Google_Otp_Activity.GenericTextWatcher(E2));
        E3.addTextChangedListener(new Google_Otp_Activity.GenericTextWatcher(E3));
        E4.addTextChangedListener(new Google_Otp_Activity.GenericTextWatcher(E4));

        button.setOnClickListener(view -> NUMBER_LOGIN());
        resend.setOnClickListener(view->RESEND_OTP(user));

        CountDownTime();
    }

    private void NUMBER_LOGIN() {

        String enteredOtp = E1.getText().toString() + E2.getText().toString() + E3.getText().toString() + E4.getText().toString();
        if(enteredOtp.equals(otp))
        {


            SAVE_NUMBER();


        }
        else
        {
            showSnackBarMessage("Enter Valid Otp");
            //textView.setVisibility(View.VISIBLE);
        }
    }

    private void SAVE_NUMBER() {
        mSubscriptions.add(networkUtils.getRetrofit().NUMBER_SAVE(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse2,this::handleError));

    }

    private void RESEND_OTP(User user)
    {
        mSubscriptions.add(networkUtils.getRetrofit().GOOGLE_FACEBOOK_OTP(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse2(LoginResponse response) {
        Toast.makeText(Google_Otp_Activity.this, "Login success!", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(constants.TOKEN,token);
        editor.putString(constants.EMAIL,user.getEmail());
        editor.putString(constants.USERNAME,user.getUsername());
        editor.putString(constants.PHONE,user.getMobile());
        editor.putString(constants.TYPE,type);
        editor.apply();
        goToHome();

    }

    private void handleResponse(LoginResponse response) {

        otp=response.getOtp();
        token=response.getToken();

    }

    private void handleError(Throwable error) {



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

    private void goToHome() {
        Intent intent = new Intent(Google_Otp_Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.edt_1:
                    if (text.length() == 1)
                        E2.requestFocus();
                    break;
                case R.id.edt_2:
                    if (text.length() == 1)
                        E3.requestFocus();
                    else if (text.length() == 0)
                        E1.requestFocus();
                    break;
                case R.id.edt_3:
                    if (text.length() == 1)
                        E4.requestFocus();
                    else if (text.length() == 0)
                        E2.requestFocus();
                    break;
                case R.id.edt_4:
                    if (text.length() == 0)
                        E3.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }
}
