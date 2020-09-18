package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.models.BasicResponse;
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

import static com.mg.shineglass.utils.validation.validateFields;

public class Login_Activity extends AppCompatActivity {


   private TextInputLayout userfield_Layout, password_Layout;
    private EditText userfield_EditText, password_EditText;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    private ImageView backImgBtn;

    RelativeLayout register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        backImgBtn=findViewById(R.id.back_btn_img);
        backImgBtn.setOnClickListener(v -> finish());
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
    }


    private void initViews() {

        userfield_Layout= findViewById(R.id.user_email_mobile);
        password_Layout=findViewById(R.id.user_password);

        userfield_EditText=findViewById(R.id.user_email_mobile_input_txt);
        password_EditText=findViewById(R.id.password_input_txt);


        register=findViewById(R.id.login_btn);
        register.setOnClickListener(view->Login());

        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();


    }

    private void Login() {

        setError();

        String email = userfield_EditText.getText().toString();
        String password = password_EditText.getText().toString();

        int err = 0;

        if (!validateFields(email)) {
            err++;
            userfield_Layout.setError("Email/Number should not be empty !");
        }

        if (!validateFields(password)) {

            err++;
            password_Layout.setError("Password should not be empty !");
        }

        if (err == 0) {

            loginProcess(new User(email,password));



        } else {

            showSnackBarMessage("Enter Valid Details !");
        }
    }

    private void setError() {

        userfield_Layout.setError(null);
        password_Layout.setError(null);
    }

    private void loginProcess(User user) {

        mSubscriptions.add(networkUtils.getRetrofit().LOGIN(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }

    private void handleResponse(LoginResponse response) {



        Toast.makeText(Login_Activity.this, response.getMessage(), Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("type","local");
        editor.putString(constants.TOKEN,response.getToken());
        editor.putString(constants.EMAIL,response.getUser().getEmail());
        editor.putString(constants.USERNAME,response.getUser().getUsername());
        editor.putString(constants.PHONE,response.getUser().getMobile());
        editor.apply();

        userfield_EditText.setText(null);
        password_EditText.setText(null);

        Intent intent = new Intent(Login_Activity.this,MainActivity.class );
        startActivity(intent);
        finish();

    }

    private void handleError(Throwable error) {



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

















    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
    }

}
