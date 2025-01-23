package ui.input;

import domain.SettingsSetter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputPanel extends JPanel {
    private final InputPanelCallback inputPanelCallback;
    private final JTextField meanA = new JTextField(10);
    private final JTextField meanB = new JTextField(10);
    private final JTextField stdA = new JTextField(10);
    private final JTextField stdB = new JTextField(10);
    private final JTextField corr = new JTextField(10);

    public InputPanel(InputPanelCallback inputPanelCallback) {
        super();

        this.inputPanelCallback = inputPanelCallback;

        configure();
        configureListeners();
    }

    private void configure() {
        JPanel meanAndDeviationPanel = new JPanel();
        meanAndDeviationPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel meanALable = new JLabel("μA");
        SettingsSetter.ignoreSettingParametersToObjects(meanALable);
        constraints.gridx = 0;
        constraints.gridy = 0;
        meanAndDeviationPanel.add(meanALable, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        meanAndDeviationPanel.add(meanA, constraints);

        JLabel meanBLable = new JLabel("μB");
        SettingsSetter.ignoreSettingParametersToObjects(meanBLable);
        constraints.gridx = 2;
        constraints.gridy = 0;
        meanAndDeviationPanel.add(meanBLable, constraints);
        constraints.gridx = 3;
        constraints.gridy = 0;
        meanAndDeviationPanel.add(meanB, constraints);

        JLabel stdALable = new JLabel("σA");
        SettingsSetter.ignoreSettingParametersToObjects(stdALable);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 2;
        meanAndDeviationPanel.add(stdALable, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridheight = 2;
        meanAndDeviationPanel.add(stdA, constraints);

        constraints.gridheight = 1;
        JLabel stdBLable = new JLabel("σB");
        SettingsSetter.ignoreSettingParametersToObjects(stdBLable);
        constraints.gridx = 2;
        constraints.gridy = 1;
        meanAndDeviationPanel.add(stdBLable, constraints);
        constraints.gridx = 3;
        constraints.gridy = 1;
        meanAndDeviationPanel.add(stdB, constraints);

        add(meanAndDeviationPanel, BorderLayout.NORTH);

        JPanel correlationPanel = new JPanel();
        correlationPanel.setLayout(new GridBagLayout());

        JLabel corrLable = new JLabel("ρ");
        SettingsSetter.ignoreSettingParametersToObjects(corrLable);
        constraints.gridx = 4;
        constraints.gridy = 0;
        correlationPanel.add(corrLable, constraints);
        constraints.gridx = 5;
        constraints.gridy = 0;
        correlationPanel.add(corr, constraints);

        add(correlationPanel, BorderLayout.SOUTH);
    }

    private void configureListeners() {

        KeyAdapter onUpdateKeyAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                try {
                    inputPanelCallback.onChangesApplied(
                            Double.parseDouble(meanA.getText().trim()),
                            Double.parseDouble(meanB.getText().trim()),
                            Double.parseDouble(stdA.getText().trim()),
                            Double.parseDouble(stdB.getText().trim()),
                            Double.parseDouble(corr.getText().trim())
                    );
                } catch (NumberFormatException exception) {
                    System.out.println("Invalid input");
                }
            }
        };

        meanA.addKeyListener(onUpdateKeyAdapter);
        meanB.addKeyListener(onUpdateKeyAdapter);
        stdA.addKeyListener(onUpdateKeyAdapter);
        stdB.addKeyListener(onUpdateKeyAdapter);
        corr.addKeyListener(onUpdateKeyAdapter);
    }
}
