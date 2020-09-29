package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.OTP_Acitvities.Register_OTP_Activity;
import com.mg.shineglass.OTP_Acitvities.Save_Profile_Otp;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import java.io.IOException;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mg.shineglass.utils.validation.validateEmail;
import static com.mg.shineglass.utils.validation.validateFields;
import static com.mg.shineglass.utils.validation.validatePhone;

public class EditDetailsPopUpActivity extends AppCompatActivity {

    private RelativeLayout cancelBtn;
    private EditText name, mobile_no, email;
    private TextInputLayout Tname,Tmobile,Temail;
    private RelativeLayout update,progress;
    private SharedPreferences sharedPreferences;
    private Boolean otp=false,change=false;
    private CompositeSubscription mSubscriptions;
    private User user;
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_details_pop_up);

        this.setFinishOnTouchOutside(false);

        mSubscriptions = new CompositeSubscription();

        cancelBtn=findViewById(R.id.cancel_btn);
        progress=findViewById(R.id.progress);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name=findViewById(R.id.user_name_input_txt);
        mobile_no=findViewById(R.id.user_mobile_input_txt);
        email=findViewById(R.id.user_email_input_txt);

        update=findViewById(R.id.update_btn);

        Tname=findViewById(R.id.user_name);
        Tmobile=findViewById(R.id.user_mobile);
        Temail=findViewById(R.id.user_email);


       sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        name.setText(sharedPreferences.getString(constants.USERNAME,null));
        email.setText(sharedPreferences.getString(constants.EMAIL,null));
        mobile_no.setText(sharedPreferences.getString(constants.PHONE,null));

        update.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            UPDATE();
        });

    }

    private void UPDATE() {

        setError();
        String Euser=name.getText().toString();
        String Ephone=mobile_no.getText().toString();
        String Email=email.getText().toString();

        int err = 0;

        if (!validateFields(Euser)) {

            err++;
            Tname.setError("Name should not be empty !");
        }

        if (!validateEmail(Email)) {

            err++;
            Temail.setError("Email should be valid !");
        }

        if (!validateFields(Ephone)) {

            err++;
            Tmobile.setError("Phone Number should not be empty !");
        }

        if(validateFields(Ephone) && !validatePhone(Ephone))
        {
            err++;
            Tmobile.setError("Enter Valid Phone Number!");
        }

        if(err==0)
        {
            user=new User();
            if(!Euser.toLowerCase().trim().equals(Objects.requireNonNull(sharedPreferences.getString(constants.USERNAME, null)).trim().toLowerCase()))
            {
                user.setUsername(Euser);
                change=true;
            }

            if(!Email.toLowerCase().trim().equals(Objects.requireNonNull(sharedPreferences.getString(constants.EMAIL, null)).trim().toLowerCase()))
            {
                user.setEmail(Email);
                change=true;
            }

            if(!Ephone.toLowerCase().trim().equals(Objects.requireNonNull(sharedPreferences.getString(constants.PHONE, null)).trim().toLowerCase()))
            {
                user.setMobile(Ephone);
                otp=true;
                change=true;
            }

            if(otp)
            {
                progress.setVisibility(View.VISIBLE);

                name.setEnabled(false);
                email.setEnabled(false);
                mobile_no.setEnabled(false);
                update.setEnabled(false);
                cancelBtn.setEnabled(false);
                mSubscriptions.add(
                        networkUtils.getRetrofit()
                                .GOOGLE_FACEBOOK_OTP(user)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(this::handleResponse,this::handleError));
            }
            else
            {

               if(change)
               {
                   name.setEnabled(false);
                   email.setEnabled(false);
                   mobile_no.setEnabled(false);
                   update.setEnabled(false);
                   cancelBtn.setEnabled(false);
                   SharedPreferences sharedPreferences = PreferenceManager
                           .getDefaultSharedPreferences(this);
                   progress.setVisibility(View.VISIBLE);
                   mSubscriptions.add(
                           networkUtils.getRetrofit(sharedPreferences.getString("token", null))
                                   .SAVE_PROFILE_DETAILS(user)
                                   .observeOn(AndroidSchedulers.mainThread())
                                   .subscribeOn(Schedulers.io())
                                   .subscribe(this::handleResponse2,this::handleError));
               }
               else
               {
                   Toast.makeText(this, "No Changes made!", Toast.LENGTH_SHORT).show();
                   finish();
               }
            }
        }
    }

    private void setError()
    {
        Tmobile.setError(null);
        Temail.setError(null);
        Tname.setError(null);
    }

    private void handleResponse(LoginResponse response) {

        progress.setVisibility(View.GONE);
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

        GoToOtp(response.getOtp());
    }

    private void handleResponse2(LoginResponse response) {
        progress.setVisibility(View.GONE);
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void handleError(Throwable error) {

        progress.setVisibility(View.GONE);

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

            Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();
        }
    }




    private void GoToOtp(String otp){

        Intent intent = new Intent(EditDetailsPopUpActivity.this, Save_Profile_Otp.class);
        intent.putExtra("otp",otp);
        intent.putExtra("name", user.getUsername());
        intent.putExtra("phone", user.getMobile());
        intent.putExtra("email", user.getEmail());
        startActivity(intent);
        finish();


    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        name.setText(sharedPreferences.getString(constants.USERNAME,null));
        email.setText(sharedPreferences.getString(constants.EMAIL,null));
        mobile_no.setText(sharedPreferences.getString(constants.PHONE,null));
    }
}