package com.example.karan.assignment_5;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText preview;
    int dwldState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = (EditText) findViewById(R.id.editText);
        final Button download = (Button) findViewById(R.id.button);

        if (savedInstanceState == null)
            dwldState = 0;
        else {
            dwldState = savedInstanceState.getInt("dwldState");
            if(dwldState == 0)
                download.setVisibility(View.VISIBLE);
            else
                download.setVisibility(View.GONE);
        }

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringUrl = "https://www.iiitd.ac.in/about";
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadWebpageTask().execute(stringUrl);
                    dwldState = 1;
                    download.setVisibility(View.GONE);
                } else {
                    Log.e("Error", "No network connection available.");
                }

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt("dwldState", dwldState);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading source..");
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Dialog.dismiss();
            preview.setText(result);
            Log.i("Result", result);
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HTTP Response Status", "The response is: " + response);

            is = conn.getInputStream();

            StringBuilder str = new StringBuilder();
            String line = null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            while ((line = bufferedReader.readLine()) != null) {
                str.append(line + "\n");
            }

            return str.toString();

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}
