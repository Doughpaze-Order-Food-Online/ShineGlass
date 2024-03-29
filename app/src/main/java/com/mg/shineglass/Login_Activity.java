package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import static com.mg.shineglass.utils.validation.validateFields;

public class Login_Activity extends AppCompatActivity {


   private TextInputLayout userfield_Layout, password_Layout;
    private EditText userfield_EditText, password_EditText;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ViewDialog viewDialog;
    private ImageView backImgBtn;
    private TextView forgot;
    private long mLastClickTime = 0;

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
        viewDialog=new ViewDialog(this);
        userfield_Layout= findViewById(R.id.user_email_mobile);
        password_Layout=findViewById(R.id.user_password);
        forgot=findViewById(R.id.forgot_password_txt);

        forgot.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent intent=new Intent(Login_Activity.this,Forgot_Password_Number_Activity.class);
            startActivity(intent);
        });

        userfield_EditText=findViewById(R.id.user_email_mobile_input_txt);
        password_EditText=findViewById(R.id.password_input_txt);


        register=findViewById(R.id.login_btn);
        register.setOnClickListener(view->{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Login();});

        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();


    }

    private void Login() {

        setError();

        String email = userfield_EditText.getText().toString();
        String password = password_EditText.getText().toString();



        if (!validateFields(email)) {

            userfield_Layout.setError("Email/Number should not be empty !");
            showSnackBarMessage("Email/Number should not be empty !");
            return;
        }

        if (!validateFields(password)) {


            password_Layout.setError("Password should not be empty !");
            showSnackBarMessage("Password should not be empty !");
            return;
        }

            loginProcess(new User(email,password));
    }

    private void setError() {

        userfield_Layout.setError(null);
        password_Layout.setError(null);
    }

    private void loginProcess(User user) {

        viewDialog.showDialog();

        mSubscriptions.add(networkUtils.getRetrofit().LOGIN(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }

    private void handleResponse(LoginResponse response) {


    viewDialog.hideDialog();
        Toast.makeText(Login_Activity.this, response.getMessage(), Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("type","local");
        editor.putString(constants.TOKEN,response.getToken());
        editor.putString(constants.EMAIL,response.getUser().getEmail());
        editor.putString(constants.USERNAME,response.getUser().getUsername());
        editor.putString(constants.PHONE,response.getUser().getMobile());
        editor.putString(constants.USER_TYPE,response.getUser().getUserType());
        editor.apply();

        userfield_EditText.setText(null);
        password_EditText.setText(null);

        Intent intent = new Intent(Login_Activity.this, response.getUser().getUserType().equals("user")?MainActivity.class:DeliveryBoyMainActivity.class);
        startActivity(intent);
        finish();

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
