package com.mg.shineglass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mg.shineglass.utils.constants;

import java.util.Objects;

public class DeliveryMyAccountFragment extends Fragment {

    private TextView name, mobile_no, email;
    private Button logout;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient mGoogleSignInClient;
    private RelativeLayout reset_password;



    public DeliveryMyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView=inflater.inflate(R.layout.fragment_delivery_my_account, container, false);
        name=rootView.findViewById(R.id.user_name);
        mobile_no=rootView.findViewById(R.id.phone_no);
        email=rootView.findViewById(R.id.user_email);
        logout=rootView.findViewById(R.id.logout_btn);
        reset_password=rootView.findViewById(R.id.cancel_btn);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        name.setText(sharedPreferences.getString(constants.USERNAME,null));
        email.setText(sharedPreferences.getString(constants.EMAIL,null));
        mobile_no.setText(sharedPreferences.getString(constants.PHONE,null));


        //google signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);
        reset_password.setOnClickListener(v -> {
            Intent i=new Intent(getContext(),ResetPassword.class);
            startActivity(i);
        });

        logout.setOnClickListener(view->LOGOUT());

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
}