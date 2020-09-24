package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Quotation_Activity extends Activity {
    private WebView pdf;
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

        pdf=findViewById(R.id.webView);
        loading=findViewById(R.id.loading);
        quotation=findViewById(R.id.quotation_no_value);
        accept=findViewById(R.id.accept_btn);
        reject=findViewById(R.id.reject_btn);

        quotation.setText(Quotation);
        loading.setVisibility(View.VISIBLE);
      pdf.setVisibility(View.GONE);
      pdf.getSettings().setJavaScriptEnabled(true);
      pdf.getSettings().setBuiltInZoomControls(true);
      pdf.getSettings().setDisplayZoomControls(false);
        pdf.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);

        pdf.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loading.setVisibility(View.GONE);
                pdf.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }
        });

        accept.setOnClickListener(v -> {
            Intent i=new Intent(Quotation_Activity.this, My_Address_Actiivity.class);
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
}
