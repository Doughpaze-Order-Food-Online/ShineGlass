package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mg.shineglass.utils.constants;

public class Invoice_Activity extends AppCompatActivity implements ViewTreeObserver.OnScrollChangedListener {
    private WebView pdf;
    private ProgressBar loading;
    String invoice;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_activity);

        Intent intent=getIntent();
        invoice=intent.getStringExtra("invoice");
        pdf=findViewById(R.id.webView);
        loading=findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        pdf.setVisibility(View.GONE);
        pdf.getSettings().setJavaScriptEnabled(true);
        pdf.getSettings().setBuiltInZoomControls(true);
        pdf.getSettings().setDisplayZoomControls(false);
        pdf.loadUrl("https://docs.google.com/gview?embedded=true&url="+ constants.BANNER_URL+invoice);

        pdf.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loading.setVisibility(View.GONE);
                pdf.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(invoice);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(Invoice_Activity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
        refreshLayout=findViewById(R.id.refresh);



        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               pdf.reload();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onScrollChanged() {
        if (pdf.getScrollY() == 0) {
            refreshLayout.setEnabled(true);
        } else {
            refreshLayout.setEnabled(false);
        }
    }
}
