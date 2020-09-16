package com.mg.shineglass.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.mg.shineglass.HomePageFragment;
import com.mg.shineglass.MainActivity;
import com.mg.shineglass.NewRequestActivity;
import com.mg.shineglass.R;
import com.mg.shineglass.Subcategory_Activity;
import com.mg.shineglass.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryItemHolder>  {

    private List<Category> list,main;
    private Context context;
    private List<String> sub;

    public CategoryAdapter(List<Category> list,Context context,List<Category> main) {
        this.list=list;
        this.context=context;
        this.main=main;
    }


    @NonNull
    @Override
    public CategoryAdapter.CategoryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapter.CategoryItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryItemHolder categoryItemHolder, int i) {
        Category category=list.get(i);

            categoryItemHolder.name.setText(category.getCategory());

        Glide
                .with(categoryItemHolder.itemView.getContext())
                .load(category.getResourceID())
//                .thumbnail(Glide.with(couponItemHolder.itemView.getContext()).load(R.drawable.loading2))
                .centerInside()
                .into(categoryItemHolder.image);

        for(Category x:main)
        {
            if(x.getCategory().toLowerCase().trim().equals(category.getCategory().toLowerCase().trim()))
            {
                sub=x.getSubcategoryList();
                break;
            }
        }



        if(sub!=null)
        {
           if(sub.size()>0)
           {
               categoryItemHolder.image.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Bundle bundle=new Bundle();
                       Gson gson=new Gson();
                       String subcategory= gson.toJson(sub);
                       bundle.putString("subcategory",subcategory);
                       bundle.putString("category",category.getCategory());
                       Fragment fragment=new Subcategory_Activity();
                       fragment.setArguments(bundle);
                       MainActivity mainActivity=(MainActivity)context;
                       mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();

                   }
               });
           }
           else
           {
               categoryItemHolder.image.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Bundle bundle=new Bundle();
                       bundle.putString("subcategory",null);
                       bundle.putString("category",category.getCategory());
                       Fragment fragment=new NewRequestActivity();
                       fragment.setArguments(bundle);
                       MainActivity mainActivity=(MainActivity)context;
                       mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();

                   }
               });
           }
        }
        else
        {
            categoryItemHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("subcategory",null);
                    bundle.putString("category",category.getCategory());
                    Fragment fragment=new NewRequestActivity();
                    fragment.setArguments(bundle);
                    MainActivity mainActivity=(MainActivity)context;
                    mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();

                }
            });
        }




    }

    @Override
    public int getItemCount() {
       if(list!=null)
       {
           return list.size();
       }
       return 0;

    }

    class CategoryItemHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView image;


        CategoryItemHolder (View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.category_name_txt);
            image=itemView.findViewById(R.id.category_img);



        }
    }












}
