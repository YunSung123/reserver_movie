package com.tenco;

import com.tenco.view.MovieReserveSwingApp;
import com.tenco.view.ReserveView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            MovieReserveSwingApp app = new MovieReserveSwingApp();
            app.updateLoginStatusLabel();
            app.refreshBookingInfo();
            app.setVisible(true);
        });
    }
}