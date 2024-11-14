package com.example.app_mobiles;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.app_mobiles.models.Comment;

public class CommentsActivity extends AppCompatActivity {
    private EditText editTextComment;
    private int postId; // ID del post al que se quiere agregar un comentario
    private ListView commentsListView;
    private ArrayAdapter<Comment> commentsAdapter;
    private ArrayList<Comment> commentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        editTextComment = findViewById(R.id.commentInput);
        Button buttonSend = findViewById(R.id.commentButton);
        commentsListView = findViewById(R.id.commentsList); // Asegúrate de tener un ListView en el layout

        // Configura el adaptador para el ListView
        commentsAdapter = new ArrayAdapter<Comment>(this, android.R.layout.simple_list_item_2, android.R.id.text1, commentsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);
                }
                Comment comment = getItem(position);
                TextView text1 = convertView.findViewById(android.R.id.text1);
                TextView text2 = convertView.findViewById(android.R.id.text2);
                text1.setText(comment.getAutor()); // Autor del comentario
                text2.setText(comment.getCuerpoComentario());   // Cuerpo del comentario
                return convertView;
            }
        };
        commentsListView.setAdapter(commentsAdapter);

        // Obtener el postId desde el intent
        postId = getIntent().getIntExtra("post_id", -1);

        // Cargar comentarios existentes
        loadComments(postId);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
    }

    private void sendComment() {
        String commentText = editTextComment.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Escribe un comentario antes de enviar", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/php-webservice/add_comment.php";
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        Log.d("CommentsActivity", "User ID: " + userId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                Toast.makeText(CommentsActivity.this, "Comentario enviado", Toast.LENGTH_SHORT).show();
                                editTextComment.setText(""); // Limpia el campo de texto
                                loadComments(postId); // Recargar comentarios después de enviar
                            } else {
                                Toast.makeText(CommentsActivity.this, "Error al enviar comentario", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentsActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("user_id", String.valueOf(userId));
                params.put("comment_text", commentText);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadComments(int postId) {
        String url = "http://10.0.2.2/php-webservice/get_comments.php?post_id=" + postId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            commentsList.clear(); // Limpiar lista antes de agregar nuevos datos

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject commentObj = jsonArray.getJSONObject(i);
                                String body = commentObj.getString("cuerpo_comentario");
                                String author = commentObj.getString("autor");
                                commentsList.add(new Comment(body, author));
                            }

                            // Actualizar el ListView con los comentarios cargados
                            updateCommentsListView();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CommentsActivity.this, "Error al procesar comentarios", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentsActivity.this, "Error al obtener comentarios", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void updateCommentsListView() {
        commentsAdapter.notifyDataSetChanged();
    }
}
