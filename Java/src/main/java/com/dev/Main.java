package com.dev;

import com.dev.view.ResumeEditorView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResumeEditorView().setVisible(true));
    }
}
