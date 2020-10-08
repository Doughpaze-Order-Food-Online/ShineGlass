package com.mg.shineglass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class Quotation_Activity extends Activity {
    private WebView pdfView;
    private ProgressBar loading;
    private TextView quotation;
    private RelativeLayout accept,reject;
    String Quotation,url,date,total,Accept,Reject,valuation;
    private SwipeRefreshLayout refreshLayout;
    private ViewDialog viewDialog;
    private CompositeSubscription mSubscriptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quotation_fragment);
        Intent intent=getIntent();
        Quotation=intent.getStringExtra("quotation");
        url=intent.getStringExtra("url");
        date=intent.getStringExtra("date");
        total=intent.getStringExtra("total");
        Accept=intent.getStringExtra("accept");
        Reject=intent.getStringExtra("reject");
        valuation=intent.getStringExtra("valuation");

        pdfView=findViewById(R.id.webView);
        loading=findViewById(R.id.loading);
        quotation=findViewById(R.id.quotation_no_value);
        accept=findViewById(R.id.accept_btn);
        reject=findViewById(R.id.reject_btn);

        if(Accept.equals("true") && Reject.equals("false"))
        {
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
        }

        if(Accept.equals("false") && Reject.equals("true"))
        {
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
        }



        quotation.setText(Quotation);
        showPdfFile((constants.BANNER_URL+url));
        viewDialog = new ViewDialog(this);
        mSubscriptions = new CompositeSubscription();






        accept.setOnClickListener(v -> {
            Intent i=new Intent(Quotation_Activity.this, My_Address_Activity.class);
            i.putExtra("quotation",Quotation);
            i.putExtra("total",total);
            i.putExtra("url",url);
            i.putExtra("date",date);
            startActivity(i);
            finish();
        });

        reject.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure??")
                    .setMessage("Do you want to reject??")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            REJECT_QUOTATION(Quotation);
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });


    }

    private void showPdfFile(final String imageString) {
        loading.setVisibility(View.VISIBLE);
        pdfView.setVisibility(View.GONE);
        pdfView.invalidate();
        pdfView.getSettings().setJavaScriptEnabled(true);
        pdfView.getSettings().setSupportZoom(true);
        pdfView.getSettings().setBuiltInZoomControls(true);
        pdfView.getSettings().setDisplayZoomControls(false);
        pdfView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + imageString);
        pdfView.setWebViewClient(new WebViewClient() {
            boolean checkOnPageStartedCalled = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                checkOnPageStartedCalled = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (checkOnPageStartedCalled) {
                    loading.setVisibility(View.GONE);
                    pdfView.setVisibility(View.VISIBLE);
                } else {
                    showPdfFile(imageString);
                }
            }
        });


    }
    private void REJECT_QUOTATION(String quotationNo) {


        viewDialog.showDialog();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mSubscriptions.add(networkUtils.getRetrofit(sharedPreferences.getString("token", null))
                .REJECT_QUOTATION(quotationNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(BasicResponse response) {

        viewDialog.hideDialog();
        Intent i=new Intent(Quotation_Activity.this,MainActivity.class);
        startActivity(i);
        finish();
        Toast.makeText(this, "Quotation Rejected! Pull Down to Refresh", Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable error) {

        viewDialog.hideDialog();
        Log.e("error", error.toString());
        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response<BasicResponse> response = gson.fromJson(errorBody, Response.class);
                assert response.body() != null;

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(this, "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }

}
