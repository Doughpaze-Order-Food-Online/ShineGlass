package com.mg.shineglass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.adapters.BannerAdapter;
import com.mg.shineglass.adapters.CategoryAdapter;
import com.mg.shineglass.adapters.RatesAdapter;
import com.mg.shineglass.models.Banners;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.Category;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.Rates;
import com.mg.shineglass.models.SpanningGridLayoutManager;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.constants;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment {

    private RecyclerView rates,category;
    private ViewPager viewPager;
    private BannerAdapter bannerAdapter;
    private RatesAdapter ratesAdapter;
    private CategoryAdapter categoryAdapter;
    private FrameLayout upload;
    private SharedPreferences sharedPreferences;


    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.homepage_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        rates=view.findViewById(R.id.rates_container);
        category=view.findViewById(R.id.categories_container);
        viewPager =  view.findViewById(R.id.banners_container);
        upload=view.findViewById(R.id.upload_image_pdf_block);





        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getContext(),UploadActivity.class);
                startActivity(i);

            }
        });

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        BANNER();
        RATES();
        CATEGORY();




    }

    private void BANNER() {
        Gson gson=new Gson();

        List<Banners> list=new ArrayList<>();
        Type type=new TypeToken<List<Banners>>(){}.getType();
        list=gson.fromJson(sharedPreferences.getString("banner", null),type);
        bannerAdapter=new BannerAdapter(Objects.requireNonNull(getContext()),list);
        assert list != null;
        bannerAdapter.setTimer(viewPager,5,list.size(),0);
        viewPager.setAdapter(bannerAdapter);
    }

    private void RATES() {
        Gson gson=new Gson();
        List<Rates> list=new ArrayList<>();
        Type type=new TypeToken<List<Rates>>(){}.getType();
        list=gson.fromJson(sharedPreferences.getString("rates", null),type);
        ratesAdapter=new RatesAdapter(list);
        LinearLayoutManager LinearLayout=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        SpanningGridLayoutManager spanningGridLayoutManager=new SpanningGridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL,false);
        rates.setLayoutManager(spanningGridLayoutManager);
        rates.setAdapter(ratesAdapter);

    }

    private void CATEGORY() {
        Gson gson=new Gson();
        List<Category> list=new ArrayList<>();
        Type type=new TypeToken<List<Category>>(){}.getType();
        list=gson.fromJson(sharedPreferences.getString("category", null),type);

        List<Category> categoryList=new ArrayList<>();
        categoryList.add(new Category("CLEAR GLASS",R.drawable.clearglass));
        categoryList.add(new Category("REFLECTIVE GLASS",R.drawable.reflectiveglass));
        categoryList.add(new Category("TINTED GLASS",R.drawable.tintedglass));
        categoryList.add(new Category("HIGH REFLECTIVE GLASS",R.drawable.highreflectiveglass));
        categoryList.add(new Category("LACQUERED GLASS",R.drawable.laqueredglass));
        categoryList.add(new Category("MIRROR",R.drawable.mirror));


        categoryAdapter=new CategoryAdapter(categoryList,getActivity(),list);
        SpanningGridLayoutManager spanningGridLayoutManager=new SpanningGridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL,false);
        category.setLayoutManager(spanningGridLayoutManager);
        category.setAdapter(categoryAdapter);
    }


}