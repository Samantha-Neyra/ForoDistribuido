package com.discussionforum.client;

import com.discussionforum.server.ForoRemoto;
import com.discussionforum.model.Tema;
import com.discussionforum.model.Mensaje;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteRMI {

    private static ForoRemoto foro;
    private static JFrame frame;
    private static DefaultListModel<String> temasModel;
    private static int temaActualId = -1;
    private static String nombreCompletoUsuario;
    private static int idUsuarioActual;
    private static byte[] fotoUsuarioActual; // Variable para almacenar la foto del usuario actual
    private static byte[] fotoSeleccionada; // Variable para almacenar la foto seleccionada durante el registro

    public static void main(String[] args) {
        try {
            foro = (ForoRemoto) Naming.lookup("rmi://localhost/ForoRemoto");
            mostrarPantallaLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mostrar la pantalla de inicio de sesión
    private static void mostrarPantallaLogin() {
        frame = new JFrame("Iniciar Sesión");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        frame.add(panel);
        colocarComponentesLogin(panel);

        frame.setVisible(true);
    }

    // Colocar los componentes de la pantalla de inicio de sesión
    private static void colocarComponentesLogin(JPanel panel) {
        panel.setLayout(null);

        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setBounds(10, 20, 80, 25);
        panel.add(usuarioLabel);

        JTextField usuarioText = new JTextField(20);
        usuarioText.setBounds(100, 20, 165, 25);
        panel.add(usuarioText);

        JLabel contraseñaLabel = new JLabel("Contraseña:");
        contraseñaLabel.setBounds(10, 50, 80, 25);
        panel.add(contraseñaLabel);

        JPasswordField contraseñaText = new JPasswordField(20);
        contraseñaText.setBounds(100, 50, 165, 25);
        panel.add(contraseñaText);

        JButton iniciarSesionButton = new JButton("Iniciar Sesión");
        iniciarSesionButton.setBounds(10, 80, 150, 25);
        panel.add(iniciarSesionButton);

        iniciarSesionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean exito = foro.iniciarSesion(usuarioText.getText(), new String(contraseñaText.getPassword()));
                    if (exito) {
                        idUsuarioActual = foro.obtenerIdUsuario(usuarioText.getText());
                        nombreCompletoUsuario = foro.obtenerNombreUsuario(idUsuarioActual);
                        fotoUsuarioActual = foro.obtenerFotoUsuario(idUsuarioActual); // Obtener la foto del usuario
                        JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso");
                        frame.dispose();
                        mostrarPantallaPrincipal();
                    } else {
                        JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton registrarButton = new JButton("Registrar");
        registrarButton.setBounds(10, 110, 150, 25);
        panel.add(registrarButton);

        registrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mostrarPantallaRegistro();
            }
        });
    }

    // Mostrar la pantalla de registro
    private static void mostrarPantallaRegistro() {
        frame = new JFrame("Registro");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JPanel panel = new JPanel();
        frame.add(panel);
        colocarComponentesRegistro(panel);

        frame.setVisible(true);
    }

    // Colocar los componentes de la pantalla de registro
    private static void colocarComponentesRegistro(JPanel panel) {
        panel.setLayout(null);

        JLabel fotoLabel = new JLabel("Foto:");
        fotoLabel.setBounds(10, 20, 80, 25);
        panel.add(fotoLabel);

        JButton seleccionarFotoButton = new JButton("Seleccionar Foto");
        seleccionarFotoButton.setBounds(100, 20, 165, 25);
        panel.add(seleccionarFotoButton);

        seleccionarFotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        fotoSeleccionada = Files.readAllBytes(selectedFile.toPath());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        JLabel nombreLabel = new JLabel("Nombre:");
        nombreLabel.setBounds(10, 50, 80, 25);
        panel.add(nombreLabel);

        JTextField nombreText = new JTextField(20);
        nombreText.setBounds(100, 50, 165, 25);
        panel.add(nombreText);

        JLabel apellidoLabel = new JLabel("Apellido:");
        apellidoLabel.setBounds(10, 80, 80, 25);
        panel.add(apellidoLabel);

        JTextField apellidoText = new JTextField(20);
        apellidoText.setBounds(100, 80, 165, 25);
        panel.add(apellidoText);

        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setBounds(10, 110, 80, 25);
        panel.add(usuarioLabel);

        JTextField usuarioText = new JTextField(20);
        usuarioText.setBounds(100, 110, 165, 25);
        panel.add(usuarioText);

        JLabel contraseñaLabel = new JLabel("Contraseña:");
        contraseñaLabel.setBounds(10, 140, 80, 25);
        panel.add(contraseñaLabel);

        JPasswordField contraseñaText = new JPasswordField(20);
        contraseñaText.setBounds(100, 140, 165, 25);
        panel.add(contraseñaText);

        JLabel repetirContraseñaLabel = new JLabel("Repetir Contraseña:");
        repetirContraseñaLabel.setBounds(10, 170, 150, 25);
        panel.add(repetirContraseñaLabel);

        JPasswordField repetirContraseñaText = new JPasswordField(20);
        repetirContraseñaText.setBounds(150, 170, 165, 25);
        panel.add(repetirContraseñaText);

        JButton registrarButton = new JButton("Registrar");
        registrarButton.setBounds(10, 200, 150, 25);
        panel.add(registrarButton);

        registrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre = nombreText.getText();
                String apellido = apellidoText.getText();
                String usuario = usuarioText.getText();
                String contraseña = new String(contraseñaText.getPassword());
                String repetirContraseña = new String(repetirContraseñaText.getPassword());

                if (!contraseña.equals(repetirContraseña)) {
                    JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden");
                    return;
                }

                if (fotoSeleccionada == null) {
                    JOptionPane.showMessageDialog(null, "Por favor, seleccione una foto");
                    return;
                }

                try {
                    boolean registrado = foro.registrarUsuario(fotoSeleccionada, nombre, apellido, usuario, contraseña);
                    if (registrado) {
                        JOptionPane.showMessageDialog(null, "Registro exitoso");
                        frame.dispose();
                        mostrarPantallaLogin();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al registrar usuario");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // Mostrar la pantalla principal del foro
    private static void mostrarPantallaPrincipal() {
        frame = new JFrame("Foro Distribuido");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel usuarioPanel = new JPanel();
        usuarioPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel usuarioLabel = new JLabel("Nombre: " + nombreCompletoUsuario); // Mostrar nombre completo del usuario

        if (fotoUsuarioActual != null) {
            ImageIcon fotoIcon = new ImageIcon(fotoUsuarioActual);
            Image img = fotoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel fotoLabel = new JLabel(new ImageIcon(img));
            usuarioPanel.add(fotoLabel);
        }

        usuarioPanel.add(usuarioLabel);
        panel.add(usuarioPanel, BorderLayout.NORTH);

        temasModel = new DefaultListModel<>();
        JList<String> temasList = new JList<>(temasModel);
        JScrollPane temasScrollPane = new JScrollPane(temasList);
        panel.add(temasScrollPane, BorderLayout.WEST);

        JPanel mensajesPanel = new JPanel();
        mensajesPanel.setLayout(new BoxLayout(mensajesPanel, BoxLayout.Y_AXIS));
        JScrollPane mensajesScrollPane = new JScrollPane(mensajesPanel);
        panel.add(mensajesScrollPane, BorderLayout.CENTER);  

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField nuevoTemaText = new JTextField(20);
        inputPanel.add(nuevoTemaText);

        JButton crearTemaButton = new JButton("Crear Tema");
        inputPanel.add(crearTemaButton);

        JTextField nuevoMensajeText = new JTextField(20);
        inputPanel.add(nuevoMensajeText);

        JButton publicarMensajeButton = new JButton("Publicar Mensaje");
        inputPanel.add(publicarMensajeButton);

        panel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        actualizarListaTemas(); // Llamada inicial para actualizar la lista de temas

        crearTemaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String titulo = nuevoTemaText.getText();
                if (!titulo.isEmpty()) {
                    try {
                        boolean creado = foro.crearTema(titulo, idUsuarioActual, nombreCompletoUsuario); // Pasar ID y nombre del autor
                        if (creado) {
                            actualizarListaTemas();  // Actualizar la lista de temas
                            nuevoTemaText.setText(""); // Limpiar el campo de texto después de crear el tema
                        } else {
                            JOptionPane.showMessageDialog(null, "Error al crear tema");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        publicarMensajeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String contenido = nuevoMensajeText.getText();
                if (temaActualId != -1 && !contenido.isEmpty()) {
                    try {
                        boolean publicado = foro.publicarMensaje(contenido, idUsuarioActual, temaActualId); // Pasar ID del autor y del tema
                        if (publicado) {
                            actualizarListaMensajes(temaActualId, mensajesPanel);
                            nuevoMensajeText.setText(""); // Limpiar el campo de texto después de publicar el mensaje
                        } else {
                            JOptionPane.showMessageDialog(null, "Error al publicar mensaje");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        temasList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = temasList.getSelectedIndex();
                if (selectedIndex != -1) {
                    try {
                        Tema temaSeleccionado = foro.obtenerTemas().get(selectedIndex);
                        temaActualId = temaSeleccionado.getId();
                        actualizarListaMensajes(temaActualId, mensajesPanel);
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    }
                }
            }
        });
    }

    // Método para actualizar la lista de temas
    private static void actualizarListaTemas() {
        SwingWorker<List<Tema>, Void> worker = new SwingWorker<List<Tema>, Void>() {
            @Override
            protected List<Tema> doInBackground() throws Exception {
                return foro.obtenerTemas(); // Método para obtener temas desde algún origen (por ejemplo, una base de datos)
            }

            @Override
            protected void done() {
                try {
                    temasModel.clear(); // Limpiar el modelo de la lista de temas
                    List<Tema> temas = get(); // Obtener los temas del resultado de doInBackground()
                    for (Tema tema : temas) {
                        temasModel.addElement(tema.getTitulo()); // Agregar cada título de tema al modelo
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace(); // Manejo de excepciones si doInBackground() o get() lanzan excepciones
                }
            }
        };
        worker.execute(); // Ejecutar el SwingWorker
    }

private static void actualizarListaMensajes(int temaId, JPanel mensajesPanel) {
    SwingWorker<List<Mensaje>, Void> worker = new SwingWorker<List<Mensaje>, Void>() {
        @Override
        protected List<Mensaje> doInBackground() throws Exception {
            return foro.obtenerMensajes(temaId); // Método para obtener mensajes de un tema específico
        }

        @Override
        protected void done() {
            try {
                mensajesPanel.removeAll();
                List<Mensaje> mensajes = get(); // Obtener los mensajes del resultado de doInBackground()
                mensajesPanel.setLayout(new GridBagLayout()); // Usar GridBagLayout para el panel de mensajes
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5); // Espaciado entre componentes

                for (Mensaje mensaje : mensajes) {
                    final int mensajeId = mensaje.getId(); // Obtener el ID del mensaje como final o efectivamente final
                    int votoUsuario = foro.obtenerVotoUsuario(mensajeId, idUsuarioActual); // Obtener el voto actual del usuario para este mensaje

                    JPanel panelMensaje = new JPanel();
                    panelMensaje.setLayout(new GridBagLayout()); // Usar GridBagLayout para el panel del mensaje
                    GridBagConstraints gbcMensaje = new GridBagConstraints();
                    gbcMensaje.anchor = GridBagConstraints.WEST;
                    gbcMensaje.fill = GridBagConstraints.HORIZONTAL;
                    gbcMensaje.insets = new Insets(5, 5, 5, 5);

                    // Posicionar la foto de perfil a la izquierda del nombre del usuario
                    if (mensaje.getAutorFoto() != null) { // Si la foto del autor está disponible
                        ImageIcon fotoIcon = new ImageIcon(mensaje.getAutorFoto());
                        Image img = fotoIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                        JLabel fotoLabel = new JLabel(new ImageIcon(img));
                        gbcMensaje.gridx = 0; // Columna 0
                        gbcMensaje.gridy = 0; // Fila 0
                        panelMensaje.add(fotoLabel, gbcMensaje);
                    }

                    // Posicionar el nombre del usuario en la segunda columna de la primera fila
                    JLabel labelUsuario = new JLabel("<html><b>"+ "@" + mensaje.getAutorNombre() + "</b></html>");
                    labelUsuario.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda
                    labelUsuario.setFont(new Font("Arial", Font.BOLD, 14)); // Ajustar la fuente y el tamaño de la letra
                    gbcMensaje.gridx = 1; // Columna 1
                    panelMensaje.add(labelUsuario, gbcMensaje);

                    // Posicionar el contenido del mensaje en la primera columna y segunda fila (ocupando dos columnas)
                    JLabel labelMensaje = new JLabel("<html>" + mensaje.getContenido() + "</html>");
                    labelMensaje.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda
                    labelMensaje.setFont(new Font("Arial", Font.PLAIN, 12)); // Ajustar la fuente y el tamaño de la letra
                    gbcMensaje.gridx = 0;
                    gbcMensaje.gridy = 1;
                    gbcMensaje.gridwidth = 2; // Ocupa dos columnas
                    panelMensaje.add(labelMensaje, gbcMensaje);

                    // Posicionar la fecha en la primera columna y tercera fila (ocupando dos columnas)
                    JLabel labelFecha = new JLabel("<html><i>" + mensaje.getFecha() + "</i></html>");
                    labelFecha.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda
                    labelFecha.setFont(new Font("Arial", Font.ITALIC, 10)); // Ajustar la fuente y el tamaño de la letra
                    gbcMensaje.gridy = 2;
                    panelMensaje.add(labelFecha, gbcMensaje);

                    // Posicionar los likes en la primera columna y cuarta fila (ocupando dos columnas)
                    JLabel labelLikes = new JLabel("<html>Me gusta: " + mensaje.getMeGusta() + " No me gusta: " + mensaje.getNoMeGusta() + "</html>");
                    labelLikes.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda
                    labelLikes.setFont(new Font("Arial", Font.PLAIN, 12)); // Ajustar la fuente y el tamaño de la letra
                    gbcMensaje.gridy = 3;
                    panelMensaje.add(labelLikes, gbcMensaje);

                    // Posicionar los botones en la primera columna y quinta fila (ocupando dos columnas)
                    JPanel panelBotones = new JPanel();
                    panelBotones.setLayout(new FlowLayout(FlowLayout.LEFT)); // Alinear botones a la izquierda
                    gbcMensaje.gridy = 4;
                    panelMensaje.add(panelBotones, gbcMensaje);

                    JButton btnMeGusta = new JButton("Me gusta");
                    btnMeGusta.setFont(new Font("Arial", Font.PLAIN, 12)); // Ajustar la fuente y el tamaño de la letra
                    btnMeGusta.addActionListener(e -> {
                        try {
                            if (votoUsuario == 1) {
                                foro.quitarMeGusta(mensajeId, idUsuarioActual);
                            } else if (votoUsuario == -1) {
                                foro.quitarNoMeGusta(mensajeId, idUsuarioActual);
                                foro.agregarMeGusta(mensajeId, idUsuarioActual);
                            } else {
                                foro.agregarMeGusta(mensajeId, idUsuarioActual);
                            }
                            actualizarListaMensajes(temaId, mensajesPanel);
                        } catch (RemoteException remoteException) {
                            remoteException.printStackTrace();
                        }
                    });
                    panelBotones.add(btnMeGusta);

                    JButton btnNoMeGusta = new JButton("No me gusta");
                    btnNoMeGusta.setFont(new Font("Arial", Font.PLAIN, 12)); // Ajustar la fuente y el tamaño de la letra
                    btnNoMeGusta.addActionListener(e -> {
                        try {
                            if (votoUsuario == -1) {
                                foro.quitarNoMeGusta(mensajeId, idUsuarioActual);
                            } else if (votoUsuario == 1) {
                                foro.quitarMeGusta(mensajeId, idUsuarioActual);
                                foro.agregarNoMeGusta(mensajeId, idUsuarioActual);
                            } else {
                                foro.agregarNoMeGusta(mensajeId, idUsuarioActual);
                            }
                            actualizarListaMensajes(temaId, mensajesPanel);
                        } catch (RemoteException remoteException) {
                            remoteException.printStackTrace();
                        }
                    });
                    panelBotones.add(btnNoMeGusta);

                    JTextField campoComentario = new JTextField(20);
                    campoComentario.setFont(new Font("Arial", Font.PLAIN, 12)); // Ajustar la fuente y el tamaño de la letra
                    panelBotones.add(campoComentario);

                    JButton btnComentar = new JButton("Comentar");
                    btnComentar.setFont(new Font("Arial", Font.PLAIN, 12)); // Ajustar la fuente y el tamaño de la letra
                    btnComentar.addActionListener(e -> {
                        String comentario = campoComentario.getText();
                        if (!comentario.isEmpty()) {
                            try {
                                foro.agregarComentario(mensajeId, comentario);
                                campoComentario.setText("");
                                actualizarListaMensajes(temaId, mensajesPanel);
                            } catch (RemoteException remoteException) {
                                remoteException.printStackTrace();
                            }
                        }
                    });
                    panelBotones.add(btnComentar);

                    // Obtener comentarios
                    List<String> comentarios = foro.obtenerComentarios(mensajeId);
                    int numComentarios = comentarios.size();
                    int mostrarComentarios = Math.min(numComentarios, 3);

                    gbcMensaje.gridy++;
                    for (int i = 0; i < mostrarComentarios; i++) {
                        JLabel comentarioLabel = new JLabel("<html><i>" + comentarios.get(i) + "</i></html>");
                        comentarioLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda
                        comentarioLabel.setFont(new Font("Arial", Font.ITALIC, 12)); // Ajustar la fuente y el tamaño de la letra
                        panelMensaje.add(comentarioLabel, gbcMensaje);
                        gbcMensaje.gridy++;
                    }

                    if (numComentarios > 3) {
                        JButton verMasComentariosButton = new JButton("Ver " + (numComentarios - 3) + " comentarios");
                        verMasComentariosButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda
                        verMasComentariosButton.setFont(new Font("Arial", Font.PLAIN, 12)); // Ajustar la fuente y el tamaño de la letra
                        verMasComentariosButton.addActionListener(e -> {
                            for (int i = 3; i < numComentarios; i++) {
                                JLabel comentarioLabel = new JLabel("<html><i>" + comentarios.get(i) + "</i></html>");
                                comentarioLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinear a la izquierda
                                comentarioLabel.setFont(new Font("Arial", Font.ITALIC, 12)); // Ajustar la fuente y el tamaño de la letra
                                panelMensaje.add(comentarioLabel, gbcMensaje);
                                gbcMensaje.gridy++;
                            }
                            panelMensaje.remove(verMasComentariosButton);
                            panelMensaje.revalidate();
                            panelMensaje.repaint();
                        });
                        panelMensaje.add(verMasComentariosButton, gbcMensaje);
                        gbcMensaje.gridy++;
                    }

                    gbc.gridy++;
                    mensajesPanel.add(panelMensaje, gbc); // Agregar el panel de mensaje al panel principal
                }
                mensajesPanel.revalidate(); // Actualizar el panel principal
                mensajesPanel.repaint();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace(); // Manejo de excepciones si doInBackground() o get() lanzan excepciones
            } catch (RemoteException ex) {
                Logger.getLogger(ClienteRMI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    worker.execute(); // Ejecutar el SwingWorker
}
}