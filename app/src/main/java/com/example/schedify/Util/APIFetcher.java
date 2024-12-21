package com.example.schedify.Util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Cookie;

public class APIFetcher {

    private ExecutorService executor = Executors.newSingleThreadExecutor(); // Single-thread executor
    private String responseData = "";
    private String requestMethod = "";

    public APIFetcher() {

    }

    // Callable task to fetch API data
    public Callable<String> fetchApiData(String apiUrl, String cookie) {
        return () -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(apiUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(requestMethod);
                urlConnection.setDoOutput(true); // Enable writing output (required for POST)
                urlConnection.setDoInput(true);  // Optional, for reading response
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Connection", "keep-alive");
                urlConnection.setRequestProperty("Cookie", cookie); // Apply the cookies to the request
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setInstanceFollowRedirects(false);  // Disable automatic redirection

                // urlConnection WILL ALWAYS FAIL IF TURN ON VPN
                int responseCode = urlConnection.getResponseCode();
                Log.d("HTTP_RESPONSE", "Response Code: " + responseCode);

                if (responseCode == 200) {
                    // Reading the response
                    InputStreamReader streamReader = new InputStreamReader(urlConnection.getInputStream());
                    reader = new BufferedReader(streamReader);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Convert response to String
                    responseData = response.toString();
                }
            } catch (Exception e) {
                Log.e("NETWORK_ERROR", "Exception: " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return responseData;  // Return populated newsList
        };
    }

    // Use Callback to wait for retrieving API Response
    public String getResponse(String apiUrl, String cookie) {
        Future<String> future = executor.submit(fetchApiData(apiUrl, cookie));  // Submit Callable task
        try {
            return future.get();  // Wait for the task to complete and get the result
        } catch (Exception e) {
            e.printStackTrace();
            return "";  // Return an empty response in case of error
        }
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String fetchDynamicHtml(String url, String cookieValue) {
        // Set the ChromeDriver executable path
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        WebDriver driver = new ChromeDriver();
        try {
            // Navigate to the target URL
            driver.get(url);

            // Set the cookie
            Cookie cookie = new Cookie.Builder("SESSIONID", cookieValue)
                    .domain("moodle.tru.ca") // Replace with the appropriate domain
                    .path("/") // Path where the cookie is valid
                    .build();
            driver.manage().addCookie(cookie);

            // Refresh the page to apply the cookie
            driver.navigate().refresh();

            // Wait for the page to fully load (use explicit wait for better control in production)
            Thread.sleep(5000); // Adjust delay based on your requirements

            // Fetch the fully rendered page source
            String pageSource = driver.getPageSource();

            return pageSource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // Quit the driver to release resources
            driver.quit();
        }
    }
}
