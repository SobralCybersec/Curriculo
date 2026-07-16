package com.dev.util;

import com.dev.view.components.RoundedScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UITheme {

    private UITheme() {
    }

    public static final Color BACKGROUND       = new Color(13, 17, 23);
    public static final Color SURFACE          = new Color(21, 27, 35);
    public static final Color SURFACE_RAISED   = new Color(27, 36, 48);
    public static final Color PREVIEW_CANVAS   = new Color(32, 40, 50);
    public static final Color BORDER           = new Color(42, 53, 67);
    public static final Color SELECTED         = new Color(36, 49, 73);
    public static final Color FOREGROUND       = new Color(220, 229, 240);
    public static final Color TEXT_STRONG      = new Color(247, 250, 252);
    public static final Color TEXT_MUTED       = new Color(139, 152, 169);
    public static final Color ACCENT           = new Color(124, 156, 255);
    public static final Color ACCENT_MUTED     = new Color(185, 199, 255);
    public static final Color ACCENT_PRESSED   = new Color(94, 128, 230);
    public static final Color PRESSED          = new Color(51, 68, 100);
    public static final Color DANGER           = new Color(240, 128, 128);

    public static final int BUTTON_HEIGHT         = 42;

    public static final int SIDE_MENU_COLLAPSED_W = 70;

    public static final int SIDE_MENU_EXPANDED_W  = 240;

    public static final int PULSE_DURATION_MS     = 200;

    public static final int WINDOW_WIDTH          = 1600;

    public static final int WINDOW_HEIGHT         = 900;

    public static void applyFlatLaf() {
        UIManager.put("defaultFont", font(Font.PLAIN, 13));
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.focusColor", ACCENT);
        UIManager.put("Component.borderColor", BORDER);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("TabbedPane.tabHeight", 32);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.rowHeight", 28);

        UIManager.put("Panel.background",          BACKGROUND);
        UIManager.put("Button.background",         SURFACE);
        UIManager.put("Button.foreground",         TEXT_STRONG);
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
        UIManager.put("ScrollBar.track",           BACKGROUND);
        UIManager.put("ScrollBar.thumb",           BORDER);
        UIManager.put("TitledBorder.titleColor",   FOREGROUND);
        UIManager.put("Separator.foreground",      BORDER);
        UIManager.put("TextField.caretForeground", ACCENT_MUTED);
        UIManager.put("TextField.selectionBackground", SELECTED);
        UIManager.put("TextArea.selectionBackground", SELECTED);
    }

    public static Font font(int style, float size) {
        Font base = UIManager.getFont("Label.font");
        if (base == null) base = new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(size));
        return base.deriveFont(style, size);
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
        table.setFont(font(Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setBackground(SURFACE);
        table.setForeground(FOREGROUND);
        table.setGridColor(BORDER);
        table.setSelectionBackground(SELECTED);
        table.setSelectionForeground(FOREGROUND);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(font(Font.BOLD, 13));
        header.setBackground(SURFACE);
        header.setForeground(FOREGROUND);
    }
}
