package com.mg.shineglass;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import java.io.IOException;
import java.util.Objects;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import static com.mg.shineglass.utils.validation.VALIDATE_PASSWORD;
import static com.mg.shineglass.utils.validation.validateFields;


public class Forgot_Password_Activity extends AppCompatActivity {

    private TextInputLayout password_Layout,rpassword_layout;

    private EditText password_EditText,rpassword_EditText;
    private CompositeSubscription mSubscriptions;
    private RelativeLayout cancel;
    private RelativeLayout progress;
    private RelativeLayout reset;
    private String _id;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        Intent i=getIntent();
        _id=i.getStringExtra("_id");

        mSubscriptions = new CompositeSubscription();

        password_Layout=findViewById(R.id.new_password);
        rpassword_layout=findViewById(R.id.reenter_new_password);


        password_EditText=findViewById(R.id.new_password_input_txt);
        rpassword_EditText=findViewById(R.id.reenter_new_password_input_txt);

        progress=findViewById(R.id.progress);

        reset=findViewById(R.id.update_btn);
        reset.setOnClickListener(view->{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            RESET();
        });

        cancel=findViewById(R.id.cancel_btn);

        cancel.setOnClickListener(v->{
            finish();
        });


    }


    private void RESET() {

        setError();

        String password = Objects.requireNonNull(password_EditText.getText()).toString();
        String password2 = Objects.requireNonNull(rpassword_EditText.getText()).toString();



        if (!validateFields(password)) {


            password_Layout.setError("New Password should not be empty !");
            return;
        }

        if(!VALIDATE_PASSWORD(password))
        {

            password_Layout.setError("Password should have atleast :\n6 characters \n1 uppercase\n1 special character \n1 number");
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




        User user=new User();
        user.setPassword(password);
        user.set_id(_id);


        progress.setVisibility(View.VISIBLE);
        mSubscriptions.add(networkUtils.getRetrofit()
                        .FORGOT_PASSWORD(user)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse,this::handleError));

    }


    private void handleResponse(BasicResponse response) {
        progress.setVisibility(View.GONE);
        Toast.makeText(this, "New Password Set Successfully!", Toast.LENGTH_SHORT).show();
        Intent i=new Intent(Forgot_Password_Activity.this,LoginSignUpActivity.class);
        startActivity(i);
        finish();
    }

    private void handleError(Throwable error) {

        Log.e("error",error.toString());
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
    {
        password_Layout.setError(null);
        rpassword_layout.setError(null);
    }
}