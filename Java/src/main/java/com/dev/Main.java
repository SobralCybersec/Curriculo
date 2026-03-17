package com.dev;

import com.dev.view.ResumeEditorView;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
            
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("TabbedPane.tabHeight", 32);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.rowHeight", 28);
            
            UIManager.put("Panel.background", new Color(45, 45, 48));
            UIManager.put("Button.background", new Color(26, 26, 26));
            UIManager.put("Button.foreground", new Color(175, 177, 179));
            UIManager.put("TextField.background", new Color(26, 26, 26));
            UIManager.put("TextField.foreground", new Color(175, 177, 179));
            UIManager.put("TextArea.background", new Color(26, 26, 26));
            UIManager.put("TextArea.foreground", new Color(175, 177, 179));
            UIManager.put("Table.background", new Color(26, 26, 26));
            UIManager.put("Table.foreground", new Color(175, 177, 179));
            UIManager.put("TableHeader.background", new Color(26, 26, 26));
            UIManager.put("TableHeader.foreground", new Color(175, 177, 179));
            UIManager.put("ComboBox.background", new Color(26, 26, 26));
            UIManager.put("ComboBox.foreground", new Color(175, 177, 179));
            UIManager.put("CheckBox.background", new Color(45, 45, 48));
            UIManager.put("CheckBox.foreground", new Color(175, 177, 179));
            UIManager.put("Label.foreground", new Color(175, 177, 179));
            UIManager.put("TabbedPane.background", new Color(45, 45, 48));
            UIManager.put("TabbedPane.foreground", new Color(175, 177, 179));
            UIManager.put("ScrollPane.background", new Color(45, 45, 48));
            UIManager.put("Viewport.background", new Color(45, 45, 48));
            UIManager.put("ScrollBar.track", new Color(42, 42, 44));
            UIManager.put("ScrollBar.thumb", new Color(61, 61, 61));
            UIManager.put("TitledBorder.titleColor", new Color(175, 177, 179));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new ResumeEditorView().setVisible(true));
    }
}
