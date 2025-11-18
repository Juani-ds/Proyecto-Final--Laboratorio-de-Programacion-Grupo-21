/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.JFrame;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLightLaf;

public class MainApp {
    public static void main(String[] args) {

        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("No se pudo iniciar FlatLaf: " + e.getMessage());
        }

        java.awt.EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Cinema Centro - Sistema de Gestión");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 900);
            frame.setLocationRelativeTo(null);

            Principal ventanaPrincipal = new Principal();

            ventanaPrincipal.setBorder(null);
            ((javax.swing.plaf.basic.BasicInternalFrameUI) ventanaPrincipal.getUI()).setNorthPane(null);

            frame.getContentPane().add(ventanaPrincipal);
            ventanaPrincipal.setVisible(true);
            frame.setVisible(true);
        });
    }
}
