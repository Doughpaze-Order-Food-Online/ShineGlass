package com.mg.shineglass;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputLayout;

public class Register_Activity extends Activity {

    TextInputLayout username_Layout,
            email_Layout,mobile_Layout,
            password_Layout,rpassword_layout;

    EditText username_EditText,
            email_EditText,mobile_EditText,
            password_EditText,rpassword_EditText;

    RelativeLayout register;

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
    }
}
