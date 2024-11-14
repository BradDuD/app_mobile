package com.example.app_mobiles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> posts;
    private String loggedInUser;

    public PostAdapter(Context context, List<Post> posts, String loggedInUser) {
        super(context, 0, posts);
        this.context = context;
        this.posts = posts;
        this.loggedInUser = loggedInUser; // Inicializar el nombre de usuario
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        }

        // Obtener referencia a los elementos del layout
        TextView postTitle = convertView.findViewById(R.id.postTitle);
        TextView postCategory = convertView.findViewById(R.id.postCategory);
        TextView postContent = convertView.findViewById(R.id.postContent);
        TextView postUser = convertView.findViewById(R.id.postUser);
        ImageView commentIcon = convertView.findViewById(R.id.commentIcon);
        ImageView likeIcon = convertView.findViewById(R.id.likeIcon);
        TextView likeCount = convertView.findViewById(R.id.likeCount);

        // Obtener el post actual
        final Post post = posts.get(position);

        // Asignar valores a los elementos del layout
        postTitle.setText(post.getTitulo());
        postCategory.setText(post.getCategoria());
        postContent.setText(post.getContenido());
        postUser.setText(post.getUsuario());
        likeCount.setText(String.valueOf(post.getLikeCount()));

        // Configurar el ícono de comentario
        commentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("post_id", post.getId()); // Pasar el ID del post
                context.startActivity(intent);
            }
        });

        // Configurar el ícono de like
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost(post.getId(), post, likeCount); // Pasar `likeCount` en lugar de `likeCountView`
            }
        });


        return convertView;
    }



    private void likePost(final int postId, final Post post, final TextView likeCount) {
        // Obtener el ID del usuario logueado desde SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int usuarioId = sharedPreferences.getInt("user_id", -1); // Suponiendo que guardas el ID del usuario

        String url = "http://10.0.2.2/php-webservice/like_post.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                // Actualizar el número de likes del post
                                int newLikeCount = post.getLikeCount() + 1; // Aumentar el contador de likes
                                post.setLikeCount(newLikeCount); // Actualizar en el objeto Post
                                likeCount.setText(String.valueOf(newLikeCount)); // Actualizar en la UI
                                Toast.makeText(context, "Like agregado exitosamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("usuario_id", String.valueOf(usuarioId)); // Pasar el ID del usuario
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    // Método para actualizar la lista de posts
    public void updatePosts(List<Post> newPosts) {
        posts.clear(); // Limpiar la lista actual
        posts.addAll(newPosts); // Agregar los nuevos posts
        notifyDataSetChanged(); // Notificar que los datos han cambiado
    }


}
