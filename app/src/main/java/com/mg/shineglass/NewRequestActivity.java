package com.mg.shineglass;

import android.Manifest;
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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.Interface.deleteFile;
import com.mg.shineglass.adapters.FileUploadAdapter;
import com.mg.shineglass.models.Quotation;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import static com.mg.shineglass.utils.validation.validateEmail;
import static com.mg.shineglass.utils.validation.validateFields;

public class NewRequestActivity  extends FragmentActivity implements deleteFile {
    private String category, subcategory;
    private TextView type,subtype;
    private RecyclerView rvItem;
    private RadioGroup radioGroup;
    private TextInputLayout thickness, width, height, quantity;
    private RelativeLayout button;
    private FrameLayout upload;
    List<Uri> arrayList = new ArrayList<>();
    final int REQUEST_EXTERNAL_STORAGE = 100;

    private EditText Ethickness, Ewidth, Eheight, Equantity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.category_description_fragment);

        // Inflate the layout for this fragment
        Intent i=getIntent();
        subcategory = i.getStringExtra("subcategory") ;
        category = i.getStringExtra("category");


        type = findViewById(R.id.item_name_txt);
        subtype=findViewById(R.id.type_of_glass_txt);
        type.setText(category);
        if (subcategory != null) {
           subtype.setText(subcategory);
        }
        else
        {
            subtype.setText(null);
        }



        thickness = findViewById(R.id.thickness);
        width = findViewById(R.id.width);
        height = findViewById(R.id.height);
        quantity = findViewById(R.id.quantity);
        button=findViewById(R.id.request_quotation_btn);
        upload=findViewById(R.id.upload);
        radioGroup=findViewById(R.id.scale);
        rvItem=findViewById(R.id.description_uploaded_container);


        Ethickness = findViewById(R.id.thickness_edt);
        Ewidth = findViewById(R.id.width_edt);
        Eheight = findViewById(R.id.height_edt);
        Equantity = findViewById(R.id.quantity_edt);
        button.setOnClickListener(view -> REQUEST());

        upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewRequestActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewRequestActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
//                    return;
                } else {
                    launchGalleryIntent();
                }
            }
        });

    }



    private void REQUEST() {
        setError();


        String Tthickness = Objects.requireNonNull( Ethickness.getText()).toString();
        String Twidth = Objects.requireNonNull(Ewidth.getText()).toString();
        String Theight = Objects.requireNonNull(Eheight.getText()).toString();
        String Tquantity = Objects.requireNonNull(Equantity.getText()).toString();
        String TScale=null;



        int err = 0;

        if (!validateFields(Tthickness)) {

            err++;
            thickness.setError("Thickness is required !");
        }

        if (!validateFields(Twidth)) {

            err++;
            width.setError("Width is required !");
        }


        if (!validateFields(Theight)) {

            err++;
            height.setError("Height is required !");
        }

        if (!validateFields(Tquantity)) {

            err++;
            quantity.setError("Quantity is required !");
        }

        if(validateFields(Tthickness) &&
                validateFields(Twidth)
                && validateFields(Theight)
                && validateFields(Tquantity))
        {
            if(radioGroup.getCheckedRadioButtonId()==-1)
            {   err++;
                Toast.makeText(this, "Select The Scale", Toast.LENGTH_SHORT).show();
            }
        }




        if (err == 0) {

            if(radioGroup.getCheckedRadioButtonId()==R.id.inch_radio_btn)
            {
                TScale="inches";
            }
            else
            {
                TScale="mm";
            }


            Quotation quotation =new Quotation(Tthickness,Twidth,Theight,Tquantity,TScale,category,subcategory);
            ADD(quotation);

        } else {

            showSnackBarMessage("Enter All Details !");
        }
    }

    private void setError() {

        thickness.setError(null);
        width.setError(null);
        height.setError(null);
        quantity.setError(null);

    }

    private void ADD(Quotation quotation)
    {
        Gson gson=new Gson();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        if(sharedPreferences.getString("quotation", null)!=null)
        {
            List<Quotation> list=new ArrayList<>();
            Type type=new TypeToken<List<Quotation>>(){}.getType();
            list=gson.fromJson(sharedPreferences.getString("quotation", null),type);

            list.add(quotation);

            String s=gson.toJson(list);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("quotation",s);
            editor.apply();
        }
        else
        {
           List<Quotation> list=new ArrayList<>();
           list.add(quotation);
            String s=gson.toJson(list);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("quotation",s);
            editor.apply();
        }


        if(sharedPreferences.getString("files", null)!=null)
        {
            List<String> list=new ArrayList<>();
            Type type=new TypeToken<List<String>>(){}.getType();
            list=gson.fromJson(sharedPreferences.getString("files", null),type);

            assert list != null;

            for(Uri x:arrayList)
            {
                list.add(String.valueOf(x));
            }

            String f=gson.toJson(list);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("files",f);
            editor.apply();
        }
        else
        {
            List<String> list=new ArrayList<>();

            for(Uri x:arrayList)
            {
                list.add(String.valueOf(x));
            }
            String f=gson.toJson(list);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("files",f);
            editor.apply();
        }


        showSnackBarMessage("Request Added to Queue!");
        Eheight.setText(null);
        Equantity.setText(null);
        Ewidth.setText(null);
        Ethickness.setText(null);

        arrayList.clear();

        FileUploadAdapter fileUploadAdapter = new FileUploadAdapter(arrayList, this, this,true);
        LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
        rvItem.setLayoutManager(LinearLayout);
        rvItem.setAdapter(fileUploadAdapter);
    }


    private View getRootView() {
        final ViewGroup contentViewGroup = (ViewGroup) Objects.requireNonNull(this).findViewById(android.R.id.content);
        View rootView = null;

        if(contentViewGroup != null)
            rootView = contentViewGroup.getChildAt(0);

        if(rootView == null)
            rootView = getWindow().getDecorView().getRootView();

        return rootView;
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(getRootView(), message,Snackbar.LENGTH_SHORT).show();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String[] mimeTypes = {"image/*", "application/pdf"};
        intent.setType("image/*|application/pdf");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent,"select file"), REQUEST_EXTERNAL_STORAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {// If request is cancelled, the result arrays are empty.
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
                            FileUploadAdapter fileUploadAdapter = new FileUploadAdapter(arrayList, this, this,true);
                            LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
                            rvItem.setLayoutManager(LinearLayout);
                            rvItem.setAdapter(fileUploadAdapter);

                        } catch (Exception e) {
                            Log.e("Upload", "File select error", e);
                        }
                    }

                } else if (data.getData() != null) {

                    final Uri uri = data.getData();
                    Log.i("Upload", "Uri = " + uri.toString());

                    try {
                        arrayList.add(uri);
                        FileUploadAdapter fileUploadAdapter = new FileUploadAdapter(arrayList, this, this,true);
                        LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
                        rvItem.setLayoutManager(LinearLayout);
                        rvItem.setAdapter(fileUploadAdapter);

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
        FileUploadAdapter fileUploadAdapter = new FileUploadAdapter(arrayList, this, this,true);
        LinearLayoutManager LinearLayout = new LinearLayoutManager(this);
        rvItem.setLayoutManager(LinearLayout);
        rvItem.setAdapter(fileUploadAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
