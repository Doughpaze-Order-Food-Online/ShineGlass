package com.mg.shineglass.OTP_Acitvities;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.MainActivity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class Register_OTP_Activity extends AppCompatActivity {

    private User user;
    private String otp,type;
    private EditText E1;
    private EditText E2;
    private EditText E3;
    private EditText E4;
    private RelativeLayout button;
    private TextView timer, resend;
    private CompositeSubscription mSubscriptions;
    private RelativeLayout Resend_block;
    private ImageView backImgBtn;
    private ViewDialog viewDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_otp_activity);
        backImgBtn=findViewById(R.id.back_btn_img);
        backImgBtn.setOnClickListener(v -> finish());

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
        timer=findViewById(R.id.time_txt);
        Resend_block=findViewById(R.id.resend_otp_block);
        resend=findViewById(R.id.signup_txt);

        E1.addTextChangedListener(new GenericTextWatcher(E1));
        E2.addTextChangedListener(new GenericTextWatcher(E2));
        E3.addTextChangedListener(new GenericTextWatcher(E3));
        E4.addTextChangedListener(new GenericTextWatcher(E4));

        button.setOnClickListener(view -> register());
        resend.setOnClickListener(view->SEND_OTP(user));
        viewDialog = new ViewDialog(this);

        CountDownTime();
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
                default:E1.requestFocus();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }

    private void register() {

        String enteredOtp = E1.getText().toString() + E2.getText().toString() + E3.getText().toString() + E4.getText().toString();

        if (enteredOtp.equals(otp)) {

            RegisterUser(user);
        } else {
            showSnackBarMessage("Enter Valid Otp");
            //textView.setVisibility(View.VISIBLE);

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

        viewDialog.showDialog();

        mSubscriptions.add(networkUtils.getRetrofit().REGISTER(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(LoginResponse response) {

        viewDialog.hideDialog();
        Toast.makeText(Register_OTP_Activity.this, "SignUp success!", Toast.LENGTH_SHORT).show();
        showSnackBarMessage(response.getMessage());
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("type","local");
        editor.putString(constants.TOKEN,response.getToken());
        editor.putString(constants.EMAIL,response.getUser().getEmail());
        editor.putString(constants.USERNAME,response.getUser().getUsername());
        editor.putString(constants.PHONE,response.getUser().getMobile());
        editor.putString(constants.TYPE,response.getType());
        editor.putString(constants.USER_TYPE,response.getUser().getUserType());
        editor.apply();
        goToHome();

    }

    private void handleError(Throwable error) {
        viewDialog.hideDialog();


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


    private void SEND_OTP(User u) {
        viewDialog.showDialog();
        mSubscriptions.add(
                networkUtils.getRetrofit().REGISTER_OTP(u)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse2,this::handleError));
    }

    private void handleResponse2(BasicResponse response) {
        viewDialog.hideDialog();

        Toast.makeText(this, "One Time Password Sent Your Mobile Number!", Toast.LENGTH_SHORT).show();
        otp=response.getOtp();


    }
}

