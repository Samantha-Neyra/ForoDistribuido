package com.discussionforum.server;

import com.discussionforum.model.Mensaje;
import com.discussionforum.model.Tema;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForoRemotoImpl extends UnicastRemoteObject implements ForoRemoto {

    private Connection connection;

    protected ForoRemotoImpl() throws RemoteException {
        super();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ForoDB", "root", "root");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean registrarUsuario(byte[] foto, String nombre, String apellido, String usuario, String contraseña) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Usuarios (foto, nombre, apellido, usuario, contraseña) VALUES (?, ?, ?, ?, ?)");
            ps.setBytes(1, foto);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, usuario);
            ps.setString(5, contraseña);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean iniciarSesion(String usuario, String contraseña) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Usuarios WHERE usuario = ? AND contraseña = ?");
            ps.setString(1, usuario);
            ps.setString(2, contraseña);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Tema> obtenerTemas() throws RemoteException {
        List<Tema> temas = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT t.id, t.titulo, t.autor_id, u.usuario AS autor_nombre FROM Temas t JOIN Usuarios u ON t.autor_id = u.id");
            while (rs.next()) {
                temas.add(new Tema(rs.getInt("id"), rs.getString("titulo"), rs.getInt("autor_id"), rs.getString("autor_nombre")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temas;
    }

    @Override
    public boolean crearTema(String titulo, int autorId, String autorNombre) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Temas (titulo, autor_id) VALUES (?, ?)");
            ps.setString(1, titulo);
            ps.setInt(2, autorId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Mensaje> obtenerMensajes(int temaId) throws RemoteException {
        List<Mensaje> mensajes = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT m.id, m.contenido, m.autor_id, m.tema_id, m.fecha, m.me_gusta, m.no_me_gusta, " +
                "u.usuario AS autor_nombre, u.foto AS autor_foto " +
                "FROM Mensajes m " +
                "JOIN Usuarios u ON m.autor_id = u.id " +
                "WHERE m.tema_id = ?");
            ps.setInt(1, temaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Mensaje mensaje = new Mensaje(
                    rs.getInt("id"),
                    rs.getString("contenido"),
                    rs.getInt("autor_id"),
                    rs.getInt("tema_id"),
                    rs.getTimestamp("fecha"),
                    rs.getInt("me_gusta"),
                    rs.getInt("no_me_gusta")
                );
                mensaje.setAutorNombre(rs.getString("autor_nombre")); // Establece el nombre del autor
                mensaje.setAutorFoto(rs.getBytes("autor_foto")); // Establece la foto del autor
                mensajes.add(mensaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mensajes;
    }

    @Override
    public boolean publicarMensaje(String contenido, int autorId, int temaId) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Mensajes (contenido, autor_id, tema_id) VALUES (?, ?, ?)");
            ps.setString(1, contenido);
            ps.setInt(2, autorId);
            ps.setInt(3, temaId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean agregarMeGusta(int mensajeId, int usuarioId) throws RemoteException {
        try {
            int votoActual = obtenerVotoUsuario(mensajeId, usuarioId);
            if (votoActual == 1) {
                return quitarMeGusta(mensajeId, usuarioId);
            } else {
                if (votoActual == -1) {
                    quitarNoMeGusta(mensajeId, usuarioId);
                }
                PreparedStatement ps = connection.prepareStatement("INSERT INTO Votos (usuario_id, mensaje_id, voto) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE voto = 1");
                ps.setInt(1, usuarioId);
                ps.setInt(2, mensajeId);
                ps.executeUpdate();
                actualizarConteoMeGusta(mensajeId);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean agregarNoMeGusta(int mensajeId, int usuarioId) throws RemoteException {
        try {
            int votoActual = obtenerVotoUsuario(mensajeId, usuarioId);
            if (votoActual == -1) {
                return quitarNoMeGusta(mensajeId, usuarioId);
            } else {
                if (votoActual == 1) {
                    quitarMeGusta(mensajeId, usuarioId);
                }
                PreparedStatement ps = connection.prepareStatement("INSERT INTO Votos (usuario_id, mensaje_id, voto) VALUES (?, ?, -1) ON DUPLICATE KEY UPDATE voto = -1");
                ps.setInt(1, usuarioId);
                ps.setInt(2, mensajeId);
                ps.executeUpdate();
                actualizarConteoNoMeGusta(mensajeId);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean quitarMeGusta(int mensajeId, int usuarioId) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Votos WHERE usuario_id = ? AND mensaje_id = ? AND voto = 1");
            ps.setInt(1, usuarioId);
            ps.setInt(2, mensajeId);
            ps.executeUpdate();
            actualizarConteoMeGusta(mensajeId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean quitarNoMeGusta(int mensajeId, int usuarioId) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Votos WHERE usuario_id = ? AND mensaje_id = ? AND voto = -1");
            ps.setInt(1, usuarioId);
            ps.setInt(2, mensajeId);
            ps.executeUpdate();
            actualizarConteoNoMeGusta(mensajeId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int obtenerVotoUsuario(int mensajeId, int usuarioId) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT voto FROM Votos WHERE usuario_id = ? AND mensaje_id = ?");
            ps.setInt(1, usuarioId);
            ps.setInt(2, mensajeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("voto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void actualizarConteoMeGusta(int mensajeId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE Mensajes SET me_gusta = (SELECT COUNT(*) FROM Votos WHERE mensaje_id = ? AND voto = 1) WHERE id = ?");
        ps.setInt(1, mensajeId);
        ps.setInt(2, mensajeId);
        ps.executeUpdate();
    }

    private void actualizarConteoNoMeGusta(int mensajeId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE Mensajes SET no_me_gusta = (SELECT COUNT(*) FROM Votos WHERE mensaje_id = ? AND voto = -1) WHERE id = ?");
        ps.setInt(1, mensajeId);
        ps.setInt(2, mensajeId);
        ps.executeUpdate();
    }

    @Override
    public boolean agregarComentario(int mensajeId, String comentario) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Comentarios (mensaje_id, comentario) VALUES (?, ?)");
            ps.setInt(1, mensajeId);
            ps.setString(2, comentario);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String obtenerNombreUsuario(int userId) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT CONCAT(nombre, ' ', apellido) AS nombre_completo FROM Usuarios WHERE id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre_completo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int obtenerIdUsuario(String usuario) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT id FROM Usuarios WHERE usuario = ?");
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public byte[] obtenerFotoUsuario(int userId) throws RemoteException {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT foto FROM Usuarios WHERE id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBytes("foto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
 
    @Override
    public List<String> obtenerComentarios(int mensajeId) throws RemoteException {
        List<String> comentarios = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT comentario FROM Comentarios WHERE mensaje_id = ?");
            ps.setInt(1, mensajeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comentarios.add(rs.getString("comentario"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comentarios;
    }

} 
