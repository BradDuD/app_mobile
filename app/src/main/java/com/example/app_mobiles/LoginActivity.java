package com.example.app_mobiles;

import android.content.Intent; // Importa Intent para la redirección
import android.content.SharedPreferences; // Importa SharedPreferences para almacenar datos
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button loginButton, registerButton; // Agrega el botón de registro

    String URL = "http://10.0.2.2/php-webservice/login_user.php"; // Cambia esta URL si es necesario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button); // Inicializa el botón de registro

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = username.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                // Verifica que los campos no estén vacíos antes de llamar a loginUser
                if (!usernameInput.isEmpty() && !passwordInput.isEmpty()) {
                    loginUser(usernameInput, passwordInput);
                } else {
                    Toast.makeText(LoginActivity.this, "Por favor, ingrese su usuario y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Maneja el clic del botón de registro
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); // Cambia a la actividad de registro
                startActivity(intent);
            }
        });
    }

    private void loginUser(String username, String password) {
        String url = "http://10.0.2.2/php-webservice/login_user.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if (jsonResponse.has("success")) {
                                // Login exitoso, obtener el user_id
                                int userId = jsonResponse.getInt("user_id");

                                // Guardar user_id y username en SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", username);
                                editor.putInt("user_id", userId);
                                editor.apply();



                                // Redirigir a MainActivity o cualquier otra actividad principal
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Mostrar el error devuelto por el servidor
                                String error = jsonResponse.getString("error");
                                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
