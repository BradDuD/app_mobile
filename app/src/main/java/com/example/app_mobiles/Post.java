package com.example.app_mobiles;  // Asegúrate de que este sea el mismo paquete que tu proyecto

public class Post {
    private int id;
    private String titulo;
    private String categoria;
    private String contenido;
    private String usuario; // Agregar el nickname del usuario
    private int likeCount; // Agregar campo para el contador de likes

    public Post(int id, String titulo, String categoria, String contenido, String usuario, int likeCount) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.contenido = contenido;
        this.usuario = usuario; // Inicializar el nickname
        this.likeCount = likeCount;
    }

    public int getId() {
        return id; // Método para obtener el ID
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getContenido() {
        return contenido;
    }

    public String getUsuario() {
        return usuario; // Método para obtener el nickname
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

}

