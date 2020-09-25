package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

    private TextInputLayout password_Layout,rpassword_layout;

    private EditText password_EditText,rpassword_EditText;
    private CompositeSubscription mSubscriptions;
    private RelativeLayout reset,cancel;
    private ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        mSubscriptions = new CompositeSubscription();

        password_Layout=findViewById(R.id.new_password);
        rpassword_layout=findViewById(R.id.reenter_new_password);

        password_EditText=findViewById(R.id.new_password_input_txt);
        rpassword_EditText=findViewById(R.id.reenter_new_password_input_txt);

        reset=findViewById(R.id.update_btn);
        reset.setOnClickListener(view->RESET());

        cancel=findViewById(R.id.cancel_btn);

        cancel.setOnClickListener(v->{
            finish();
        });

        viewDialog = new ViewDialog(this);
    }

    private void RESET() {

        setError();
        String password = Objects.requireNonNull(password_EditText.getText()).toString();
        String password2 = Objects.requireNonNull(rpassword_EditText.getText()).toString();
        int err=0;

        if (!validateFields(password)) {

            err++;
            password_Layout.setError("Password should not be empty !");
        }
        else if(!VALIDATE_PASSWORD(password))
        {
            err++;
            password_Layout.setError("Password should have atleast:\n6 characters \n1 uppercase\n1 special character \n1 number");
        }

        if (!validateFields(password2)) {

            err++;
            rpassword_layout.setError("Confirm Password should not be empty !");
        }

        if(!password.equals(password2) && validateFields(password) && validateFields(password2)  )
        {
            err++;
            rpassword_layout.setError("Passwords do not match !");
        }


        if (err==0) {

                User user=new User();
                user.setPassword(password);

            viewDialog.showDialog();

            mSubscriptions.add(
                    networkUtils.getRetrofit().RESET_PASSWORD(user)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(this::handleResponse,this::handleError));
        }
    }


    private void handleResponse(BasicResponse response) {

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



    private void setError()
    {
        password_Layout.setError(null);
        rpassword_layout.setError(null);
    }
}