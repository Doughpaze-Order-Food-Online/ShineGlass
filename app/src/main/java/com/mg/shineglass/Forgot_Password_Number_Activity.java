package com.mg.shineglass;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.mg.shineglass.OTP_Acitvities.Forgot_Password_Otp;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import java.util.Objects;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import static com.mg.shineglass.utils.validation.validateFields;
import static com.mg.shineglass.utils.validation.validatePhone;

public class Forgot_Password_Number_Activity extends AppCompatActivity {
    TextInputLayout mobile_Layout;
    EditText mobile_EditText;
    private CompositeSubscription mSubscriptions;
    private User user;
    private RelativeLayout button;
    private TextView title;
    private ViewDialog viewDialog;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_with_otp_activity);

        mobile_EditText = findViewById(R.id.user_email_mobile_input_txt);
        mobile_Layout = findViewById(R.id.user_email_mobile);
        mSubscriptions = new CompositeSubscription();
        button = findViewById(R.id.send_otp_btn);
        title=findViewById(R.id.enter_number);

        title.setText("Enter Registered Mobile Number");

        button.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            SEND_OTP();});

        viewDialog=new ViewDialog(this);
    }

    private void SEND_OTP() {
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

        viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit()
                .FORGOT_PASSWORD_OTP(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));



    }



    private void handleResponse(LoginResponse response) {

        viewDialog.hideDialog();

        Intent intent = new Intent(Forgot_Password_Number_Activity.this, Forgot_Password_Otp.class);
        intent.putExtra("otp",response.getOtp());
        intent.putExtra("_id",response.getUser().get_id());
        intent.putExtra("number",mobile_EditText.getText().toString());
        startActivity(intent);
        finish();
    }

    private void handleError(Throwable error) {


        viewDialog.hideDialog();
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

