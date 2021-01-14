package com.mg.shineglass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.adapters.InfoAdapter;
import com.mg.shineglass.adapters.MyRequestAdapter;
import com.mg.shineglass.models.MyQuotation;
import com.mg.shineglass.models.Quotation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyRequestDetailsPopUpActivity extends AppCompatActivity {
    ImageView closeImg;
    RecyclerView rvItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request_details_pop_up);
        Intent intent=getIntent();
        Gson gson = new Gson();
        List<Quotation> list = new ArrayList<>();
        Type type = new TypeToken<List<Quotation>>() {
        }.getType();
        list = gson.fromJson(intent.getStringExtra("quotation"), type);


        rvItem=findViewById(R.id.quotation_details_rv);

        InfoAdapter infoAdapter = new InfoAdapter(list);
        LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
        rvItem.setLayoutManager(LinearLayout);
        rvItem.setAdapter(infoAdapter);

        closeImg = findViewById(R.id.close_img_btn);
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}