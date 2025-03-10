package fr.itds.menu_and_dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button login;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in... Please wait.");
        progressDialog.setCancelable(false);

        requestQueue = Volley.newRequestQueue(this);

        login.setOnClickListener(v -> {
            String userVar = username.getText().toString();
            String passVar = password.getText().toString();
            if (userVar.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username cannot be blank", Toast.LENGTH_SHORT).show();
            } else if (passVar.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
            } else {
                loginRequest(userVar, passVar);
            }
        });
    }

    private void loginRequest(String userVar, String passVar) {
        String loginUrl = "https://temp.itds.fr/login_tst.php"; // your server's URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");

                        if (status == 200) { // Success
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Proceed to next activity or dashboard
                            String userId = jsonObject.getString("id");
                            String lastName = jsonObject.getString("lastName");
                            String firstName = jsonObject.getString("firstName");
                            String email = jsonObject.getString("email");
                            String role = jsonObject.getString("role");
                            String token = jsonObject.getString("token");

                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("lastName", lastName);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("email", email);
                            intent.putExtra("role", role);
                            intent.putExtra("token", token);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Failed to parse server response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Failed to connect to server. Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", userVar);
                params.put("password", passVar);
                return params;
            }
        };

        progressDialog.show();
        requestQueue.add(stringRequest);
    }
}