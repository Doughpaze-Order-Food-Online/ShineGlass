package com.mg.shineglass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.mg.shineglass.OTP_Acitvities.Google_Otp_Activity;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;

import java.util.Objects;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mg.shineglass.utils.validation.validateFields;
import static com.mg.shineglass.utils.validation.validatePhone;

public class Google_Enter_Number extends AppCompatActivity {
    TextInputLayout mobile_Layout;
    EditText mobile_EditText;
    private CompositeSubscription mSubscriptions;
    private User user;
    private RelativeLayout button;
    private String token,type,email,Number,username;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_with_otp_activity);

        Intent i=getIntent();
        token=i.getStringExtra("token");
        type=i.getStringExtra("type");
        email=i.getStringExtra("email");
        username=i.getStringExtra("name");
        mobile_EditText = findViewById(R.id.user_email_mobile_input_txt);
        mobile_Layout = findViewById(R.id.user_email_mobile);
        mSubscriptions = new CompositeSubscription();
        button = findViewById(R.id.send_otp_btn);
        title=findViewById(R.id.enter_number);

        title.setText("Enter Mobile Number");

        button.setOnClickListener(view -> SEND_OTP());
    }

    private void SEND_OTP() {
        setError();

        String number = Objects.requireNonNull(mobile_EditText.getText()).toString();


        int err = 0;

        if (!validateFields(number)) {

            err++;
            mobile_Layout.setError("Phone Number is required");

        }
        else if(!validatePhone(number))
        {
            err++;
            mobile_Layout.setError("Enter Valid Phone Number!");

        }

        if (err == 0) {
            Number=number;
            user = new User(number);
            user.setEmail(email);

            mSubscriptions.add(networkUtils.getRetrofit().GOOGLE_FACEBOOK_OTP(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse,this::handleError));

        } else {
            showSnackBarMessage("Enter Valid Details !");

        }

    }



    private void handleResponse(LoginResponse response) {


        GoToOtp(response.getOtp(),token);
    }

    private void handleError(Throwable error) {



        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody,BasicResponse.class);
                showSnackBarMessage(response.getMessage());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error",error.toString());
            showSnackBarMessage( "Network Error !");

        }
    }

    private void GoToOtp(String otp,String token){

        Intent intent = new Intent(Google_Enter_Number.this, Google_Otp_Activity.class);
        intent.putExtra("type",type);
        intent.putExtra("otp",otp);
        intent.putExtra("token", token);
        intent.putExtra("phone", Number);
        intent.putExtra("email", email);
        intent.putExtra("name", username);
        startActivity(intent);
        finish();
    }




    private void setError() {

        mobile_Layout.setError(null);

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



}

