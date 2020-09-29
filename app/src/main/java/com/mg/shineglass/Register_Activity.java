package com.mg.shineglass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.OTP_Acitvities.Register_OTP_Activity;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;

import java.io.IOException;
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

public class Register_Activity extends AppCompatActivity {

    private TextInputLayout username_Layout,
            email_Layout,mobile_Layout,
            password_Layout,rpassword_layout;

    private EditText username_EditText,
            email_EditText,mobile_EditText,
            password_EditText,rpassword_EditText;
    private ViewDialog viewDialog;

    private CompositeSubscription mSubscriptions;
    private User user;
    private RelativeLayout register;
    private long mLastClickTime = 0;
    private ImageView backImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.create_account_activity);

        backImgBtn=findViewById(R.id.back_btn_img);
        backImgBtn.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            finish();});
        initViews();
    }

    private void initViews() {

        mSubscriptions = new CompositeSubscription();

        username_Layout= findViewById(R.id.user_name);
        email_Layout=findViewById(R.id.user_email);
        mobile_Layout=findViewById(R.id.user_mobile);
        password_Layout=findViewById(R.id.user_password);
        rpassword_layout=findViewById(R.id.user_re_password);


        username_EditText=findViewById(R.id.user_name_input_txt);
        email_EditText=findViewById(R.id.user_email_input_txt);
        password_EditText=findViewById(R.id.password_input_txt);
        mobile_EditText=findViewById(R.id.user_mobile_input_txt);
        rpassword_EditText=findViewById(R.id.re_password_input_txt);

        register=findViewById(R.id.signUp_btn);
        register.setOnClickListener(view->{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Register();});

        viewDialog = new ViewDialog(this);


    }


    private void Register() {
        setError();


        String username = Objects.requireNonNull(username_EditText.getText()).toString();
        String email = Objects.requireNonNull(email_EditText.getText()).toString();
        String password = Objects.requireNonNull(password_EditText.getText()).toString();
        String password2 = Objects.requireNonNull(rpassword_EditText.getText()).toString();
        String mobile_no = Objects.requireNonNull(mobile_EditText.getText()).toString();




        if (!validateFields(username)) {


            username_Layout.setError("Name should not be empty !");
            showSnackBarMessage("Name should not be empty !");
            return;
        }

        if (!validateEmail(email)) {


            email_Layout.setError("Email should be valid !");
            showSnackBarMessage("Email should be valid !");
            return;
        }

        if (!validateFields(password)) {


           password_Layout.setError("Password should not be empty !");
            showSnackBarMessage("Password should not be empty !");
            return;
        }

         if(!VALIDATE_PASSWORD(password))
        {

            password_Layout.setError("Password should have atleast:\n6 characters \n1 uppercase\n1 special character \n1 number");
            showSnackBarMessage("Enter Valid Password!");
            return;
        }

        if (!validateFields(password2)) {


            rpassword_layout.setError("Confirm Password should not be empty !");
            showSnackBarMessage("Confirm Password should not be empty !");
            return;
        }


        if (!validateFields(mobile_no)) {


            mobile_Layout.setError("Phone Number should not be empty !");
            showSnackBarMessage("Phone Number should not be empty !");
            return;
        }

        if(validateFields(mobile_no) && !validatePhone(mobile_no))
        {

            mobile_Layout.setError("Enter Valid Phone Number!");
            showSnackBarMessage("Enter Valid Phone Number!");
            return;
        }




        if(!password.equals(password2) && validateFields(password) && validateFields(password2)  )
        {

           rpassword_layout.setError("Passwords do not match !");
            showSnackBarMessage("Passwords do not match !");
            return;
        }



            user = new User(username,email,password,mobile_no);
            SEND_OTP(user);


    }


    private void SEND_OTP(User u) {

        viewDialog.showDialog();

        mSubscriptions.add(
                networkUtils.getRetrofit().REGISTER_OTP(u)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(BasicResponse response) {

        viewDialog.hideDialog();
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

        GoToOtp(response.getOtp(),user);
    }

    private void handleError(Throwable error) {

        viewDialog.hideDialog();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody,BasicResponse.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }




    private void GoToOtp(String otp,User user){

        Intent intent = new Intent(Register_Activity.this, Register_OTP_Activity.class);
        intent.putExtra("type","local");
        intent.putExtra("otp",otp);
        intent.putExtra("name", user.getUsername());
        intent.putExtra("phone", user.getMobile());
        intent.putExtra("password", user.getPassword());
        intent.putExtra("email", user.getEmail());
        startActivity(intent);
        finish();


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
