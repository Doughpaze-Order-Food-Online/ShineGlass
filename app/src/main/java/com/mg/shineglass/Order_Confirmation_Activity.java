package com.mg.shineglass;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.adapters.FileUploadAdapter;
import com.mg.shineglass.models.Address;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.network.FileUtils;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.mg.shineglass.utils.validation.validateFields;

public class Order_Confirmation_Activity extends AppCompatActivity {

    private TextView quotation, date, address, total, amount;
    private RelativeLayout view;
    private String Quotation, Date, Address, url;
    private RadioGroup radioGroup;
    private RelativeLayout confirm;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private long mLastClickTime = 0;
    private final String TAG = "order_confirm_activity";
    private final Integer ActivityRequestCode = 2;
    private ImageView backBtnImg;
    private Double latitude, longitude;
    private Address newaddres;
    private ViewDialog viewDialog;
    private EditText tid;
    private Double Total;
    private SharedPreferences sharedPreferences;
    private Button image;
    private Uri uri = null;
    private TextView textView;
    private RelativeLayout file;
    private FrameLayout cross;
    final int REQUEST_EXTERNAL_STORAGE = 100;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_and_payment_details_fragment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mSubscriptions = new CompositeSubscription();

        backBtnImg=findViewById(R.id.back_btn_img);
        backBtnImg.setOnClickListener(view -> finish());

        Intent intent=getIntent();
        Quotation=intent.getStringExtra("quotation");
        url=intent.getStringExtra("url");
        Total=Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("total")));

        Address=intent.getStringExtra("address");
        Date=intent.getStringExtra("date");
        latitude=Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("latitude")));
        longitude=Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("longitude")));
        viewDialog = new ViewDialog(this);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        newaddres=new Address();
        newaddres.setAddress(Address);
        newaddres.setLatitude(latitude);
        newaddres.setLongitude(longitude);

        quotation=findViewById(R.id.request_no);
        date=findViewById(R.id.date_value);
        address=findViewById(R.id.name_txt);
        total=findViewById(R.id.total_charge_value);
        view=findViewById(R.id.request_quotation_btn);
        file=findViewById(R.id.file);
        file.setVisibility(View.GONE);
        textView=findViewById(R.id.file_name_txt);
        cross=findViewById(R.id.cross_btn);

        confirm=findViewById(R.id.place_order_btn);
        amount=findViewById(R.id.wallet_cash_value_txt);
        quotation.setText(Quotation);
        date.setText(Date);
        total.setText(String.valueOf(Total));
        address.setText(Address);
        tid=findViewById(R.id.transaction_id_value);


        cross.setOnClickListener(v -> {
            uri=null;
            file.setVisibility(View.GONE);
        });
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);




        view.setOnClickListener(v -> {
            Intent i=new Intent(Order_Confirmation_Activity.this, Quotation_Activity.class);
            i.putExtra("quotation",Quotation);
            i.putExtra("url",url);
            i.putExtra("total",Total.toString());
            i.putExtra("date",Date);
            startActivity(i);
            finish();

        });

        confirm.setOnClickListener(v -> {
            if(!validateFields(tid.getText().toString()))
            {
                tid.setError("Enter Transaction Id");
                return ;

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                new AlertDialog.Builder(Order_Confirmation_Activity.this)
                        .setTitle("Are you sure??")
                        .setMessage("Do you want to place the order??")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    OFFLINE();
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


        });


        image=findViewById(R.id.attatch_payment_screenshot_btn);
        image.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(Order_Confirmation_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Order_Confirmation_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
//                    return;
            } else {
                launchGalleryIntent();
            }

        });

    }



    private void OFFLINE() throws URISyntaxException {


        viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit( mSharedPreferences.getString(constants.TOKEN, null))
                .PLACE_OFFLINE_ORDER(prepareFilePart("payment", uri),Quotation,newaddres,tid.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }



    private void handleResponse(Integer status) {
        Intent i=new Intent(Order_Confirmation_Activity.this,MainActivity.class);
        i.putExtra("callMyOrdersFragment","OrderConfirmationActivity");
        startActivity(i);
        finish();
        Toast.makeText(this, "Order Placed!", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("request",null);
        editor.putString("orders",null);
        editor.apply();

    }


    private void handleError(Throwable error) {
        viewDialog.hideDialog();

        Log.e("OrderConfirmation", error.toString());
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
            Toast.makeText(this, "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {

            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int currentItem = 0;
                    while (currentItem < count) {
                        Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                        currentItem = currentItem + 1;

                        Log.d("Uri Selected", imageUri.toString());

                        try {
                           uri=imageUri;
                           textView.setText(getFileName(uri));
                           file.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            Log.e("Upload", "File select error", e);
                        }
                    }

                } else if (data.getData() != null) {

                    final Uri uri1 = data.getData();
                    Log.i("Upload", "Uri = " + uri1.toString());

                    try {
                        uri=uri1;
                        textView.setText(getFileName(uri));
                        file.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Log.e("Upload", "File select error", e);
                    }
                }

            }

        }
    }


    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) throws URISyntaxException {
        // use the FileUtils to get the actual file by uri
        String filePath= FileUtils.getPath(this,fileUri);

        assert filePath != null;
        File file = new File(filePath);



        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"select file"), REQUEST_EXTERNAL_STORAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchGalleryIntent();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}
