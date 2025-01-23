package ui;

import domain.SettingsSetter;

import javax.swing.*;
import java.awt.*;


public class View extends JFrame {

    private static final Dimension frameSize = new Dimension(800, 800);
    {new SettingsSetter(this);}

    public void show() {
        configure();
        openMenu();
        SettingsSetter.setParametersToObjects(this);
    }

    private void configure() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Bivariant Normal Distribution");
        this.setSize(frameSize);
        this.setLocationRelativeTo(null);   // center the window
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(Color.LIGHT_GRAY);
    }

    private void openMenu() {

    }
}
