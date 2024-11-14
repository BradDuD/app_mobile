package com.example.app_mobiles.models;

public class Comment {
    private String cuerpoComentario;
    private String autor;

    public Comment(String cuerpoComentario, String autor) {
        this.cuerpoComentario = cuerpoComentario;
        this.autor = autor;
    }

    public String getCuerpoComentario() {
        return cuerpoComentario;
    }

    public void setCuerpoComentario(String cuerpoComentario) {
        this.cuerpoComentario = cuerpoComentario;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
