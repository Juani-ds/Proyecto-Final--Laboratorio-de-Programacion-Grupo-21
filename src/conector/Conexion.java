package conector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {
    private static final String URL = "jdbc:mariadb://localhost:3306/gp21_cinemacentro?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection con = null;

    private Conexion() {
        // privado para evitar instanciación
    }

    public static Connection getConexion() {
        if (con == null) {
            try {
                Class.forName("org.mariadb.jdbc.Driver"); // o "com.mysql.cj.jdbc.Driver" según el conector
                con = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión exitosa a la base de datos");
            } catch (ClassNotFoundException e) {
                System.out.println("Error: Driver no encontrado - " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Error: Driver no encontrado");
            } catch (SQLException e) {
                System.out.println("Error de conexión a BD: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Error de conexión: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return con;
    }
}
