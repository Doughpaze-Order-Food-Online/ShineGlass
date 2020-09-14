package com.mg.shineglass.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mg.shineglass.MainActivity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryItemHolder>  {

    private List<Category> list;

    public CategoryAdapter(List<Category> list) {
        this.list=list;
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



        categoryItemHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(categoryItemHolder.itemView.getContext(), MainActivity.class);
                intent.putExtra("category",category.getCategory());
                categoryItemHolder.itemView.getContext().startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();

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
