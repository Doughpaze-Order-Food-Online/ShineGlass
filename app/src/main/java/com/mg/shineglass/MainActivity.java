package com.mg.shineglass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mBottomNavigationView=findViewById(R.id.bottom_nav_menu);
        mBottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, new HomePageFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment=null;
            switch (item.getItemId()){
                case R.id.home_icon:
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
                        finish();
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