package com.example.androidwebviewcertpinning;

import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String localhostEndpoint = "https://10.0.2.2:4443";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView myWebView = (WebView) findViewById(R.id.webview);

        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)  {

                validateWithBundledCertificate(view,handler, error);
            }
        });

        myWebView.loadData("<html><p><a href='" + localhostEndpoint + "/'>Go to localhost https </a></p></html>", null, "UTF-8");

    }


    public void validateWithBundledCertificate(WebView view, SslErrorHandler handler, SslError error) {

        try {
            System.out.println("Did receive challenge for " + error.getUrl());
            String sslCertificate = error.getCertificate().toString();
            System.out.println("\nCertificate received: " + sslCertificate);

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

            InputStream ins = getResources().openRawResource(R.raw.valid_cert);

            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(ins);
            String mySslCertificate = new SslCertificate(cert).toString();
            System.out.println("\nCertificate bundled: " + mySslCertificate);
            if (sslCertificate.equals(mySslCertificate)) {
                System.out.println("\nSuccessfully validated incoming certificate with bundled certificate! Allowing the connection to proceed ...\n\n");
                handler.proceed();
            }
        }
        catch (Exception ex) {
            System.out.println("\nException:" + ex.getMessage());
        }
    }

}
