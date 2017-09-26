package com.sst.devtools.alterproxy;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.google.common.base.Throwables;
import com.sst.devtools.alterproxy.swingui.MainWindow;

public class App {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.show();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        null,
                        Throwables.getStackTraceAsString(e),
                        "error:" + e.getMessage(),
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });

    }
}
