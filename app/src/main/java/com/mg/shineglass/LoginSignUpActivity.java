package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoginSignUpActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_activity);

        RelativeLayout login = findViewById(R.id.login_btn);
        Button otp_login = findViewById(R.id.login_with_otp_btn);
        TextView sign_up = findViewById(R.id.signup_txt);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginSignUpActivity.this,Login_Activity.class);
                startActivity(i);
            }
        });

        otp_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginSignUpActivity.this,Otp_Login_Activity.class);
                startActivity(i);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginSignUpActivity.this,Register_Activity.class);
                startActivity(i);
            }
        });
    }
}