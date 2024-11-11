package com.example.schedify;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        String courseRegURL = "https://reg-prod.ec.tru.ca/StudentRegistrationSsb/ssb/registrationHistory/registrationHistory";

        // Create and show the WebView login dialog
        WebViewLoginDialog webViewLoginDialog = new WebViewLoginDialog(MainActivity.this, courseRegURL);
        webViewLoginDialog.show();
        boolean state = webViewLoginDialog.getState();

//        //loginPage
//        WebView webView = findViewById(R.id.webView);
//        webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript
//
//        // Set custom User-Agent if needed
//        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36";
//        webView.getSettings().setUserAgentString(userAgent);
//
//        // Set up a WebViewClient to capture the redirect after login
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                String url = request.getUrl().toString();
//                return checkLoginStatus(webView, url, courseRegURL);
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                // Log the SSL error for debugging purposes
//                Log.e("SSL_ERROR", "SSL Error: " + error.getPrimaryError());
//                handleSSLError(handler, error);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                checkLoginStatus(webView, url, courseRegURL);
//            }
//        });
//
//        webView.loadUrl(courseRegURL);
//    }
//
//    private boolean checkLoginStatus(WebView webView, String url, String courseRegURL) {
//        // Check if the URL indicates a successful login
//        if (url.equals(courseRegURL)) {
//            // Close the WebView and retrieve any needed data here
//            webView.setVisibility(View.GONE);
//            fetchUserData();
//            return true;
//        }
//        return false;
//    }
//
//    private void fetchUserData() {
//        Toast.makeText(getApplicationContext(), "Start Extracting Data!!!", Toast.LENGTH_SHORT).show();
//    }
//
//
//    private void handleSSLError(SslErrorHandler handler, SslError error) {
//        // If the error is SSL certificate validation failure, you should
//        // show a warning or block access.
//        switch (error.getPrimaryError()) {
//            case SslError.SSL_UNTRUSTED:
//                // Handle untrusted certificate (e.g., expired, self-signed)
//                // You could show a message to the user and block access
//                showSslErrorDialog("The certificate is untrusted.");
//                handler.cancel();  // Stop the WebView from loading the page
//                break;
//            case SslError.SSL_EXPIRED:
//                // Handle expired certificate
//                showSslErrorDialog("The certificate has expired.");
//                handler.cancel();
//                break;
//            case SslError.SSL_IDMISMATCH:
//                // Handle domain mismatch error
//                showSslErrorDialog("The certificate domain mismatch.");
//                handler.cancel();
//                break;
//            case SslError.SSL_NOTYETVALID:
//                // Handle certificates not yet valid
//                showSslErrorDialog("The certificate is not yet valid.");
//                handler.cancel();
//                break;
//            case SslError.SSL_INVALID:
//                // Handle any other SSL errors
//                showSslErrorDialog("An invalid SSL certificate was detected.");
//                handler.cancel();
//                break;
//            default:
//                handler.cancel(); // In case of other errors, just cancel
//                break;
//        }
//    }
//
//    private void showSslErrorDialog(String message) {
//        new AlertDialog.Builder(getApplicationContext())
//                .setTitle("SSL Error")
//                .setMessage(message)
//                .setCancelable(false)
//                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                })
//                .create()
//                .show();
    }
}