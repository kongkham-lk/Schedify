package com.example.schedify;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatDelegate;
import android.webkit.CookieManager;

public class WebViewLoginDialog extends Dialog {

    public interface LoginCallback {
        void onLoginResult(boolean isSuccess);
    }

    private LoginCallback loginCallback;
    private WebView webView;
    private ImageButton backBtn;
    private String targetURL;
    private Context context;
    private String cookie;

    public WebViewLoginDialog(Context context, String targetURL, LoginCallback loginCallback) {
        super(context);
        this.context = context;
        this.targetURL = targetURL;
        this.loginCallback = loginCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);

        // Remove the default title bar for the dialog
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login); // The layout contains a WebView

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Cancel Login!!!", Toast.LENGTH_SHORT).show();
                onBackPressed();
                loginCallback.onLoginResult(false);
            }
        });

        // Initialize the WebView
        webView = findViewById(R.id.webViewScreen);
        webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Check if the user has navigated to the login success page
                if (url.equals(targetURL)) {
                    // Sync WebView cookies to the app
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookies = cookieManager.getCookie(url);

                    // Save the cookies or apply them to the next request
                    setCookie(cookies);

                    // If login is successful, dismiss the dialog
                    dismiss();
                    if (loginCallback != null) {
                        loginCallback.onLoginResult(true);
                    }
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // Log the SSL error for debugging purposes
                Log.e("SSL_ERROR", "SSL Error: " + error.getPrimaryError());
//                handleSSLError(handler, error);
                handler.proceed(); // ignore error and proceed loading login page
            }
        });

        // Load the initial login URL
        webView.loadUrl(targetURL);

        // Customize the dialog to appear as a floating window
        Window window = getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT); // Full-screen dialog
            window.setGravity(Gravity.CENTER); // Center the dialog on the screen
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL); // Non-modal behavior
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void handleSSLError(SslErrorHandler handler, SslError error) {
        // If the error is SSL certificate validation failure, you should show a warning or block access.
        switch (error.getPrimaryError()) {
            case SslError.SSL_UNTRUSTED:
                // Handle untrusted certificate (e.g., expired, self-signed)
                // You could show a message to the user and block access
                showSslErrorDialog("The certificate is untrusted.");
                break;
            case SslError.SSL_EXPIRED:
                // Handle expired certificate
                showSslErrorDialog("The certificate has expired.");
                break;
            case SslError.SSL_IDMISMATCH:
                // Handle domain mismatch error
                showSslErrorDialog("The certificate domain mismatch.");
                break;
            case SslError.SSL_NOTYETVALID:
                // Handle certificates not yet valid
                showSslErrorDialog("The certificate is not yet valid.");
                break;
            case SslError.SSL_INVALID:
                // Handle any other SSL errors
                showSslErrorDialog("An invalid SSL certificate was detected.");
                break;
            default:
                break;
        }
//        handler.cancel();  // Stop the WebView from loading the page
    }

    // Error dialog for display on screen
    private void showSslErrorDialog(String message) {
        new AlertDialog.Builder(context)
                .setTitle("SSL Error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return this.cookie;
    }

    public String getTargetURL() {
        return targetURL;
    }

    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }
}

