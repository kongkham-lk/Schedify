package com.example.schedify;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.webkit.CookieManager;

import org.json.JSONObject;

public class WebViewLoginDialog extends Dialog {

    public interface LoginCallback {
        void onLoginResult(boolean isSuccess);
    }

    private LoginCallback loginCallback;
    private WebView webView;
    private ImageButton backBtn;
    private String initialURL;
    private String finalURL;
    private Context context;
    private String cookie;
    private JSONObject jsonObject;
    private int attemptCount = 3;

    public WebViewLoginDialog(Context context, String initialURL, String finalURL, LoginCallback loginCallback) {
        super(context);
        this.context = context;
        this.initialURL = initialURL;
        this.finalURL = finalURL;
        this.loginCallback = loginCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

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



        // Customize the dialog to appear as a floating window
        Window window = getWindow();
        if (window != null) {
            // Access the WindowManager from the dialog's context
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

            window.setLayout(0, 0); // Full-screen dialog
            window.setGravity(Gravity.CENTER); // Center the dialog on the screen
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL); // Non-modal behavior
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Check if the user has navigated to the login success page
                if (url.equals(initialURL)) {
                    // Sync WebView cookies to the app
                    String cookies = cookieManager.getCookie(url);

                    // Save the cookies or apply them to the next request
                    cookieManager.setCookie(url, cookies);

                    // If login is successful, dismiss the dialog
                    if (webView.getVisibility() == View.VISIBLE)
                        dismiss();

                    if (!url.equals(finalURL))
                        webView.loadUrl(finalURL);
                    else
                        extractHTMLFromWebView();
                } else if (url.equals(finalURL)) {
                    extractJsonDataFromWebView();
                } else {
                    // Customize the dialog to appear as a floating window
//                    Window window = getWindow();
                    if (window != null) {
                        // Access the WindowManager from the dialog's context
                        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        if (windowManager != null) {
                            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                        }

                        int screenWidth = displayMetrics.widthPixels;
                        int screenHeight = displayMetrics.heightPixels;

                        int desiredWidth = (int) (screenWidth * 0.9); // 80% of the screen width
                        int desiredHeight = (int) (screenHeight * 0.7); // 60% of the screen height

                        window.setLayout(desiredWidth, desiredHeight); // Full-screen dialog
                    }

//                    LayoutInflater inflater = getLayoutInflater();
//                    View cardLayout = inflater.inflate(R.layout.dialog_login, null);
//                    CardView cardView = cardLayout.findViewById(R.id.cardView);
//                    cardView.setVisibility(View.VISIBLE);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush(); // Sync cookies for Lollipop and above
        } else {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(webView.getContext());
            cookieSyncManager.startSync(); // Sync cookies for older versions
        }

        // Load the initial login URL
        webView.loadUrl(initialURL);
    }

    private void extractJsonDataFromWebView() {
        webView.evaluateJavascript(
                "(function() { return document.body.innerText; })();",
                jsonData -> {
                    // Process the JSON data
                    Log.d("WebView", "Extracted JSON: " + jsonData);

                    // Example: Parse the JSON (remove quotes from the response string)
                    try {
                        String cleanJson = jsonData.replaceAll("^\"|\"$", "").replace("\\n", "").replace("\\\"", "\"").replace("  ", "");
                        JSONObject jsonObject = new JSONObject(cleanJson);
                        Helper.saveJsonToPreferences(context, jsonObject);
                        loginCallback.onLoginResult(true);
                    } catch (Exception e) {
                        Log.e("WebView", "Error parsing JSON: " + e.getMessage());
                    }
                }
        );
    }

    private void extractHTMLFromWebView() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            webView.evaluateJavascript(
                    "(function() {\n" +
                            "    var html = document.documentElement.outerHTML;\n" +
                            "    return html;\n" +
                            "})();\n",
                    html -> {
                        html = html
                                .replace("\\u003C", "<")
                                .replace("\\n", "")
                                .replace("\\\"", "\"")
                                .replace("\"<", "<")
                                .replace(">\"", ">");
                        if (html != null && !html.equals("null") && html.contains("event-list-wrapper")) {
                            // Successfully retrieved HTML
                            Log.d("WebView", "HTML Content: " + html); // Logging the HTML for debugging
                            Helper.saveHTMLToPreferences(context, html);
                            loginCallback.onLoginResult(true); // Proceed after data is loaded
                        } else {
                            // Retry if not found
                            if (attemptCount > 0) {
                                attemptCount--;
                                new Handler(Looper.getMainLooper()).postDelayed(this::extractHTMLFromWebView, 1000); // Retry after 1 second
                            } else {
                                Helper.saveHTMLToPreferences(context, html);
                                loginCallback.onLoginResult(false); // Indicate failure after max attempts
                            }
                        }

                    }
            );
        }, 5000);
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

    public String getInitialURL() {
        return initialURL;
    }

    public void setInitialURL(String initialURL) {
        this.initialURL = initialURL;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}

