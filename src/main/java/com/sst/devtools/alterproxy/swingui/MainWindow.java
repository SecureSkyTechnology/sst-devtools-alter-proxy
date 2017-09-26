package com.sst.devtools.alterproxy.swingui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MainWindow {

    private JFrame frame;
    private MainPanel mainPanel;

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * デフォルトで生成された public static void main() の中で呼ばれていた
     * setVisible(true)を外部から可能とするために手作業で追加したpublicメソッド。
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("alter-proxy");
        frame.setBounds(100, 100, 700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        mainPanel = new MainPanel();
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
}
