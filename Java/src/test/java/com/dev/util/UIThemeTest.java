package com.dev.util;

import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UIThemeTest {

    @Test
    void appliesConfiguredTableDefaults() {
        UITheme.applyFlatLaf();

        assertEquals(8, UIManager.get("Button.arc"));
        assertEquals(28, UIManager.get("Table.rowHeight"));
    }

    @Test
    void stylesTablesAndWrapsContentInPanel() {
        JTable table = new JTable(new DefaultTableModel(new Object[][]{{"value"}}, new String[]{"Column"}));

        UITheme.styleTable(table);

        assertEquals(28, table.getRowHeight());
        assertEquals(UITheme.SURFACE, table.getBackground());
        assertInstanceOf(JPanel.class, UITheme.wrapInScrollPanel(new JPanel()));
    }
}
