package com.discussionforum.model;

import java.io.Serializable;

public class Tema implements Serializable {
    private int id;
    private String titulo;
    private int autorId;
    private String autorNombre;

    public Tema(int id, String titulo, int autorId, String autorNombre) {
        this.id = id;
        this.titulo = titulo;
        this.autorId = autorId;
        this.autorNombre = autorNombre;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getAutorId() {
        return autorId;
    }

    public String getAutorNombre() {
        return autorNombre;
    }
}
