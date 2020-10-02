package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

public class TermsAndPolicies extends AppCompatActivity {

    private ImageView BackBtnImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_policies);
        BackBtnImg=findViewById(R.id.back_btn_img);
        BackBtnImg.setOnClickListener(view -> finish());
    }
}