package com.dev.view.components;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dev.util.UITheme;

class ModernButtonTest {

    @Test
    void configuresFlatButtonDefaults() {
        ModernButton button = new ModernButton("Save");

        assertFalse(button.isFocusPainted());
        assertFalse(button.isBorderPainted());
        assertFalse(button.isContentAreaFilled());
        assertEquals(13, button.getFont().getSize());
    }

    @Test
    void appliesCustomNormalColorAndPaints() {
        ModernButton button = new ModernButton("Save");
        button.setNormalColor(Color.ORANGE);
        button.setHoverColor(Color.BLUE);
        button.setPressedColor(Color.RED);
        button.setCornerRadius(20);
        button.setSize(120, 42);
        BufferedImage image = new BufferedImage(120, 42, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        try {
            button.paint(graphics);
        } finally {
            graphics.dispose();
        }

        assertEquals(Color.ORANGE, button.getBackground());
        assertTrue((image.getRGB(60, 20) >>> 24) != 0);
    }

    @Test
    void exposesClearPrimarySecondaryAndDangerHierarchy() {
        ModernButton button = new ModernButton("Compile");

        button.setStyle(ModernButton.Style.PRIMARY);
        assertEquals(UITheme.ACCENT, button.getBackground());
        assertEquals(UITheme.BACKGROUND, button.getForeground());

        button.setStyle(ModernButton.Style.SECONDARY);
        assertEquals(UITheme.SURFACE_RAISED, button.getBackground());
        assertEquals(UITheme.TEXT_STRONG, button.getForeground());

        button.setStyle(ModernButton.Style.DANGER);
        assertEquals(UITheme.DANGER, button.getForeground());
    }
}
