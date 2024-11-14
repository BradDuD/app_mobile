package com.example.app_mobiles;

import android.content.Intent;
import android.content.SharedPreferences; // Importa SharedPreferences
import android.os.Bundle;
import android.util.Log;  // Asegúrate de importar la clase Log
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.widget.EditText;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.example.app_mobiles.models.Comment;
import com.example.app_mobiles.models.Tag;


public class MainActivity extends AppCompatActivity {

    ListView listViewPosts;
    List<Post> postsList;
    PostAdapter adapter;
    int userId;
    String URL = "http://10.0.2.2/php-webservice/get_posts.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewPosts = findViewById(R.id.listViewPosts);
        postsList = new ArrayList<>();

        // Recuperar el nombre de usuario y el user_id de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("username", null); // Obtener el nombre de usuario
        int userId = sharedPreferences.getInt("user_id", -1); // Obtener el user_id

        // Crear el adaptador para la lista usando PostAdapter
        adapter = new PostAdapter(this, postsList, loggedInUser);
        listViewPosts.setAdapter(adapter);

        // Llamar al método para obtener los posts
        getPosts();

        // Inicializar el botón flotante para crear un nuevo post
        FloatingActionButton fabNewPost = findViewById(R.id.fabNewPost);
        fabNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de publicar un nuevo post si el usuario está logueado
                if (loggedInUser != null) {
                    Intent intent = new Intent(MainActivity.this, PublishBlogActivity.class);
                    intent.putExtra("username", loggedInUser); // Pasar el nombre de usuario
                    startActivityForResult(intent, 1);
                } else {
                    Toast.makeText(MainActivity.this, "Usuario no logueado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fabSearchUser = findViewById(R.id.fabSearchUser);
        fabSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserSearchDialog();
            }
        });
    }

        // Método para mostrar el diálogo de búsqueda
        private void showUserSearchDialog () {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Buscar Usuario");

            // Configuración del EditText en el diálogo
            final EditText input = new EditText(this);
            input.setHint("Nombre de usuario");
            builder.setView(input);

            // Configuración de los botones
            builder.setPositiveButton("Buscar", (dialog, which) -> {
                String username = input.getText().toString().trim();
                if (!username.isEmpty()) {
                    searchPostsByUser(username);
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

            builder.show();
        }


    private void searchPostsByUser(String username) {
        String url = "http://10.0.2.2/php-webservice/get_user_posts.php?username=" + username;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Procesa la respuesta y actualiza el ListView con los posts de ese usuario
                    List<Post> posts = parsePostsResponse(response);
                    adapter.updatePosts(posts);
                },
                error -> Toast.makeText(MainActivity.this, "Error al obtener posts", Toast.LENGTH_SHORT).show());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Método para parsear la respuesta del servidor
    private List<Post> parsePostsResponse(String response) {
        List<Post> posts = new ArrayList<>();
        Log.d("parsePostsResponse", "Response JSON: " + response); // Imprime la respuesta JSON
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Post post = new Post(
                        jsonObject.getInt("id"),
                        jsonObject.getString("titulo"),
                        jsonObject.getString("categoria"),
                        jsonObject.getString("contenido"),
                        jsonObject.getString("autor"),  // Cambiado de 'usuario' a 'autor'
                        jsonObject.getInt("like_count") // Cambiado de 'likeCount' a 'like_count'
                );
                posts.add(post);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return posts;
    }





    // Función likePost para enviar también el user_id del usuario logueado
    private void likePost(final int postId, final TextView likeCountView) {
        String url = "http://10.0.2.2/php-webservice/like_post.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LikePost", "Respuesta: " + response);
                        // Incrementar el conteo de likes en la UI
                        int currentLikes = Integer.parseInt(likeCountView.getText().toString());
                        likeCountView.setText(String.valueOf(currentLikes + 1));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("usuario_id", String.valueOf(userId)); // Agrega el user_id como parámetro
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }



    private void getPosts() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MainActivity", "Respuesta: " + response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<Post> posts = new ArrayList<>(); // Crear una nueva lista de posts
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject post = jsonArray.getJSONObject(i);



                                // Ajusta las claves según el formato del JSON
                                String titulo = post.getString("titulo");
                                String contenido = post.getString("contenido");
                                String categoria = post.getString("categoria");
                                String autor = post.getString("autor"); // Cambiado de "usuario" a "autor"
                                int likeCount = post.getInt("like_count");
                                int postId = post.getInt("id"); // Suponiendo que tu JSON tiene un campo "id"

                                // Crear un nuevo objeto Post
                                posts.add(new Post(postId, titulo, categoria, contenido, autor, likeCount));

                            }

                            // Actualiza el adaptador con la nueva lista de posts
                            adapter.updatePosts(posts); // Método para actualizar la lista en el adaptador
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error en el formato de respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainActivity", "Error de red: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getPostDetails(int postId) {
        String url = "http://10.0.2.2/php-webservice/get_post_details.php?post_id=" + postId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MainActivity", "Post Details Response: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            // Obtener el post
                            JSONObject postObject = jsonResponse.getJSONObject("post");

                            // Obtener comentarios
                            JSONArray comentariosArray = jsonResponse.getJSONArray("comentarios");
                            ArrayList<Comment> comments = new ArrayList<>();
                            for (int i = 0; i < comentariosArray.length(); i++) {
                                JSONObject comentario = comentariosArray.getJSONObject(i);
                                String cuerpoComentario = comentario.getString("cuerpo_comentario");
                                String autorComentario = comentario.getString("autor");
                                comments.add(new Comment(cuerpoComentario, autorComentario));
                            }

                            // Obtener etiquetas
                            JSONArray etiquetasArray = jsonResponse.getJSONArray("etiquetas");
                            ArrayList<String> etiquetas = new ArrayList<>();
                            for (int i = 0; i < etiquetasArray.length(); i++) {
                                etiquetas.add(etiquetasArray.getJSONObject(i).getString("nombre_etiqueta"));
                            }

                            // Aquí puedes actualizar la UI para mostrar los comentarios y etiquetas.
                            // Ejemplo: mostrar en un TextView o ListView los comentarios y etiquetas.

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error en el formato de la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainActivity", "Error de red: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }







    // Método para manejar el resultado de PublishBlogActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            getPosts(); // Llamar a getPosts para actualizar la lista de posts
        }
    }
}
