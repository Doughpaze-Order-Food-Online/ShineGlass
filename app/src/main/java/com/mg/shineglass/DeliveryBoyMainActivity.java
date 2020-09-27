package com.mg.shineglass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mg.shineglass.R;

public class DeliveryBoyMainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_boy_activity_main);
        mBottomNavigationView=findViewById(R.id.bottom_nav_menu);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mBottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.previousOrders_icon);
        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, new DeliveryPreviousOrdersFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if(mBottomNavigationView.getSelectedItemId()!=R.id.previousOrders_icon){
            mBottomNavigationView.setSelectedItemId(R.id.previousOrders_icon);
            getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, new DeliveryPreviousOrdersFragment()).commit();
        }
        else{
            finishAffinity();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment=null;
            switch (item.getItemId()){
                case R.id.previousOrders_icon:
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mBottomNavigationView.getWindowToken(), 0);
                    fragment=new DeliveryPreviousOrdersFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();
                    break;
                case R.id.current_orders_icon:
                    fragment=new CurrentOrdersFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();
                    break;
                case R.id.my_account_icon:
                        fragment=new DeliveryMyAccountFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();
                    break;
            }
            return true;
        }
    };
}