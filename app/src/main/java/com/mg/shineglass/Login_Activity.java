package com.mg.shineglass;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class Login_Activity extends Activity {


    TextInputLayout userfield_Layout, email_Layout;

    EditText userfield_EditText, email_EditText;

    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }


    private void initViews() {

        userfield_Layout= findViewById(R.id.);
        email_Layout=findViewById(R.id.user_email);

        userfield_EditText=findViewById(R.id.user_name_input_txt);
        email_EditText=findViewById(R.id.user_email_input_txt);


        register=findViewById(R.id.login_btn);
        register.setOnClickListener(view->Register());


    }

    private void Register() {
    }
}
