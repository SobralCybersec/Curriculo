package com.dev;

import com.dev.view.ResumeEditorView;
import com.dev.util.UITheme;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatDarkLaf.setup();
                UITheme.applyFlatLaf();
                new ResumeEditorView().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
