import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class FrmParqueadero extends JFrame {

    private JTextField txtPlaca, txtPropietario, txtEdad;
    private JComboBox<String> cboTipo;
    private JLabel lblEspaciosLibres;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo, btnGuardar, btnSalida, btnCancelar;

    private ParqueaderoDAO dao = new ParqueaderoDAO();

    public FrmParqueadero() {
        setTitle("Gestión de Parqueadero");
        setBounds(100, 100, 900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        setContentPane(contentPane);

        // ---- Componentes del formulario ----
        JLabel lblPlaca = new JLabel("Placa");
        txtPlaca = new JTextField();

        JLabel lblTipo = new JLabel("Tipo Vehículo");
        cboTipo = new JComboBox<>(new String[]{"MOTO", "AUTOMOVIL", "CAMION"});

        JLabel lblPropietario = new JLabel("Propietario");
        txtPropietario = new JTextField();

        JLabel lblEdad = new JLabel("Edad");
        txtEdad = new JTextField();

        lblEspaciosLibres = new JLabel("Espacios libres: -");
        lblEspaciosLibres.setFont(new Font("Tahoma", Font.BOLD, 12));

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar Ingreso");
        btnSalida = new JButton("Registrar Salida");
        btnCancelar = new JButton("Cancelar");

        // ---- Tabla ----
        modeloTabla = new DefaultTableModel(
            new Object[]{"Código", "Placa", "Tipo", "Propietario", "Edad", "Espacio", "Ingreso", "Salida", "Total"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);

        // ---- GroupLayout: se ajusta solo al redimensionar ----
        GroupLayout gl = new GroupLayout(contentPane);
        contentPane.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        // Panel del formulario (columna izquierda, ancho fijo)
        GroupLayout.SequentialGroup formHorizontal = gl.createSequentialGroup();
        formHorizontal.addGroup(gl.createParallelGroup()
                .addComponent(lblPlaca).addComponent(lblTipo)
                .addComponent(lblPropietario).addComponent(lblEdad));
        formHorizontal.addGroup(gl.createParallelGroup()
                .addComponent(txtPlaca, 150, 150, 150)
                .addComponent(cboTipo, 150, 150, 150)
                .addComponent(txtPropietario, 150, 150, 150)
                .addComponent(txtEdad, 150, 150, 150));

        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.LEADING)
                .addGroup(formHorizontal)
                .addComponent(lblEspaciosLibres)
                .addGroup(gl.createSequentialGroup()
                    .addComponent(btnNuevo)
                    .addComponent(btnGuardar))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(btnSalida)
                    .addComponent(btnCancelar)))
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(scrollPane) // crece con la ventana
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.LEADING)
                // columna izquierda
                .addGroup(gl.createSequentialGroup()
                    .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblPlaca).addComponent(txtPlaca, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblTipo).addComponent(cboTipo, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblPropietario).addComponent(txtPropietario, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblEdad).addComponent(txtEdad, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(lblEspaciosLibres)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnNuevo).addComponent(btnGuardar))
                    .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnSalida).addComponent(btnCancelar)))
                // columna derecha (tabla, crece con la ventana)
                .addComponent(scrollPane))
        );

        // ---- Eventos ----
        btnNuevo.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarIngreso());
        btnSalida.addActionListener(e -> registrarSalida());
        btnCancelar.addActionListener(e -> limpiarCampos());

        cargarTabla();
        actualizarEspaciosLibres();
    }

    private void limpiarCampos() {
        txtPlaca.setText("");
        txtPropietario.setText("");
        txtEdad.setText("");
        cboTipo.setSelectedIndex(0);
        txtPlaca.requestFocus();
    }

    private void guardarIngreso() {
        String placa = txtPlaca.getText().trim().toUpperCase();
        String tipo = (String) cboTipo.getSelectedItem();
        String propietario = txtPropietario.getText().trim();
        String edadTexto = txtEdad.getText().trim();

        if (placa.isEmpty() || propietario.isEmpty() || edadTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La edad debe ser numérica.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Vehiculo vehiculo = new Vehiculo(placa, tipo, propietario, edad);
            boolean ok = dao.registrarIngreso(vehiculo);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Vehículo ingresado correctamente.");
                limpiarCampos();
                cargarTabla();
                actualizarEspaciosLibres();
            } else {
                JOptionPane.showMessageDialog(this, "No hay espacios disponibles. No se puede ingresar el vehículo.",
                        "Sin cupo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarSalida() {
        String placa = txtPlaca.getText().trim().toUpperCase();
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la placa del vehículo que sale.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String resultado = dao.registrarSalida(placa);
            if (resultado == null) {
                JOptionPane.showMessageDialog(this, "No se encontró un ingreso activo para esa placa.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Salida registrada", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTabla();
                actualizarEspaciosLibres();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try (Connection con = Conexion.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = dao.obtenerTodosLosRegistros(st)) {

            while (rs.next()) {
                Timestamp salida = rs.getTimestamp("hora_salida");
                Double total = rs.getObject("total_pagado") != null ? rs.getDouble("total_pagado") : null;

                modeloTabla.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getString("tipo_vehiculo"),
                        rs.getString("propietario"),
                        rs.getInt("edad_propietario"),
                        rs.getInt("espacio_id"),
                        fmt.format(rs.getTimestamp("hora_ingreso")),
                        salida != null ? fmt.format(salida) : "-",
                        total != null ? String.format("$%.2f", total) : "-"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar la tabla: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEspaciosLibres() {
        try {
            int libres = dao.consultarEspaciosLibres();
            lblEspaciosLibres.setText("Espacios libres: " + libres);
        } catch (SQLException ex) {
            lblEspaciosLibres.setText("Espacios libres: error");
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            FrmParqueadero frame = new FrmParqueadero();
            frame.setVisible(true);
        });
    }
}