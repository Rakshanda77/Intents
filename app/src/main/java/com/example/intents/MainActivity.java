package com.example.intents;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        ArrayList<String> url = new ArrayList<String>();


        OkHttpClient client = new OkHttpClient();
        String Url = "https://www.reddit.com/.json";
        Request req = new Request.Builder().url(Url).build();


        Thread t = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    Response response = client.newCall(req).execute();
                    String text = response.body().string();
                    Log.d("response", text);

                    JSONObject object = (JSONObject) new JSONTokener(text).nextValue();
                    JSONArray listings = object.getJSONObject("data").getJSONArray("children");


                    ArrayList<String> titles = new ArrayList<>(listings.length());
                    ArrayList<String> comments = new ArrayList<>(listings.length());

                    for (int i = 0; i < listings.length(); i++) {
                        JSONObject item = listings.getJSONObject(i);
                        titles.add(item.getJSONObject("data").getString("title"));
                        url.add(item.getJSONObject("data").getString("permalink"));
                    }
                    runOnUiThread(() -> {
                        String result = titles.stream().reduce("", (a, b) -> a += "\n" + b);


                        titles.add(result);
                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, titles);
                        listView.setAdapter(arrayAdapter);


                    });
                } catch (IOException | JSONException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    });
                }

            }
        };

        t.start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Object position = listView.getItemAtPosition(i);
                String URL = url.get(i);

                Intent intent = new Intent(MainActivity.this, FullNews.class);
                intent.putExtra("url", URL);
                startActivity(intent);
            }
        });


}
}



