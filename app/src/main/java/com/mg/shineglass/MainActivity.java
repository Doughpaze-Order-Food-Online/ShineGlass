package com.mg.shineglass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static BottomNavigationView mBottomNavigationView;
    private CardView cart;
    private CardView wallet;
    private SharedPreferences sharedPreferences;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuImgButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_navigation_drawer);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

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

        drawerLayout=findViewById(R.id.drawer_main_activity);
        navigationView=findViewById(R.id.nav_view);
        menuImgButton=findViewById(R.id.menu_button);

        menuImgButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(mBottomNavigationView.getSelectedItemId()!=R.id.home_icon){
            mBottomNavigationView.setSelectedItemId(R.id.home_icon);
            getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, new HomePageFragment()).commit();
        }
        else{
            finishAffinity();

            sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("orders", null);
            editor.putString("orderId", null);
            editor.putString("request", null);
            editor.putString("wallet", null);
            editor.apply();
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent=null;
        switch (item.getItemId()){
            case R.id.about_facebook:
                Toast.makeText(MainActivity.this,"facebook",Toast.LENGTH_SHORT).show();
                break;
            case R.id.terms_and_policies:
                intent=new Intent(MainActivity.this,TermsAndPolicies.class);
                startActivity(intent);
                break;

            case R.id.help_support:
                intent=new Intent(MainActivity.this,HelpOrContactUs.class);
                startActivity(intent);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}