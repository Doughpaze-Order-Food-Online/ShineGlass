package com.mg.shineglass.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.mg.shineglass.R;

import java.util.ArrayList;
import java.util.List;

public class CityDialogue {
    Activity activity;
    public Dialog dialog;

    public CityDialogue(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {

        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.city_dialogue);
        Spinner citySpinner = dialog.findViewById(R.id.citySpinner);
        List<String> cityList = new ArrayList<String>();
        cityList.add("Tap to select");
        cityList.add("city1");
        cityList.add("city2");
        cityList.add("city3");
        cityList.add("city4");
        cityList.add("city5");

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
