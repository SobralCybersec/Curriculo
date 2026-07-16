package com.dev;

import com.dev.util.UITheme;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
            UITheme.applyFlatLaf();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new ResumeEditorView().setVisible(true));
    }
}
