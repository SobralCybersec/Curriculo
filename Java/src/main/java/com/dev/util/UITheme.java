package com.dev.util;

import com.dev.view.components.RoundedScrollPane;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UITheme {

    private UITheme() {
    }

    public static final Color BACKGROUND   = new Color(45, 45, 48);

    public static final Color SURFACE      = new Color(26, 26, 26);

    public static final Color BORDER       = new Color(34, 34, 34);

    public static final Color SELECTED     = new Color(62, 62, 64);

    public static final Color FOREGROUND   = new Color(175, 173, 179);

    public static final Color ACCENT       = new Color(122, 162, 247);

    public static final Color ACCENT_MUTED = new Color(192, 202, 245);

    public static final Color PRESSED      = new Color(78, 78, 80);

    public static final int BUTTON_HEIGHT         = 42;

    public static final int SIDE_MENU_COLLAPSED_W = 70;

    public static final int SIDE_MENU_EXPANDED_W  = 240;

    public static final int PULSE_DURATION_MS     = 200;

    public static final int WINDOW_WIDTH          = 1600;

    public static final int WINDOW_HEIGHT         = 900;

    public static void applyFlatLaf() {
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("TabbedPane.tabHeight", 32);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.rowHeight", 28);

        UIManager.put("Panel.background",          BACKGROUND);
        UIManager.put("Button.background",         SURFACE);
        UIManager.put("Button.foreground",         FOREGROUND);
        UIManager.put("TextField.background",      SURFACE);
        UIManager.put("TextField.foreground",      FOREGROUND);
        UIManager.put("TextArea.background",       SURFACE);
        UIManager.put("TextArea.foreground",       FOREGROUND);
        UIManager.put("Table.background",          SURFACE);
        UIManager.put("Table.foreground",          FOREGROUND);
        UIManager.put("TableHeader.background",    SURFACE);
        UIManager.put("TableHeader.foreground",    FOREGROUND);
        UIManager.put("ComboBox.background",       SURFACE);
        UIManager.put("ComboBox.foreground",       FOREGROUND);
        UIManager.put("CheckBox.background",       BACKGROUND);
        UIManager.put("CheckBox.foreground",       FOREGROUND);
        UIManager.put("Label.foreground",          FOREGROUND);
        UIManager.put("TabbedPane.background",     BACKGROUND);
        UIManager.put("TabbedPane.foreground",     FOREGROUND);
        UIManager.put("ScrollPane.background",     BACKGROUND);
        UIManager.put("Viewport.background",       BACKGROUND);
        UIManager.put("ScrollBar.track",           new Color(42, 42, 44));
        UIManager.put("ScrollBar.thumb",           new Color(61, 61, 61));
        UIManager.put("TitledBorder.titleColor",   FOREGROUND);
    }

    public static JPanel wrapInScrollPanel(JPanel content) {
        RoundedScrollPane scrollPane = new RoundedScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND);
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }

    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setBackground(SURFACE);
        table.setForeground(FOREGROUND);
        table.setGridColor(BORDER);
        table.setSelectionBackground(SELECTED);
        table.setSelectionForeground(FOREGROUND);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(SURFACE);
        header.setForeground(FOREGROUND);
    }
}
