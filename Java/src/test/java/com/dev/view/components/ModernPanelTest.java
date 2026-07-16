package com.dev.view.components;

import com.dev.util.UITheme;
import org.junit.jupiter.api.Test;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ModernPanelTest {

    @Test
    void configuresTransparentPanelDefaults() {
        ModernPanel panel = new ModernPanel();

        assertFalse(panel.isOpaque());
        assertEquals(UITheme.BACKGROUND, panel.getBackground());
    }

    @Test
    void retainsProvidedLayoutAndPaintsWithCustomArc() {
        BorderLayout layout = new BorderLayout();
        ModernPanel panel = new ModernPanel(layout);
        panel.setArc(24);
        panel.setSize(80, 40);
        BufferedImage image = new BufferedImage(80, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        try {
            panel.paint(graphics);
        } finally {
            graphics.dispose();
        }

        assertSame(layout, panel.getLayout());
    }
}
