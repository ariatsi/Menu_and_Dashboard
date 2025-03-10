package fr.itds.menu_and_dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Récupérer les éléments de l'interface utilisateur
        welcomeText = findViewById(R.id.welcomeText);

        // Configure la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        String lastName = intent.getStringExtra("lastName");
        String firstName = intent.getStringExtra("firstName");
        String email = intent.getStringExtra("email");
        // Token et userId peuvent être stockés dans SharedPreferences pour un accès global
        String token = intent.getStringExtra("token");
        String userId = intent.getStringExtra("userId");

        // Stocker le token dans SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putString("userId", userId);
        editor.apply();

        // Afficher le message de bienvenue
        welcomeText.setText(String.format("Bienvenue %s %s !", firstName, lastName));
        toolbar.setTitle("Logged in as " + email);
        toolbar.setTitleTextColor(Color.WHITE);
/*
        // Si jamais nous voulons récupérez le token d'authentification et
        // l'ID de l'utilisateur stockés dans SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String userId = sharedPreferences.getString("userId", "");
*/
        fetchFiches(userId, token);
        Toast.makeText(DashboardActivity.this, "Les fiches de " + firstName + " recuperés", Toast.LENGTH_LONG).show();
        Toast.makeText(DashboardActivity.this, "Token:" + token, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.logout) {
                // Effacer le token et déconnecter l'utilisateur
                SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();

                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
        } else {
            // Ajoutez des cas pour les autres items du menu si nécessaire ...

        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchFiches(String userId, String token) {
        String url = "https://temp.itds.fr/api_records.php"; // Remplacez par l'URL de votre API
        RequestQueue queue = Volley.newRequestQueue(this);
        //Toast.makeText(DashboardActivity.this, "RequestQueue - OK", Toast.LENGTH_LONG).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        //Toast.makeText(DashboardActivity.this, "TRY : "+response, Toast.LENGTH_LONG).show();
                        JSONObject jsonResponse = new JSONObject(response);
                        Toast.makeText(DashboardActivity.this, "JsonResponse - OK", Toast.LENGTH_LONG).show();
                        int status = jsonResponse.getInt("status");
                        if (status == 200) {
                            // Traitement de la réponse
                            parseFichesResponse(jsonResponse);
                        } else {
                            // Gérer "aucune fiche trouvée"
                            showNoFichesMessage();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showNoFichesMessage();
                    }
                },
                error -> {
                    error.printStackTrace();
                    showNoFichesMessage();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userId);
                params.put("token", token);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void parseFichesResponse(JSONObject response) throws JSONException {
        ArrayList<Fiche> fiches = new ArrayList<>();
        Iterator<String> keys = response.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            if (!key.equals("status") && !key.equals("message") && !key.equals("token")) {
                JSONObject ficheJson = response.getJSONObject(key);
                Fiche fiche = new Fiche(
                        ficheJson.getString("id"),
                        ficheJson.getString("userId"),
                        ficheJson.getString("date"),
                        ficheJson.getString("title"),
                        ficheJson.getString("description"));
                fiches.add(fiche);
            }
        }

        if (fiches.isEmpty()) {
            showNoFichesMessage();
        } else {
            RecyclerView recyclerView = findViewById(R.id.fichesRecyclerView);
            FicheAdapter adapter = new FicheAdapter(fiches);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged(); // Rafraîchit la vue
            // Rendre invisible le message d'erreur et son séparateur
            TextView noFichesTextView = findViewById(R.id.noFichesTextView);
            noFichesTextView.setVisibility(View.GONE);
        }
    }

    private void showNoFichesMessage() {
        TextView noFichesTextView = findViewById(R.id.noFichesTextView);
        noFichesTextView.setVisibility(View.VISIBLE);
    }


}