package com.mg.shineglass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private CardView cart;
    private CardView wallet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.main_activity);
        mBottomNavigationView=findViewById(R.id.bottom_nav_menu);
        cart=findViewById(R.id.my_cart_btn);

        wallet=findViewById(R.id.my_wallet_btn);

        wallet.setOnClickListener(view -> {
            Intent i=new Intent(MainActivity.this,WalletActivity.class);
            startActivity(i);
        });

        cart.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this,CartActivity.class);
            startActivity(i);
        });

        mBottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.home_icon);
        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, new HomePageFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if(mBottomNavigationView.getSelectedItemId()!=R.id.home_icon){
            mBottomNavigationView.setSelectedItemId(R.id.home_icon);
            getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, new HomePageFragment()).commit();
        }
        else{
            finishAffinity();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        InputMethodManager imm = (InputMethodManager)getSystemService(
//                Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mBottomNavigationView.getWindowToken(), 0);
//        mBottomNavigationView.setSelectedItemId(R.id.home_icon);
//    }
//
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        InputMethodManager imm = (InputMethodManager)getSystemService(
//                Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mBottomNavigationView.getWindowToken(), 0);
//        mBottomNavigationView.setSelectedItemId(R.id.home_icon);
//    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment=null;
            switch (item.getItemId()){
                case R.id.home_icon:
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mBottomNavigationView.getWindowToken(), 0);
                    fragment=new HomePageFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();
                    break;
                case R.id.request_icon:
                    fragment=new MyRequestsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();
                    break;
                case R.id.myOrders_icon:
                    fragment=new MyOrdersFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();
                    break;
                case R.id.my_account_icon:
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(MainActivity.this);

                    if(sharedPreferences.getString("token", null) == null)
                    {
                        Intent i=new Intent(MainActivity.this,LoginSignUpActivity.class);
                        startActivity(i);
//                        finish();
                    }
                    else
                    {
                        fragment=new MyAccountFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();
                    }

                    break;
            }

            return true;
        }
    };


}