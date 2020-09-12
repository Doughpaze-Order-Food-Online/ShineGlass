package com.mg.shineglass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
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
                    break;
                case R.id.request_icon:
                    fragment=new MyRequestsFragment();
                    break;
                case R.id.myOrders_icon:
                    fragment=new MyOrdersFragment();
                    break;
                case R.id.my_account_icon:
                    fragment=new MyAccountFragment();
                    break;
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();
            return true;
        }
    };
}