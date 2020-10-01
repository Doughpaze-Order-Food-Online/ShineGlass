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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mg.shineglass.utils.constants;

public class Invoice_Activity extends Activity {
    private WebView pdfView;
    private ProgressBar loading;
    String invoice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_activity);

        Intent intent=getIntent();
        invoice=intent.getStringExtra("invoice");
        pdfView=findViewById(R.id.webView);
        loading=findViewById(R.id.loading);

        showPdfFile((constants.BANNER_URL+invoice));

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
