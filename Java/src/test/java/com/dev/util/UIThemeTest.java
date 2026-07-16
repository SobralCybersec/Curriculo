package com.dev.util;

import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UIThemeTest {

    @Test
    void appliesConfiguredTableDefaults() {
        UITheme.applyFlatLaf();

        assertEquals(12, UIManager.get("Button.arc"));
        assertEquals(28, UIManager.get("Table.rowHeight"));
        assertEquals(UITheme.ACCENT, UIManager.get("Component.focusColor"));
    }

    @Test
    void stylesTablesAndWrapsContentInPanel() {
        JTable table = new JTable(new DefaultTableModel(new Object[][]{{"value"}}, new String[]{"Column"}));

        UITheme.styleTable(table);

        assertEquals(28, table.getRowHeight());
        assertEquals(UITheme.SURFACE, table.getBackground());
        assertInstanceOf(JPanel.class, UITheme.wrapInScrollPanel(new JPanel()));
    }

    @Test
    void appliesCompleteTableSelectionAndHeaderStyling() {
        JTable table = new JTable(new DefaultTableModel(new Object[][]{{"value"}}, new String[]{"Column"}));

        UITheme.styleTable(table);

        assertEquals(UITheme.BORDER, table.getGridColor());
        assertEquals(UITheme.SELECTED, table.getSelectionBackground());
        assertEquals(UITheme.FOREGROUND, table.getSelectionForeground());
        assertTrue(table.getShowHorizontalLines());
        assertTrue(table.getShowVerticalLines());
        assertEquals(UITheme.SURFACE, table.getTableHeader().getBackground());
    }

    @Test
    void preservesContentAndScrollDefaultsInWrapper() {
        JPanel content = new JPanel();
        JPanel wrapper = UITheme.wrapInScrollPanel(content);
        JScrollPane scrollPane = (JScrollPane) wrapper.getComponent(0);

        assertSame(content, scrollPane.getViewport().getView());
        assertEquals(UITheme.BACKGROUND, scrollPane.getViewport().getBackground());
        assertEquals(16, scrollPane.getVerticalScrollBar().getUnitIncrement());
    }
}
