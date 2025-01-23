package domain;

import data.Data;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SettingsSetter {

    private static Font font;
    private static final ArrayList<Component> ignoredComponents = new ArrayList<>();

    public SettingsSetter(JFrame window) {

        try {
            window.setIconImage(new ImageIcon(Data.ICON.getPath()).getImage());
//            font = Font.createFont(Font.TRUETYPE_FONT,
//                    new java.io.File(Data.FONT.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setParametersToObjects(Component component) {
        if (!ignoredComponents.contains(component)) {
            if (!(component.getFont() == null)) {
                //component.setFont(font.deriveFont((float) component.getFont().getSize()));
            }
            component.setVisible(true);
        }
        for (Component child : ((Container) component).getComponents()) {
            setParametersToObjects(child);
        }
    }

    public static void ignoreSettingParametersToObjects(Component component) {
        ignoredComponents.add(component);
    }
}

