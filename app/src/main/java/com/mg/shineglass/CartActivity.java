package com.mg.shineglass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.Interface.DeleteCartItem;
import com.mg.shineglass.Interface.deleteFile;
import com.mg.shineglass.adapters.CartAdapter;
import com.mg.shineglass.adapters.FileUploadAdapter;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.Quotation;
import com.mg.shineglass.network.FileUtils;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.CityDialogue;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
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

public class CartActivity extends AppCompatActivity implements deleteFile, DeleteCartItem {

    private RecyclerView cartItem,fileItem;
    private List<Uri> filelist;
    private RelativeLayout request;
    private long mLastClickTime = 0;
    private CompositeSubscription mSubscriptions;
    private ArrayList<Quotation> cartlist;
    private ImageView backbtn;
    private TextView empty,images;
    private ViewDialog viewDialog;
    private String city;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_fragment);
        cartItem=findViewById(R.id.cart_container);
        fileItem=findViewById(R.id.uploaded_container);
        request=findViewById(R.id.request);
        empty=findViewById(R.id.empty_text);
        images=findViewById(R.id.images);

        backbtn=findViewById(R.id.back_btn_img);

        backbtn.setOnClickListener(v -> finish());

        mSubscriptions = new CompositeSubscription();

        Gson gson=new Gson();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        if(sharedPreferences.getString("quotation", null)==null)
        {
            cartItem.setVisibility(View.GONE);
            fileItem.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            request.setVisibility(View.GONE);
            images.setVisibility(View.GONE);

        }
        else
        {
            cartItem.setVisibility(View.VISIBLE);
            fileItem.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            request.setVisibility(View.VISIBLE);
        }

       cartlist=new ArrayList<>();
        Type type=new TypeToken<List<Quotation>>(){}.getType();
        cartlist=gson.fromJson(sharedPreferences.getString("quotation", null),type);


        CartAdapter cartAdapter = new CartAdapter(cartlist, this);
        LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
        cartItem.setLayoutManager(LinearLayout);
        cartItem.setAdapter(cartAdapter);

        if(sharedPreferences.getString("files", null)==null)
        {
            images.setVisibility(View.GONE);
        }
        else
        {
            images.setVisibility(View.VISIBLE);
        }

        filelist=new ArrayList<>();
        List<String> list=new ArrayList<>();
        Type filetype=new TypeToken<List<String>>(){}.getType();
        list=gson.fromJson(sharedPreferences.getString("files", null),filetype);
       if(list!=null)
       {
           for(String x:list)
           {
               Uri uri=Uri.parse(x);
               filelist.add(uri);
           }
       }

        FileUploadAdapter fileUploadAdapter = new FileUploadAdapter(filelist, CartActivity.this, this,true);
        LinearLayoutManager LinearLayout2 = new LinearLayoutManager(this);
        fileItem.setLayoutManager(LinearLayout2);
        fileItem.setAdapter(fileUploadAdapter);


        request.setOnClickListener(view-> {

            if(sharedPreferences.getString("token", null) == null)
            {
                Toast.makeText(CartActivity.this, "Login To Request Quotation", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(CartActivity.this,LoginSignUpActivity.class);
                startActivity(i);
                finish();
            }
            else
            {
                CityDialogue cityDialogue=new CityDialogue(this);
                cityDialogue.showDialog();

                RelativeLayout RContinue=cityDialogue.dialog.findViewById(R.id.update_btn);
                RelativeLayout RCancel=cityDialogue.dialog.findViewById(R.id.cancel_btn);
                Spinner citySpinner=cityDialogue.dialog.findViewById(R.id.citySpinner);
                city=citySpinner.getSelectedItem().toString();
                if(city.toLowerCase().trim().equals("Tap to select".toLowerCase().trim()))
                {
                    Toast.makeText(this, "Select City!", Toast.LENGTH_SHORT).show();
                    return;
                }
                RContinue.setOnClickListener(view1 -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();



                    new AlertDialog.Builder(CartActivity.this)
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
                    cityDialogue.hideDialog();
                });

                RCancel.setOnClickListener(view1 -> cityDialogue.hideDialog());

            }


        });
        viewDialog = new ViewDialog(this);


    }

    @Override
    public void remove(int i) {
        filelist.remove(i);
        FileUploadAdapter fileUploadAdapter = new FileUploadAdapter(filelist, CartActivity.this, this,true);
        LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
        fileItem.setLayoutManager(LinearLayout);
        fileItem.setAdapter(fileUploadAdapter);


        Gson gson=new Gson();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);


        List<String> list=new ArrayList<>();
        Type type=new TypeToken<List<String>>(){}.getType();
        list=gson.fromJson(sharedPreferences.getString("files", null),type);

        assert list != null;
        list.remove(i);

        if(list.size()>0)
        {
            String f=gson.toJson(list);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("files",f);
            editor.apply();
        }
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("files",null);
            editor.apply();
        }

    }



    private void SEND_REQUEST() throws URISyntaxException {
        final List<MultipartBody.Part> files = new ArrayList<>();


        if (filelist != null) {

            for (int i = 0; i < filelist.size(); i++) {
                files.add(prepareFilePart("files", filelist.get(i))) ;
            }

        }
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        viewDialog.showDialog();

        mSubscriptions.add(networkUtils.getRetrofit(sharedPreferences.getString("token", null))
                .REQUEST_QUOTATION(files,cartlist,sharedPreferences.getString(constants.PHONE, null),city)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));


    }

    private void handleResponse(Response<LoginResponse> response) {
        viewDialog.hideDialog();

        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("files", null);
        editor.putString("quotation", null);
        editor.apply();




        Intent intent = new Intent(CartActivity.this, MainActivity.class);
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

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) throws URISyntaxException {
        // use the FileUtils to get the actual file by uri
        String filePath= FileUtils.getPath(this,fileUri);
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


    @Override
    public void removeCartItem(int i) {
            cartlist.remove(i);

        CartAdapter cartAdapter = new CartAdapter(cartlist, this);
        LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
        cartItem.setLayoutManager(LinearLayout);
        cartItem.setAdapter(cartAdapter);

        Gson gson=new Gson();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        List<Quotation> list=new ArrayList<>();
        Type type=new TypeToken<List<Quotation>>(){}.getType();
        list=gson.fromJson(sharedPreferences.getString("quotation", null),type);

        assert list != null;
        list.remove(i);

        if(list.size()>0)
        {
            String s=gson.toJson(list);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("quotation",s);
            editor.apply();
        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("quotation",null);
            editor.putString("files", null);
            editor.apply();

            cartItem.setVisibility(View.GONE);
            fileItem.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            request.setVisibility(View.GONE);
            images.setVisibility(View.GONE);
        }



    }
}
