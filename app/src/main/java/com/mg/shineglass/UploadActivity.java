package com.mg.shineglass;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.Interface.deleteFile;
import com.mg.shineglass.adapters.FileUploadAdapter;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.Quotation;
import com.mg.shineglass.network.FileUtils;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class UploadActivity extends AppCompatActivity implements deleteFile {

    final int REQUEST_EXTERNAL_STORAGE = 100;
    private FrameLayout upload;
    List<Uri> arrayList = new ArrayList<>();
    private RecyclerView fileItem;
    private CompositeSubscription mSubscriptions;
    private RelativeLayout cancel,request;
    private long mLastClickTime = 0;
    private ViewDialog viewDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_pop_activity);
        this.setFinishOnTouchOutside(false);
        upload = findViewById(R.id.choose_file_block1);
        fileItem = findViewById(R.id.file_container);
        mSubscriptions = new CompositeSubscription();
        cancel=findViewById(R.id.cancel_btn);
        request=findViewById(R.id.request);

        cancel.setOnClickListener(view->finish());
        request.setOnClickListener(view-> {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);

            if(sharedPreferences.getString("token", null) == null)
            {
                Toast.makeText(UploadActivity.this, "Login To Request Quotation", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(UploadActivity.this,LoginSignUpActivity.class);
                startActivity(i);
                finish();
            }
            else
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                new AlertDialog.Builder(UploadActivity.this)
                        .setTitle("Are you sure??")
                        .setMessage("Do you want to continue with the request??")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    SEND_REQUEST();
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }


        });

        fileItem=findViewById(R.id.file_container);

        upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
//                    return;
                } else {
                    launchGalleryIntent();
                }
            }
        });


        viewDialog = new ViewDialog(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        String[] mimeTypes = {"image/*", "application/pdf"};
        intent.setType("image/*|application/pdf");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
                            arrayList.add(imageUri);
                            FileUploadAdapter fileUploadAdapter = new FileUploadAdapter(arrayList, UploadActivity.this, this::remove,false);
                            LinearLayoutManager LinearLayout = new LinearLayoutManager(UploadActivity.this);
                            fileItem.setLayoutManager(LinearLayout);
                            fileItem.setAdapter(fileUploadAdapter);

                        } catch (Exception e) {
                            Log.e("Upload", "File select error", e);
                        }
                    }

                } else if (data.getData() != null) {

                    final Uri uri = data.getData();
                    Log.i("Upload", "Uri = " + uri.toString());

                    try {
                        arrayList.add(uri);
                        FileUploadAdapter fileUploadAdapter = new FileUploadAdapter(arrayList, UploadActivity.this, this::remove,false);
                        LinearLayoutManager LinearLayout = new LinearLayoutManager(UploadActivity.this);
                        fileItem.setLayoutManager(LinearLayout);
                        fileItem.setAdapter(fileUploadAdapter);

                    } catch (Exception e) {
                        Log.e("Upload", "File select error", e);
                    }
                }

            }

        }
    }



    @Override
    public void remove(int i) {
        arrayList.remove(i);
        FileUploadAdapter fileUploadAdapter = new FileUploadAdapter(arrayList, UploadActivity.this, this::remove,false);
        LinearLayoutManager LinearLayout = new LinearLayoutManager(UploadActivity.this);
        fileItem.setLayoutManager(LinearLayout);
        fileItem.setAdapter(fileUploadAdapter);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse("*/*"), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) throws URISyntaxException {
        // use the FileUtils to get the actual file by uri
        String filePath=FileUtils.getPath(this,fileUri);
        String filetype;
        assert filePath != null;
        File file = new File(filePath);

        if(filePath.substring(filePath.lastIndexOf(".")).equals(".pdf"))
        {
            filetype="application/pdf";
        }
        else
        {
            filetype="image/"+filePath.substring(filePath.lastIndexOf("."));
        }


       

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(filetype), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

    }

    private void SEND_REQUEST() throws URISyntaxException {

        viewDialog.showDialog();
       final List<MultipartBody.Part> files = new ArrayList<>();


        if (arrayList != null) {

            for (int i = 0; i < arrayList.size(); i++) {
                files.add(prepareFilePart("files", arrayList.get(i))) ;
            }

        }

        ArrayList<Quotation> list=new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mSubscriptions.add(networkUtils.getRetrofit(sharedPreferences.getString("token", null)).REQUEST_QUOTATION(files,list,sharedPreferences.getString(constants.PHONE, null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));


    }

    private void handleResponse(Response<LoginResponse> response) {

        viewDialog.hideDialog();
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(UploadActivity.this,MainActivity.class );
        startActivity(intent);
        finish();

    }

    private void handleError(Throwable error) {
        viewDialog.hideDialog();

        Log.e("error",error.toString());
        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response<BasicResponse> response = gson.fromJson(errorBody,Response.class);
                assert response.body() != null;

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(this, "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }





}

