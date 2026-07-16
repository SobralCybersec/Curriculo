package com.dev.view.components;

import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.Insets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundedScrollPaneTest {

    @Test
    void wrapsTheProvidedViewWithRoundedDefaults() {
        JPanel view = new JPanel();
        RoundedScrollPane pane = new RoundedScrollPane(view);

        assertSame(view, pane.getViewport().getView());
        assertFalse(pane.isOpaque());
        assertFalse(pane.getViewport().isOpaque());
        assertTrue(pane.getBorder() instanceof EmptyBorder);
        assertEquals(new Insets(5, 5, 5, 5), pane.getBorder().getBorderInsets(pane));
    }

    @Test
    void preservesRequestedScrollbarPolicies() {
        RoundedScrollPane pane = new RoundedScrollPane(new JPanel(),
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        assertEquals(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, pane.getVerticalScrollBarPolicy());
        assertEquals(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, pane.getHorizontalScrollBarPolicy());
    }
}
