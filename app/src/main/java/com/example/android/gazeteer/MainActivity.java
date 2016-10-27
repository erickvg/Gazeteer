package com.example.android.gazeteer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BookAdapter adapter;

    private TextView mEmptyTextView;

    private EditText searchText;

    private String userInput;

    public static final String LOG_TAG = MainActivity.class.getName();

    private static String Books_Url_Request = "https://www.googleapis.com/books/v1/volumes?q=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.search_button);

        final ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyTextView = (TextView) findViewById(R.id.empty_view);

        bookListView.setEmptyView(mEmptyTextView);

        //adapt new BookAdapter to BookListView.

        adapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setAdapter(adapter);
        final EditText userText = (EditText)findViewById(R.id.search_text);


        //set Onclick listener to handle button  & search events.

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (userText.getText().toString().trim().equals("")) {
                    userText.setError(getString(R.string.no_search));
                } else {
                    searchText = (EditText) findViewById(R.id.search_text);
                    userInput = searchText.getText().toString().replace(" ", "+");
                    BooksAsyncTask task = new BooksAsyncTask();

                    ConnectivityManager connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connection.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                        task.execute();
                        adapter.clear();

                    } else {

                        mEmptyTextView.setText(R.string.no_connection);
                    }
                }
            }
        });

    }
    //Create Async Task for Querying Google Books API Data.

    private class BooksAsyncTask extends AsyncTask<URL, Void, List<Book>> {
        List<Book> volume = new ArrayList<>();

        @Override
        protected List<Book> doInBackground(URL... urls) {

            URL url = createUrl(Books_Url_Request + userInput + "&maxresults=10");

            //Perform HTTP request and receive a response in JSON format.

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);

            } catch (IOException e) {

                Log.e(LOG_TAG, "Http request failed, try again later", e);
                return null;

            }

            List<Book> volume = extractFeatureFromJson(jsonResponse);

            return volume;
        }

        private void updateUi(List<Book> volume) {

            adapter.clear();
            adapter.addAll(volume);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(List<Book> volume) {

            if (volume == null) {

                return;
            } else {

                updateUi(volume);
            }
        }

        private URL createUrl(String stringUrl) {

            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {

                Log.e(LOG_TAG, "Error creating with URL", e);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {

            String jsonResponse = "";

            if (url == null) {

                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                if (urlConnection.getResponseCode() == 200) {

                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {

                    Log.e(LOG_TAG, "Error Response Code" + urlConnection.getResponseCode());
                }


            } catch (IOException e) {

                Log.e(LOG_TAG, "Problem Retrieving Json Requests", e);
            } finally {

                if (urlConnection != null) {

                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {

            StringBuilder output = new StringBuilder();
            if (inputStream == null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<Book> extractFeatureFromJson(String volumeJSON) {

            try {
                JSONObject root = new JSONObject(volumeJSON);
                JSONArray items = root.getJSONArray("items");

                if (items.length()< 0)
                    for (int i = 0; i < items.length(); i++) {
                    JSONObject bookData = items.getJSONObject(i);
                    JSONObject volumeInfo = bookData.getJSONObject("volumeInfo");


                    String title = volumeInfo.getString("title");
                    String author = volumeInfo.getString("authors");


                    volume.add(new Book(title, author));

                }

                  } catch (JSONException e) {

                Log.e(LOG_TAG, "Error Parsing JSON Data", e);
            }
            return volume;
        }
    }
}










