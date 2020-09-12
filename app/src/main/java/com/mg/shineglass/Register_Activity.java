package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;

import java.util.Objects;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mg.shineglass.utils.validation.VALIDATE_PASSWORD;
import static com.mg.shineglass.utils.validation.validateEmail;
import static com.mg.shineglass.utils.validation.validateFields;
import static com.mg.shineglass.utils.validation.validatePhone;

public class Register_Activity extends Activity {

    private TextInputLayout username_Layout,
            email_Layout,mobile_Layout,
            password_Layout,rpassword_layout;

    private EditText username_EditText,
            email_EditText,mobile_EditText,
            password_EditText,rpassword_EditText;

    private CompositeSubscription mSubscriptions;
    private User user;
    private RelativeLayout register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        initViews();
    }

    private void initViews() {

        username_Layout= findViewById(R.id.user_name);
        email_Layout=findViewById(R.id.user_email);
        mobile_Layout=findViewById(R.id.user_mobile);
        password_Layout=findViewById(R.id.user_password);
        rpassword_layout=findViewById(R.id.user_re_password);


        username_EditText=findViewById(R.id.user_name_input_txt);
        email_EditText=findViewById(R.id.user_email_input_txt);
        password_EditText=findViewById(R.id.password_input_txt);
        mobile_EditText=findViewById(R.id.user_email_mobile_input_txt);
        rpassword_EditText=findViewById(R.id.re_password_input_txt);

        register=findViewById(R.id.signUp_btn);
        register.setOnClickListener(view->Register());


    }


    private void Register() {
        setError();


        String username = Objects.requireNonNull(username_EditText.getText()).toString();
        String email = Objects.requireNonNull(email_EditText.getText()).toString();
        String password = Objects.requireNonNull(password_EditText.getText()).toString();
        String password2 = Objects.requireNonNull(rpassword_EditText.getText()).toString();
        String mobile_no = Objects.requireNonNull(mobile_EditText.getText()).toString();



        int err = 0;

        if (!validateFields(username)) {

            err++;
            username_Layout.setError("Name should not be empty !");
        }

        if (!validateEmail(email)) {

            err++;
            email_Layout.setError("Email should be valid !");
        }

        if (!validateFields(password)) {

            err++;
           password_Layout.setError("Password should not be empty !");
        }
        else if(!VALIDATE_PASSWORD(password))
        {
            err++;
            password_Layout.setError("Password should have atleast 6 characters with 1 uppercase, 1 special character and 1 number");
        }

        if (!validateFields(password2)) {

            err++;
            rpassword_layout.setError("Confirm Password should not be empty !");
        }


        if (!validateFields(mobile_no)) {

            err++;
            mobile_Layout.setError("Phone Number should not be empty !");
        }

        if(validateFields(mobile_no) && !validatePhone(mobile_no))
        {
            err++;
            mobile_Layout.setError("Enter Valid Phone Number!");
        }




        if(!password.equals(password2) && validateFields(password) && validateFields(password2)  )
        {
            err++;
           rpassword_layout.setError("Passwords do not match !");
        }


        if (err == 0) {
            user = new User(username,email,password,password2);
            SEND_OTP(user);

        } else {

            showSnackBarMessage("Enter Valid Details !");
        }
    }


    private void SEND_OTP(User u) {

        mSubscriptions.add(networkUtils.getRetrofit().REGISTER_OTP(u)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(BasicResponse response) {


        Toast.makeText(this, "One Time Password Sent Your Mobile Number!", Toast.LENGTH_SHORT).show();

        GoToOtp(response.getOtp(),user);
    }

    private void handleError(Throwable error) {



        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                assert ((HttpException) error).response().errorBody() != null;
                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody,BasicResponse.class);
                showSnackBarMessage(response.getMessage());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error",error.toString());
            showSnackBarMessage("Network Error !");
        }
    }





    private void GoToOtp(String otp,User user){

        Intent intent = new Intent(Register_Activity.this, OTP_Activity.class);
        intent.putExtra("type","local");
        intent.putExtra("otp",otp);
        intent.putExtra("name", user.getUsername());
        intent.putExtra("phone", user.getMobile());
        intent.putExtra("password", Objects.requireNonNull(password_EditText.getText()).toString());
        intent.putExtra("email", user.getEmail());
        startActivity(intent);

    }




    private void setError() {

        username_Layout.setError(null);
        email_Layout.setError(null);
        mobile_Layout.setError(null);
        password_Layout.setError(null);
        rpassword_layout.setError(null);
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
        Snackbar.make(getRootView(),message,Snackbar.LENGTH_SHORT).show();

    }
}
