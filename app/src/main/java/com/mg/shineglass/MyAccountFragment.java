package com.mg.shineglass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.adapters.OrdersAdapter;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.MyOrders;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MyAccountFragment extends Fragment {

    private TextView name, mobile_no, email;
    private Button logout,show;
    private RelativeLayout myaddress,reset_password;
    private GoogleSignInClient mGoogleSignInClient;
    private CircleImageView circleImageView;
    private SharedPreferences sharedPreferences;
    private CompositeSubscription mSubscriptions;
    private TextView empty,orderid,date,mode,total;
    private ViewDialog viewDialog;
    private LinearLayout recent;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView=inflater.inflate(R.layout.fragment_my_account, container, false);

        name=rootView.findViewById(R.id.user_name);
        mobile_no=rootView.findViewById(R.id.phone_no);
        email=rootView.findViewById(R.id.user_email);
        myaddress=rootView.findViewById(R.id.myAddresses_btn);
        reset_password=rootView.findViewById(R.id.cancel_btn);
        show=rootView.findViewById(R.id.show_more_btn);
        empty=rootView.findViewById(R.id.empty);
        recent=rootView.findViewById(R.id.recent);

        orderid=rootView.findViewById(R.id.transaction_id_value);
        date=rootView.findViewById(R.id.date_value);
        mode=rootView.findViewById(R.id.mode_value);
        total=rootView.findViewById(R.id.amount_value);

        logout=rootView.findViewById(R.id.logout_btn);

     sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        mSubscriptions = new CompositeSubscription();

        circleImageView=rootView.findViewById(R.id.profile_icon_img);

        show.setOnClickListener(v -> {
            Fragment fragment=new MyOrdersFragment();
           Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, fragment).commit();
        });


    reset_password.setOnClickListener(v -> {
        Intent i=new Intent(getContext(),ResetPassword.class);
        startActivity(i);
    });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),EditDetailsPopUpActivity.class);
                startActivity(i);
            }
        });

        myaddress.setOnClickListener(v -> {
            Intent i=new Intent(getContext(),My_Account_Address_Activity.class);
            startActivity(i);
        });


        name.setText(sharedPreferences.getString(constants.USERNAME,null));
        email.setText(sharedPreferences.getString(constants.EMAIL,null));
        mobile_no.setText(sharedPreferences.getString(constants.PHONE,null));


        //google signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);

        logout.setOnClickListener(view->LOGOUT());

        viewDialog = new ViewDialog(getActivity());

        if(sharedPreferences.getString("orderId",null)==null)
        {
            FETCH_DATA();
        }
        else
        {
            if(sharedPreferences.getString("orderId", null).equals("empty"))
            {
                recent.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);

            }
            else {
                recent.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);

                orderid.setText(sharedPreferences.getString("orderId",null));
                mode.setText(sharedPreferences.getString("mode",null));
                total.setText(sharedPreferences.getString("total",null));
                date.setText(sharedPreferences.getString("date",null));
            }
        }




        return rootView;
    }


    private void LOGOUT(){



        if(sharedPreferences.getString(constants.TYPE, null).equals("google"))
        {
            revokeAccess();
        }

        if(sharedPreferences.getString(constants.TYPE, null).equals("facebook"))
        {
            LoginManager.getInstance().logOut();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(constants.TOKEN,null);
        editor.putString(constants.EMAIL,null);
        editor.putString(constants.USERNAME,null);
        editor.putString(constants.PHONE,null);
        editor.putString(constants.TYPE,null);
        editor.putString(constants.USER_TYPE,null);
        editor.apply();
        goToHome();

    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void goToHome() {
        Toast.makeText(getActivity(), "LogOut Success! ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginSignUpActivity.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onResume() {
        super.onResume();

            name.setText(sharedPreferences.getString(constants.USERNAME,null));
            email.setText(sharedPreferences.getString(constants.EMAIL,null));
            mobile_no.setText(sharedPreferences.getString(constants.PHONE,null));
    }

    private void FETCH_DATA()
    {

        viewDialog.showDialog();

        mSubscriptions.add(
                networkUtils.getRetrofit(sharedPreferences.getString("token", null))
                        .GET_ORDERS()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(List<MyOrders> response) {
        viewDialog.hideDialog();

        if(response.size()>0 && response.get(0) !=null)
        {
            recent.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("orderId",response.get(0).getOrderNo());
            editor.putString("mode",response.get(0).getPayment_mode());
            editor.putString("date",simpleDateFormat.format(response.get(0).getOrder_date()));
            editor.putString("total",response.get(0).getTotal().toString());
            editor.apply();

            orderid.setText(response.get(0).getOrderNo());
            mode.setText(response.get(0).getPayment_mode());
            total.setText(response.get(0).getTotal().toString());

            date.setText(simpleDateFormat.format(response.get(0).getOrder_date()));
        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("orderId","empty");
            editor.apply();
            recent.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);

        }


    }

    private void handleError(Throwable error) {
        viewDialog.hideDialog();

        Log.e("error",error.toString());

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody,BasicResponse.class);
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }
}