package com.discussionforum.client;

import javax.swing.*;
import java.awt.*;

public class PanelRenderer implements ListCellRenderer<JPanel> {
    @Override
    public Component getListCellRendererComponent(JList<? extends JPanel> list, JPanel panel, int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
        } else {
            panel.setBackground(list.getBackground());
        }
        return panel;
    }
}
