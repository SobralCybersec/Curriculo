package com.dev.util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UITheme {
    private UITheme() {}

    // Color constants
    public static final Color BACKGROUND   = new Color(45, 45, 48);
    public static final Color SURFACE      = new Color(26, 26, 26);
    public static final Color BORDER       = new Color(34, 34, 34);
    public static final Color SELECTED     = new Color(62, 62, 64);
    public static final Color FOREGROUND   = new Color(175, 177, 179);
    public static final Color ACCENT       = new Color(122, 162, 247);
    public static final Color ACCENT_MUTED = new Color(192, 202, 245);
    public static final Color PRESSED      = new Color(78, 78, 80);
    public static final Color ERROR        = new Color(255, 100, 100);

    // Layout constants
    public static final int BUTTON_HEIGHT          = 42;
    public static final int SIDE_MENU_COLLAPSED_W  = 70;
    public static final int SIDE_MENU_EXPANDED_W   = 240;
    public static final int PULSE_DURATION_MS      = 200;
    public static final int WINDOW_WIDTH           = 1600;
    public static final int WINDOW_HEIGHT          = 900;
    public static final int BANNER_HEIGHT          = 120;
    public static final int TABLE_ROW_HEIGHT       = 30;
    public static final int SCROLL_UNIT_INCREMENT  = 16;
    public static final int PADDING                = 20;

    // FlatLaf bootstrap
    public static void applyFlatLaf() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("TextField.background", SURFACE);
        UIManager.put("TextArea.background", SURFACE);
        UIManager.put("Table.background", SURFACE);
        UIManager.put("TableHeader.background", SURFACE);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.selectionBackground", SELECTED);
        UIManager.put("Table.selectionForeground", FOREGROUND);
        UIManager.put("TableHeader.foreground", FOREGROUND);
        UIManager.put("TextField.foreground", FOREGROUND);
        UIManager.put("TextArea.foreground", FOREGROUND);
        UIManager.put("Button.background", SURFACE);
        UIManager.put("Button.foreground", FOREGROUND);
        UIManager.put("ComboBox.background", SURFACE);
        UIManager.put("ComboBox.foreground", FOREGROUND);
        UIManager.put("Label.foreground", FOREGROUND);
        UIManager.put("ScrollPane.background", BACKGROUND);
        UIManager.put("Viewport.background", BACKGROUND);
        UIManager.put("TabbedPane.background", BACKGROUND);
        UIManager.put("TabbedPane.selected", SELECTED);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND);
        UIManager.put("SplitPane.background", BACKGROUND);
        UIManager.put("SplitPaneDivider.background", BORDER);
        UIManager.put("List.background", SURFACE);
        UIManager.put("List.foreground", FOREGROUND);
        UIManager.put("MenuBar.background", BACKGROUND);
        UIManager.put("MenuBar.foreground", FOREGROUND);
        UIManager.put("Menu.background", BACKGROUND);
        UIManager.put("Menu.foreground", FOREGROUND);
        UIManager.put("MenuItem.background", BACKGROUND);
        UIManager.put("MenuItem.foreground", FOREGROUND);
        UIManager.put("PopupMenu.background", BACKGROUND);
        UIManager.put("PopupMenu.foreground", FOREGROUND);
        UIManager.put("ToolTip.background", BACKGROUND);
        UIManager.put("ToolTip.foreground", FOREGROUND);
        UIManager.put("Spinner.background", SURFACE);
        UIManager.put("Spinner.foreground", FOREGROUND);
        UIManager.put("ProgressBar.background", SURFACE);
        UIManager.put("ProgressBar.foreground", FOREGROUND);
        UIManager.put("Slider.background", SURFACE);
        UIManager.put("Slider.foreground", FOREGROUND);
        UIManager.put("CheckBox.background", BACKGROUND);
        UIManager.put("CheckBox.foreground", FOREGROUND);
        UIManager.put("RadioButton.background", BACKGROUND);
        UIManager.put("RadioButton.foreground", FOREGROUND);
        UIManager.put("ToggleButton.background", SURFACE);
        UIManager.put("ToggleButton.foreground", FOREGROUND);
        UIManager.put("FormattedTextField.background", SURFACE);
        UIManager.put("FormattedTextField.foreground", FOREGROUND);
        UIManager.put("PasswordField.background", SURFACE);
        UIManager.put("PasswordField.foreground", FOREGROUND);
        UIManager.put("TextPane.background", SURFACE);
        UIManager.put("TextPane.foreground", FOREGROUND);
        UIManager.put("EditorPane.background", SURFACE);
        UIManager.put("EditorPane.foreground", FOREGROUND);
        UIManager.put("DesktopPane.background", BACKGROUND);
        UIManager.put("InternalFrame.background", BACKGROUND);
        UIManager.put("TitledBorder.titleColor", FOREGROUND);
        UIManager.put("OptionPane.messageForeground", FOREGROUND);
        UIManager.put("OptionPane.buttonBackground", SURFACE);
        UIManager.put("OptionPane.buttonForeground", FOREGROUND);
    }

    // Table styling helper
    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(TABLE_ROW_HEIGHT);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setBackground(SURFACE);
        table.setForeground(FOREGROUND);
        table.setGridColor(BORDER);
        table.setSelectionBackground(SELECTED);
        table.setSelectionForeground(FOREGROUND);
        header.setBackground(SURFACE);
        header.setForeground(FOREGROUND);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
    }

    // Scroll-wrapper factory
    public static JPanel wrapInScrollPanel(Component content) {
        JScrollPane scroll = new RoundedScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(SCROLL_UNIT_INCREMENT);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }
}
