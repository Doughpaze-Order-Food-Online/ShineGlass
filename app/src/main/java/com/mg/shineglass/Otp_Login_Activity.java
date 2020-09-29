package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.mg.shineglass.OTP_Acitvities.Number_OTP_Activity;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;

import java.util.Objects;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mg.shineglass.utils.validation.validateFields;
import static com.mg.shineglass.utils.validation.validatePhone;

public class Otp_Login_Activity extends AppCompatActivity {
    TextInputLayout mobile_Layout;
    EditText mobile_EditText;
    private CompositeSubscription mSubscriptions;
    private User user;
    private RelativeLayout button;
    private ViewDialog viewDialog;
    private ImageView backImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.login_with_otp_activity);
        mobile_EditText = findViewById(R.id.user_email_mobile_input_txt);
        mobile_Layout = findViewById(R.id.user_email_mobile);
        mSubscriptions = new CompositeSubscription();
        button = findViewById(R.id.send_otp_btn);

        button.setOnClickListener(view -> NUMBER_LOGIN());

        viewDialog=new ViewDialog(this);
        backImgBtn=findViewById(R.id.back_btn_img);
        backImgBtn.setOnClickListener(v -> finish());
    }

    private void NUMBER_LOGIN() {
        setError();

        String number = Objects.requireNonNull(mobile_EditText.getText()).toString();



        if (!validateFields(number)) {

            mobile_Layout.setError("Phone Number is required!");
            showSnackBarMessage("Phone Number is required!");
            return;

        }

       if(!validatePhone(number))
        {

            mobile_Layout.setError("Enter Valid Phone Number!");
            showSnackBarMessage("Enter Valid Phone Number!");
            return;

        }


            user = new User(number);
            SEND_OTP(user);


    }

    private void SEND_OTP(User user)
    {   viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit().NUMBER_LOGIN(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(LoginResponse response) {
        viewDialog.hideDialog();

        GoToOtp(response.getOtp(),response.getUser(),response.getToken());
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

    private void GoToOtp(String otp,User user,String token){


        Intent intent = new Intent(Otp_Login_Activity.this, Number_OTP_Activity.class);

        intent.putExtra("type","number");
        intent.putExtra("otp",otp);
        intent.putExtra("token", token);
        intent.putExtra("phone", user.getMobile());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("name", user.getUsername());
        intent.putExtra("userType", user.getUserType());
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
