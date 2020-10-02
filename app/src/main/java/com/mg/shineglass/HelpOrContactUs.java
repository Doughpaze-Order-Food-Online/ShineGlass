package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HelpOrContactUs extends AppCompatActivity {

    private ImageView BackBtnImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_or_contact_us);
        BackBtnImg=findViewById(R.id.back_btn_img);
        BackBtnImg.setOnClickListener(view -> finish());
    }
}