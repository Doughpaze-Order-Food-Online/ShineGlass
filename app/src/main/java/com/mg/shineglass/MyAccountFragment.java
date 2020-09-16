package com.mg.shineglass;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mg.shineglass.utils.constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MyAccountFragment extends Fragment {

    private TextView name, mobile_no, email;

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
        email=rootView.findViewById(R.id.email);


        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        name.setText(sharedPreferences.getString(constants.USERNAME,null));
        email.setText(sharedPreferences.getString(constants.EMAIL,null));
        mobile_no.setText(sharedPreferences.getString(constants.PHONE,null));

        return rootView;
    }
}