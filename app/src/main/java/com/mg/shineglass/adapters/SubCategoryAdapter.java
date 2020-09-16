package com.mg.shineglass.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mg.shineglass.MainActivity;
import com.mg.shineglass.NewRequestActivity;
import com.mg.shineglass.R;
import com.mg.shineglass.Subcategory_Activity;
import com.mg.shineglass.models.Category;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryItemHolder> {

    private List<String> list;
    private Context context;
    private String category;

    public SubCategoryAdapter(List<String> list, Context context,String category) {
        this.list = list;
        this.context = context;
        this.category=category;
    }


    @NonNull
    @Override
    public SubCategoryAdapter.SubCategoryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.glass_item, parent, false);
        return new SubCategoryAdapter.SubCategoryItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryAdapter.SubCategoryItemHolder SubcategoryItemHolder, int i) {
        String name=list.get(i);

        SubcategoryItemHolder.name.setText(name);




        SubcategoryItemHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("subcategory", name);
                bundle.putString("category", category);
                Fragment fragment = new NewRequestActivity();
                fragment.setArguments(bundle);
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    class SubCategoryItemHolder extends RecyclerView.ViewHolder {

        private TextView name;


        SubCategoryItemHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name_txt);


        }
    }

}

