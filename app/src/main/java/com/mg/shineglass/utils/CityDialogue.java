package com.mg.shineglass.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.R;
import com.mg.shineglass.models.Banners;
import com.mg.shineglass.models.City;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CityDialogue {
    Activity activity;
    public Dialog dialog;
    Spinner citySpinner;
    private SharedPreferences sharedPreferences;


    public CityDialogue(Activity activity) {
        this.activity = activity;
        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.city_dialogue);
    }

    public void showDialog() {


        citySpinner = dialog.findViewById(R.id.citySpinner);


        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);

        Gson gson=new Gson();

        List<City> list=new ArrayList<>();
        Type type=new TypeToken<List<City>>(){}.getType();
        list=gson.fromJson(sharedPreferences.getString("city", null),type);

        List<String> cityList = new ArrayList<String>();
        cityList.add("Tap to select");
        assert list != null;
        for(City x:list)
            cityList.add(x.getName());


        ArrayAdapter<String> cityDataAdapter = new ArrayAdapter<String>(activity, R.layout.spinner_selected_text, cityList);
        cityDataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        citySpinner.setAdapter(cityDataAdapter);
        dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog(){
        dialog.dismiss();
    }


}
