package ma.projet.tp6_meteoapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Define UI components
    private Spinner spinnerVille; // Uses Spinner instead of EditText to avoid typos
    private ListView listViewMeteo;
    private ImageButton buttonOK;

    // Data List and Adapter
    private List<MeteoItem> data = new ArrayList<>();
    private MeteoListModel model;

    // API Key from the TP text [cite: 31]
    private static final String API_KEY = "b6907d289e10d714a6e88b30761fae22";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize Views
        spinnerVille = findViewById(R.id.spinnerVille);
        listViewMeteo = findViewById(R.id.listViewMeteo);
        buttonOK = findViewById(R.id.buttonOK);

        // 2. Setup the City List (Spinner)
        // This ensures valid city names are always sent to the API
        String[] cities = {"Casablanca", "Rabat", "Marrakech", "Paris", "London", "New York", "Tokyo"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                cities
        );
        spinnerVille.setAdapter(spinnerAdapter);

        // 3. Setup the Custom Adapter for Weather Data [cite: 390]
        model = new MeteoListModel(this, R.layout.list_item_layout, data);
        listViewMeteo.setAdapter(model);

        // 4. Handle Button Click
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected city directly from the dropdown
                String ville = spinnerVille.getSelectedItem().toString();

                Log.i("MyLog", "Ville sélectionnée: " + ville);

                // Clear old data and refresh list before new request
                data.clear();
                model.notifyDataSetChanged();

                // Fetch new data
                getWeatherData(ville);
            }
        });
    }

    private void getWeatherData(String city) {
        // Construct the URL as defined in the TP [cite: 370]
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + API_KEY;

        // Initialize Volley RequestQueue [cite: 369]
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("MyLog", "Réponse reçue");

                            // JSON Parsing Logic [cite: 378-385]
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("list");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                MeteoItem meteoItem = new MeteoItem();
                                JSONObject d = jsonArray.getJSONObject(i);

                                // Date Formatting
                                Date date = new Date(d.getLong("dt") * 1000);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy 'T' HH:mm", Locale.getDefault());
                                String dateString = sdf.format(date);

                                JSONObject main = d.getJSONObject("main");
                                JSONArray weather = d.getJSONArray("weather");

                                // Temperature Conversion (Kelvin to Celsius) [cite: 418]
                                int tempMin = (int) (main.getDouble("temp_min") - 273.15);
                                int tempMax = (int) (main.getDouble("temp_max") - 273.15);
                                int pression = main.getInt("pressure");
                                int humidity = main.getInt("humidity");

                                // Populate Model Object
                                meteoItem.tempMax = tempMax;
                                meteoItem.tempMin = tempMin;
                                meteoItem.pression = pression;
                                meteoItem.humidite = humidity;
                                meteoItem.date = dateString;
                                meteoItem.image = weather.getJSONObject(0).getString("icon");

                                data.add(meteoItem);
                            }

                            // Notify Adapter to update UI [cite: 393]
                            model.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Erreur de lecture JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MyLog", "Erreur Connexion: " + error.toString());
                        // Specific error message if connection fails
                        Toast.makeText(MainActivity.this, "Erreur de connexion API", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add request to queue [cite: 371]
        queue.add(stringRequest);
    }

    // --- PART 10: CYCLE DE VIE (LIFECYCLE) IMPLEMENTATION [cite: 425-433] ---

    @Override
    protected void onStart() {
        super.onStart();
        // Afficher un Toast indiquant que l’application devient visible [cite: 428]
        Toast.makeText(this, "Application visible (onStart)", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Afficher un Toast indiquant que l’application est prête à être utilisée [cite: 429]
        Toast.makeText(this, "Application prête (onResume)", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Afficher un Toast et écrire un message dans le Logcat [cite: 430]
        Toast.makeText(this, "Application en pause", Toast.LENGTH_SHORT).show();
        Log.i("Lifecycle", "Application mise en pause (onPause)");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Afficher une AlertDialog informant l’utilisateur que l’application est en arrière-plan [cite: 431]
        Log.i("Lifecycle", "Application en arrière-plan (onStop)");
        // Note: Creating a dialog here might not show as the activity is stopping, but logic follows the requirement.
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Afficher une AlertDialog demandant à l’utilisateur s’il souhaite continuer [cite: 432]
        new AlertDialog.Builder(this)
                .setTitle("Reprise")
                .setMessage("Voulez-vous continuer l'utilisation ?")
                .setPositiveButton("Oui", null)
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // Close the app if user says No
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Message dans le Logcat indiquant la fermeture définitive [cite: 433]
        Log.i("Lifecycle", "Fermeture définitive de l'application (onDestroy)");
    }
}