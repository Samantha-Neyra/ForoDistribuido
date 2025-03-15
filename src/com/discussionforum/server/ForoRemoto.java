package com.discussionforum.server;

import com.discussionforum.model.Tema;
import com.discussionforum.model.Mensaje;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ForoRemoto extends Remote {
    boolean registrarUsuario(byte[] foto, String nombre, String apellido, String usuario, String contraseña) throws RemoteException;
    boolean iniciarSesion(String usuario, String contraseña) throws RemoteException;
    List<Tema> obtenerTemas() throws RemoteException;
    boolean crearTema(String titulo, int autorId, String autorNombre) throws RemoteException;
    List<Mensaje> obtenerMensajes(int temaId) throws RemoteException;
    boolean publicarMensaje(String contenido, int autorId, int temaId) throws RemoteException;
    boolean agregarMeGusta(int mensajeId, int usuarioId) throws RemoteException;
    boolean agregarNoMeGusta(int mensajeId, int usuarioId) throws RemoteException;
    boolean quitarMeGusta(int mensajeId, int usuarioId) throws RemoteException;
    boolean quitarNoMeGusta(int mensajeId, int usuarioId) throws RemoteException;
    boolean agregarComentario(int mensajeId, String comentario) throws RemoteException;
    String obtenerNombreUsuario(int userId) throws RemoteException;
    int obtenerIdUsuario(String usuario) throws RemoteException;
    byte[] obtenerFotoUsuario(int userId) throws RemoteException;
    int obtenerVotoUsuario(int mensajeId, int usuarioId) throws RemoteException; // Nuevo método para obtener el voto del usuario
    List<String> obtenerComentarios(int mensajeId) throws RemoteException; // Nuevo método para obtener comentarios
}

