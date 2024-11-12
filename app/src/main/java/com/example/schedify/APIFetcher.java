package com.example.schedify;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class APIFetcher {

    private ExecutorService executor = Executors.newSingleThreadExecutor(); // Single-thread executor
    private String responseData = "";

    public APIFetcher() {

    }

    // Callable task to fetch API data
    public Callable<String> fetchApiData(String apiUrl) {
        return () -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(apiUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setInstanceFollowRedirects(false);  // Disable automatic redirection

                // urlConnection WILL ALWAYS FAIL IF TURN ON VPN
                int responseCode = urlConnection.getResponseCode();
                Log.d("HTTP_RESPONSE", "Response Code: " + responseCode);

                // Check if redirect (e.g., 3xx status code)
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                        responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                        responseCode == HttpURLConnection.HTTP_SEE_OTHER) {

                    // Get the new location from the "Location" header
                    String newUrl = urlConnection.getHeaderField("Location");
                    Log.d("HTTP_REDIRECT", "Redirected to: " + newUrl);

                    // Optionally, create a new connection to handle the redirect if needed
                    // For example:
//                    urlConnection.disconnect();  // Disconnect current connection
                    url = new URL(apiUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    responseCode = urlConnection.getResponseCode();
                    Log.d("HTTP_RESPONSE_AFTER_REDIRECT", "Response Code after redirect: " + responseCode);
                }

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

                } else {
                    Log.e("API_ERROR", "Response Code: " + responseCode + ", Message: " + urlConnection.getResponseMessage());
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
    public String getResponse(String apiUrl) {
        Future<String> future = executor.submit(fetchApiData(apiUrl));  // Submit Callable task
        try {
            return future.get();  // Wait for the task to complete and get the result
        } catch (Exception e) {
            e.printStackTrace();
            return "";  // Return an empty response in case of error
        }
    }
}
