package fr.itds.menu_and_dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

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

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        String lastName = intent.getStringExtra("lastName");
        String firstName = intent.getStringExtra("firstName");
        String email = intent.getStringExtra("email");
        // Token peut être stocké dans SharedPreferences pour un accès global
        String token = intent.getStringExtra("token");

        // Stocker le token dans SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();

        // Afficher le message de bienvenue
        welcomeText.setText(String.format("Bienvenue %s %s !", firstName, lastName));
        toolbar.setTitle("Logged in as " + email);
        toolbar.setTitleTextColor(Color.WHITE);

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
}