package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;

import java.io.IOException;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mg.shineglass.utils.validation.VALIDATE_PASSWORD;
import static com.mg.shineglass.utils.validation.validateFields;

public class ResetPassword extends AppCompatActivity {

    private TextInputLayout password_Layout,rpassword_layout,old_layout;

    private EditText password_EditText,rpassword_EditText,old_EditText;
    private CompositeSubscription mSubscriptions;
    private RelativeLayout cancel,progress;
    private Button reset;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_fragment);
        mSubscriptions = new CompositeSubscription();

        password_Layout=findViewById(R.id.new_password);
        rpassword_layout=findViewById(R.id.reenter_new_password);
        old_layout=findViewById(R.id.old_password);

        password_EditText=findViewById(R.id.new_password_input_txt);
        rpassword_EditText=findViewById(R.id.reenter_new_password_input_txt);
        old_EditText=findViewById(R.id.old_password_input_txt);
        progress=findViewById(R.id.progress);

        reset=findViewById(R.id.change_btn);
        reset.setOnClickListener(view->{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            RESET();});

        cancel=findViewById(R.id.cancel_btn);

        cancel.setOnClickListener(v->{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            finish();
        });


    }



    private void RESET() {

        setError();
        String old=Objects.requireNonNull(old_EditText.getText()).toString();
        String password = Objects.requireNonNull(password_EditText.getText()).toString();
        String password2 = Objects.requireNonNull(rpassword_EditText.getText()).toString();


        if (!validateFields(old)) {


            old_layout.setError("Old Password should not be empty !");
            return;
        }

        if (!validateFields(password)) {


            password_Layout.setError("New Password should not be empty !");
            return;
        }

        if(!VALIDATE_PASSWORD(password))
        {

            password_Layout.setError("Password should have atleast:\n6 characters \n1 uppercase\n1 special character \n1 number");
            return;
        }


        if (!validateFields(password2)) {


            rpassword_layout.setError("Confirm Password should not be empty !");
            return ;
        }

        if(!password.equals(password2) && validateFields(password) && validateFields(password2)  )
        {

            rpassword_layout.setError("Passwords do not match !");
            return;
        }

        if(password.equals(old))
        {
            password_Layout.setError("New Password cannot be same as old password");
            return;
        }


                User user=new User();
                user.setPassword(password);
                user.setOld_password(old);

        progress.setVisibility(View.VISIBLE);

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);

            mSubscriptions.add(
                    networkUtils.getRetrofit(sharedPreferences.getString("token", null)).RESET_PASSWORD(user)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(this::handleResponse,this::handleError));

    }


    private void handleResponse(BasicResponse response) {
        progress.setVisibility(View.GONE);

        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
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



    private void setError()
    {   old_layout.setError(null);
        password_Layout.setError(null);
        rpassword_layout.setError(null);
    }
}