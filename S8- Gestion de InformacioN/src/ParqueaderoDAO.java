import java.sql.*;
import java.time.LocalDateTime;
import java.time.Duration;

public class ParqueaderoDAO {

    public int consultarEspaciosLibres() throws SQLException {
        String sql = "SELECT COUNT(*) AS libres FROM espacios WHERE ocupado = FALSE";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("libres");
        }
        return 0;
    }

    public boolean registrarIngreso(Vehiculo vehiculo) throws SQLException {
        Connection con = null;
        try {
            con = Conexion.obtenerConexion();
            con.setAutoCommit(false);

            int espacioId = -1;
            String buscarSql = "SELECT id FROM espacios WHERE ocupado = FALSE LIMIT 1 FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(buscarSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    espacioId = rs.getInt("id");
                } else {
                    con.rollback();
                    return false; // no hay espacio disponible
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE espacios SET ocupado = TRUE WHERE id = ?")) {
                ps.setInt(1, espacioId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO registros (placa, tipo_vehiculo, propietario, edad_propietario, espacio_id, hora_ingreso) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, vehiculo.getPlaca());
                ps.setString(2, vehiculo.getTipo());
                ps.setString(3, vehiculo.getPropietario());
                ps.setInt(4, vehiculo.getEdadPropietario());
                ps.setInt(5, espacioId);
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }

    public String registrarSalida(String placa) throws SQLException {
        Connection con = null;
        try {
            con = Conexion.obtenerConexion();
            con.setAutoCommit(false);

            int registroId, espacioId, edad;
            String tipo;
            LocalDateTime horaIngreso;

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id, tipo_vehiculo, edad_propietario, espacio_id, hora_ingreso FROM registros WHERE placa = ? AND hora_salida IS NULL LIMIT 1")) {
                ps.setString(1, placa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return null; // no había ingreso activo
                    }
                    registroId = rs.getInt("id");
                    tipo = rs.getString("tipo_vehiculo");
                    edad = rs.getInt("edad_propietario");
                    espacioId = rs.getInt("espacio_id");
                    horaIngreso = rs.getTimestamp("hora_ingreso").toLocalDateTime();
                }
            }

            double valorHora;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT valor_hora FROM tarifas WHERE tipo_vehiculo = ?")) {
                ps.setString(1, tipo);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    valorHora = rs.getDouble("valor_hora");
                }
            }

            LocalDateTime horaSalida = LocalDateTime.now();
            long minutos = Duration.between(horaIngreso, horaSalida).toMinutes();
            double horas = Math.max(1, Math.ceil(minutos / 60.0));
            double total = horas * valorHora;
            if (edad >= 60) total *= 0.80;

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE registros SET hora_salida = ?, total_pagado = ? WHERE id = ?")) {
                ps.setTimestamp(1, Timestamp.valueOf(horaSalida));
                ps.setDouble(2, total);
                ps.setInt(3, registroId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE espacios SET ocupado = FALSE WHERE id = ?")) {
                ps.setInt(1, espacioId);
                ps.executeUpdate();
            }

            con.commit();
            return String.format("Total a pagar: $%.2f%s", total, edad >= 60 ? " (desc. adulto mayor aplicado)" : "");

        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }

    public ResultSet obtenerTodosLosRegistros(Statement st) throws SQLException {
        return st.executeQuery(
            "SELECT id, placa, tipo_vehiculo, propietario, edad_propietario, espacio_id, hora_ingreso, hora_salida, total_pagado " +
            "FROM registros ORDER BY id DESC");
    }
}