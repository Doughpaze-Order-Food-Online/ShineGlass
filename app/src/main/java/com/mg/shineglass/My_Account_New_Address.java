package com.mg.shineglass;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mg.shineglass.utils.validation.validateFields;

public class My_Account_New_Address extends Activity {

    private CompositeSubscription mSubscriptions;
    private RelativeLayout automatic,proceed;
    private EditText address;
    private CheckBox save;
    private SharedPreferences mSharedPreferences;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = New_Address_Activity.class.getSimpleName();
    private double latitude, longitude;
    private String newaddress;
    private ViewDialog viewDialog;
    private static final int REQUEST_LOCATION_PERMISSION2 = 2;

    // Constants
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adress_details_fragment);
        mSubscriptions = new CompositeSubscription();

        automatic=findViewById(R.id.cancel_btn);
        address=findViewById(R.id.user_email_mobile_input_txt);
        save=findViewById(R.id.save);
        save.setVisibility(View.GONE);

        TextView text=findViewById(R.id.request_quotation_txt);
        text.setText("SAVE");
        proceed=findViewById(R.id.proceed_to_buy_btn);


        automatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();

            }
        });

        proceed.setOnClickListener(view->DETAILS());
        viewDialog = new ViewDialog(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION2);
        } else {
            getCoordinates();
        }

    }


    private void DETAILS() {
        address.setError(null);

        int err=0;

        newaddress = address.getText().toString();


        if (!validateFields(newaddress)) {

            err++;
            address.setError("Address is required!");
        }



        if(err==0)
        {


            com.mg.shineglass.models.Address address=new com.mg.shineglass.models.Address();
            address.setAddress(newaddress);
            address.setLatitude(latitude);
            address.setLongitude(longitude);

            mSharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);

            String token = mSharedPreferences.getString(constants.TOKEN, null);
            viewDialog.showDialog();
            mSubscriptions.add(networkUtils.getRetrofit(token)
                    .SAVE_ADDRESS(address)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse,this::handleError));
        }

    }



    private void handleResponse(BasicResponse response) {
        viewDialog.hideDialog();
        Toast.makeText(this, "Address Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void handleError(Throwable error) {

        viewDialog.hideDialog();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody,BasicResponse.class);
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();

        }
    }





    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "getLocation: permissions granted");
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();


                    setAddress(location);
                } else {
                    Toast.makeText(My_Account_New_Address.this,
                            "Permisson denied",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void setAddress(Location location) {
        Geocoder geocoder = new Geocoder(My_Account_New_Address.this,
                Locale.getDefault());
        List<Address> addresses = null;
        String resultMessage = "";

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems
            resultMessage =My_Account_New_Address.this
                    .getString(R.string.service_not_available);
            Log.e(TAG, resultMessage, ioException);
        }

        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
                resultMessage =My_Account_New_Address.this
                        .getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);
            }
        } else {
            Address address = addresses.get(0);
            StringBuilder out = new StringBuilder();
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                out.append(address.getAddressLine(i));
            }

            resultMessage = out.toString();
        }

        address.setText(resultMessage);

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this,
                            "Permission Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_LOCATION_PERMISSION2:
                getCoordinates();
                break;
        }

    }


    private void getCoordinates() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                } else {
                    Toast.makeText(My_Account_New_Address.this,
                            "Permisson denied",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}

