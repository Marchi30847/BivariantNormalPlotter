package ui;

import domain.Presenter;
import domain.PresenterContract;
import domain.SettingsSetter;
import ui.graph.GraphPanel;
import ui.graph.GraphPanelCallback;
import ui.input.InputPanel;
import ui.input.InputPanelCallback;

import javax.swing.*;
import java.awt.*;


public class View extends JFrame implements InputPanelCallback, GraphPanelCallback {
    private final PresenterContract presenterContract = new Presenter();
    private final GraphPanel graphPanel = new GraphPanel(this);
    private final InputPanel inputPanel = new InputPanel(this);
    private static final Dimension frameSize = new Dimension(800, 800);
    {new SettingsSetter(this);}

    public void openMainJFrame() {
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
        add(graphPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    @Override
    public void onChangesApplied(double meanA, double meanB, double stdA, double stdB, double corr) {
        presenterContract.updateGraph(meanA, meanB, stdA, stdB, corr);
        graphPanel.repaint();
    }

    @Override
    public double getNewDensity(double x, double y) {
        return presenterContract.getNewDensity(x, y);
    }
}
