package app.beermecum.com.beermecum.connecttion;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectionBeerDatabase {
    private final String LOG_TAG = HttpConnectionBeerDatabase.class.getSimpleName();

    public HttpConnectionBeerDatabase() {
    }

    public String connectToUrlAndObtainResponseString(String urlString, int page) throws EmptyResponseException, IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String responseJsonStr = null;

        try {


            Uri builtUri = Uri.parse(urlString + "?page=" + page).buildUpon().build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenBeerDatabase, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                throw new EmptyResponseException();
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                throw new EmptyResponseException();
            }
            responseJsonStr = buffer.toString();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return responseJsonStr;
    }
}