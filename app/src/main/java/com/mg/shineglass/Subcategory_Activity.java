package com.mg.shineglass;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.adapters.SubCategoryAdapter;
import com.mg.shineglass.models.Category;
import com.mg.shineglass.models.subcategory;

import java.lang.reflect.Type;
import java.util.List;

public class Subcategory_Activity extends FragmentActivity {
    private List<subcategory> subcategory;
    private String category;
    private TextView type;
    private RecyclerView rvItem;
    private ImageView BackBtnImg;

    public Subcategory_Activity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.categories_fragment);


        Intent i=getIntent();
        Gson gson=new Gson();
        Type t=new TypeToken<List<subcategory>>(){}.getType();
        subcategory= gson.fromJson(i.getStringExtra("subcategory"),t);
        category=i.getStringExtra("category");

        type=findViewById(R.id.category_name_txt);
        type.setText(category);

        rvItem=findViewById(R.id.categories_container);

        SubCategoryAdapter subCategoryAdapter=new SubCategoryAdapter(subcategory,this,category);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        rvItem.setLayoutManager(gridLayoutManager);
        rvItem.setAdapter(subCategoryAdapter);

        BackBtnImg=findViewById(R.id.back_btn_img);
        BackBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
