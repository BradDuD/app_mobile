package com.example.app_mobiles;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class PublishBlogActivity extends AppCompatActivity {

    EditText blogTitle, blogContent;
    Spinner categorySpinner;
    Button publishButton;
    String URL = "http://10.0.2.2/php-webservice/publish.php"; // Cambiar la URL si es necesario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_blog2); // Cargar el layout correcto

        // Encuentra los elementos en el layout
        blogTitle = findViewById(R.id.blog_title);
        blogContent = findViewById(R.id.blog_content);
        categorySpinner = findViewById(R.id.spinner_category);
        publishButton = findViewById(R.id.go_to_publish_button);

        // Configura el botón de publicar
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    publishPost();
                } catch (Exception e) {
                    // Mostrar cualquier excepción que ocurra
                    Toast.makeText(PublishBlogActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void publishPost() {
        final String title = blogTitle.getText().toString().trim();
        final String content = blogContent.getText().toString().trim();
        final int selectedCategoryId = categorySpinner.getSelectedItemPosition() + 1; // Obtener el ID de la categoría (posición + 1)

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        final int userId = sharedPreferences.getInt("user_id", -1); // Obtener el ID del usuario logueado

        // Validar que los campos no estén vacíos
        if (title.isEmpty() || content.isEmpty() || selectedCategoryId <= 0) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Realizar la solicitud para publicar el post
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("PublishBlogActivity", "Server response: " + response);
                        Toast.makeText(PublishBlogActivity.this, "Publicación exitosa", Toast.LENGTH_LONG).show();

                        // Establecer resultado y finalizar la actividad
                        setResult(RESULT_OK);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Mostrar el error en un Toast
                Toast.makeText(PublishBlogActivity.this, "Error de red: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("titulo", title);
                params.put("contenido", content);
                params.put("categoria_id", String.valueOf(selectedCategoryId)); // Enviar el ID de la categoría seleccionada
                params.put("usuario_id", String.valueOf(userId)); // Enviar el ID del usuario
                return params;
            }
        };

        // Añadir la solicitud a la cola de solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
