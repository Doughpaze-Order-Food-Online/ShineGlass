package com.mg.shineglass;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.adapters.CategoryAdapter;
import com.mg.shineglass.adapters.SubCategoryAdapter;

import java.lang.reflect.Type;
import java.util.List;

public class Subcategory_Activity extends Fragment {
    private List<String> subcategory;
    private String category;
    private TextView type;
    private RecyclerView rvItem;

    public Subcategory_Activity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.categories_fragment, container, false);
        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            Gson gson=new Gson();
            Type type=new TypeToken<List<String>>(){}.getType();
            subcategory= gson.fromJson(bundle.getString("subcategory"),type);
            category=bundle.getString("category");
        }

        type=view.findViewById(R.id.type_of_glass_txt);
        type.setText(category);

        rvItem=view.findViewById(R.id.categories_container);

        SubCategoryAdapter subCategoryAdapter=new SubCategoryAdapter(subcategory,getActivity(),category);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2);
        rvItem.setLayoutManager(gridLayoutManager);
        rvItem.setAdapter(subCategoryAdapter);



        return view;

    }

}
