package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    EditText enteredittext;
    TextView showuptextview;

    String ori_url = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/O-A0001-001?Authorization=CWA-493019A1-09D7-4461-ACF6-2B72A1D406E6&format=JSON&elementName=Weather&parameterName=CITY";
    String temp;
    String town_INFO = "";
    ArrayList<String> town = new ArrayList<String>();
    ArrayList<String> town_weather = new ArrayList<String>();

    public String JSON_URL(String... urls){
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while (data != -1){
                char current = (char) data;
                result += current;
                data = reader.read();
            }
            Log.i("URL", "DONE");
            return result;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public void JSON_arr(String json){
        String city = enteredittext.getText().toString();
        Map<String, String> data = new HashMap<String, String>();
        try {
            JSONObject jsonObjecttemp = new JSONObject(json);
            String test = jsonObjecttemp.getString("records");
            JSONObject jsonObject = new JSONObject(test);
            JSONArray jsonArr = jsonObject.getJSONArray("location");

            //Log.i("TEST", test);
            //Log.i("locationName",jsonArr.toString());
            for(int i = 0; i < jsonArr.length(); i++){
                JSONObject jsonPart = jsonArr.getJSONObject(i);
                String locationName = jsonPart.getString("locationName");

                String weatherElement = jsonPart.getString("weatherElement");
                String parameter = jsonPart.getString("parameter");
                JSONArray jsonweathervaluearr = new JSONArray(weatherElement);
                JSONArray jsonparametervaluearr = new JSONArray(parameter);
                JSONObject jsonweathervalue = jsonweathervaluearr.getJSONObject(0);
                JSONObject jsonparametervalue = jsonparametervaluearr.getJSONObject(0);
                String elementValue = jsonweathervalue.getString("elementValue");
                String parameterValue = jsonparametervalue.getString("parameterValue");

                if(parameterValue.equals(city)){
                    showuptextview.setMovementMethod(new ScrollingMovementMethod());
                    town_INFO += city + locationName + " 天氣：" +elementValue + "\n";
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getWeather(View view){
        town_INFO = "";
        showuptextview.setText(town_INFO);
        JSON_arr(temp);
        showuptextview.setText(town_INFO);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enteredittext = (EditText) findViewById(R.id.entereditTextText);
        showuptextview = (TextView) findViewById(R.id.showuptextview);
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    temp = JSON_URL(ori_url);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(temp != null){

                            }
                        }
                    });
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}