package com.discussionforum.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Mensaje implements Serializable {
    private int id;
    private String contenido;
    private int autorId;
    private String autorNombre; // Nuevo campo para el nombre del autor
    private byte[] autorFoto; // Nuevo campo para la foto del autor
    private int temaId;
    private Timestamp fecha;
    private int meGusta;
    private int noMeGusta;
    private List<String> comentarios;

    public Mensaje(int id, String contenido, int autorId, int temaId, Timestamp fecha, int meGusta, int noMeGusta) {
        this.id = id;
        this.contenido = contenido;
        this.autorId = autorId;
        this.temaId = temaId;
        this.fecha = fecha;
        this.meGusta = meGusta;
        this.noMeGusta = noMeGusta;
        this.comentarios = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public int getAutorId() {
        return autorId;
    }

    public String getAutorNombre() {
        return autorNombre;
    }

    public void setAutorNombre(String autorNombre) {
        this.autorNombre = autorNombre;
    }

    public byte[] getAutorFoto() {
        return autorFoto;
    }

    public void setAutorFoto(byte[] autorFoto) {
        this.autorFoto = autorFoto;
    }

    public int getTemaId() {
        return temaId;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public int getMeGusta() {
        return meGusta;
    }

    public int getNoMeGusta() {
        return noMeGusta;
    }

    public List<String> getComentarios() {
        return comentarios;
    }

    public void agregarComentario(String comentario) {
        comentarios.add(comentario);
    }

    public void agregarMeGusta() {
        meGusta++;
    }

    public void agregarNoMeGusta() {
        noMeGusta++;
    }
}

