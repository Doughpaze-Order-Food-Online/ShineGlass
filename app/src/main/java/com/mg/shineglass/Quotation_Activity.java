package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mg.shineglass.utils.constants;

public class Quotation_Activity extends Activity {
    private WebView pdfView;
    private ProgressBar loading;
    private TextView quotation;
    private RelativeLayout accept,reject;
    String Quotation,url,date,total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quotation_fragment);
        Intent intent=getIntent();
        Quotation=intent.getStringExtra("quotation");
        url=intent.getStringExtra("url");
        date=intent.getStringExtra("date");
        total=intent.getStringExtra("total");

        pdfView=findViewById(R.id.webView);
        loading=findViewById(R.id.loading);
        quotation=findViewById(R.id.quotation_no_value);
        accept=findViewById(R.id.accept_btn);
        reject=findViewById(R.id.reject_btn);

        quotation.setText(Quotation);
        showPdfFile((constants.BANNER_URL+url));






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
            Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show();
        });


    }
    private void showPdfFile(final String imageString) {
        loading.setVisibility(View.VISIBLE);
        pdfView.setVisibility(View.GONE);
        pdfView.invalidate();
        pdfView.getSettings().setJavaScriptEnabled(true);
        pdfView.getSettings().setSupportZoom(true);
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
}
