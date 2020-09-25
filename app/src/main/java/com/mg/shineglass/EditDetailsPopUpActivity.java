package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
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
    private TextView name, mobile_no, email;
    private TextInputLayout Tname,Tmobile,Temail;
    private RelativeLayout update;
    private SharedPreferences sharedPreferences;
    private Boolean otp=false,change=false;
    private CompositeSubscription mSubscriptions;
    private ViewDialog viewDialog;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_details_pop_up);

        this.setFinishOnTouchOutside(false);

        mSubscriptions = new CompositeSubscription();

        cancelBtn=findViewById(R.id.cancel_btn);
        viewDialog=new ViewDialog(this);

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
                viewDialog.showDialog();
                mSubscriptions.add(
                        networkUtils.getRetrofit().GOOGLE_FACEBOOK_OTP(user)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(this::handleResponse,this::handleError));
            }
            else
            {

               if(change)
               {
                   SharedPreferences sharedPreferences = PreferenceManager
                           .getDefaultSharedPreferences(this);
                   viewDialog.showDialog();
                   mSubscriptions.add(
                           networkUtils.getRetrofit(sharedPreferences.getString("token", null)).SAVE_PROFILE_DETAILS(user)
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

        viewDialog.hideDialog();
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

        GoToOtp(response.getOtp(),user);
    }

    private void handleResponse2(BasicResponse response) {
        viewDialog.hideDialog();
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
        finish();
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

            Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();
        }
    }




    private void GoToOtp(String otp,User user){

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