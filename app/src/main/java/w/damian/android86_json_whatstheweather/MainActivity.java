package w.damian.android86_json_whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText editText;
    TextView weatherTextView;
    String weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weather = "";
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        weatherTextView = findViewById(R.id.weatherTextView);
    }

    public void clearText(View view){
        editText.setText("");
        weatherTextView.setText("");
    }

    public void getWeather(View v){
        DownloadTask task = new DownloadTask();
        String s = null;
        String city = editText.getText().toString();
        if(city != ""){
            try {
                s = task.execute("http://api.openweathermap.org/data/2.5/weather?q="+ city +"&appid=179e68c6699a03b9974d8842e670f3e3").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // Check if no view has focus:
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                StringBuffer sb = new StringBuffer();

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    sb.append(current);
                    data = reader.read();
                }
                result = sb.toString();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String weatherInfo = jsonObject.getString("weather");

                    JSONArray arr = new JSONArray(weatherInfo);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < arr.length(); i++) {

                        JSONObject jsonPart = arr.getJSONObject(i);
                        sb.append(jsonPart.getString("main"));
                        sb.append("\n");
    //                    stringBuilder.append(jsonPart.getString("description"));
    //                    stringBuilder.append(" ");
                    }

                    String mainInfo = jsonObject.getString("main");
                    JSONObject mainInfoJSON = new JSONObject(mainInfo);

                    String kelvinTemperature = mainInfoJSON.getString("temp");
                    String pressure = mainInfoJSON.getString("pressure");
                    String humidity = mainInfoJSON.getString("humidity");

                    double temp = Double.parseDouble(kelvinTemperature) - 273.15;

                    sb.append("Temperature: ").append(String.format("%.2f", temp)).append(" \u00b0").append("C");
                    sb.append("\n");
                    sb.append("Pressure: ").append(pressure).append(" hPa");
                    sb.append("\n");
                    sb.append("Humidity: ").append(humidity).append(" %");

                    System.out.println(mainInfo);
                    weather = sb.toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (weather != null){
                    weatherTextView.setText(weather);
                }
            } else {
                editText.setText("Unsuported city!");
            }
        }
    }
}
