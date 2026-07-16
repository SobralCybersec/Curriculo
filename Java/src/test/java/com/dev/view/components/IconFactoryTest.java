package com.dev.view.components;

import org.junit.jupiter.api.Test;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IconFactoryTest {

    @Test
    void createsAndPaintsEveryApplicationIcon() {
        List<Icon> icons = List.of(
            IconFactory.createPlusIcon(16, Color.WHITE),
            IconFactory.createMinusIcon(16, Color.WHITE),
            IconFactory.createSaveIcon(16, Color.WHITE),
            IconFactory.createLoadIcon(16, Color.WHITE),
            IconFactory.createCompileIcon(16, Color.WHITE),
            IconFactory.createInfoIcon(16, Color.WHITE),
            IconFactory.createMagnifierIcon(16, Color.WHITE),
            IconFactory.createMenuToggleIcon(16, Color.WHITE)
        );

        for (Icon icon : icons) {
            BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            try {
                icon.paintIcon(null, graphics, 4, 4);
            } finally {
                graphics.dispose();
            }
            assertEquals(16, icon.getIconWidth());
            assertEquals(16, icon.getIconHeight());
            assertTrue(hasVisiblePixel(image));
        }
    }

    @Test
    void createsAndPaintsEveryNavigationIcon() {
        for (String type : List.of("person", "document", "education", "briefcase",
                "lightning", "settings", "code", "info")) {
            Icon icon = IconFactory.createNavigationIcon(type, 20, Color.CYAN);
            BufferedImage image = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            try {
                icon.paintIcon(null, graphics, 4, 4);
            } finally {
                graphics.dispose();
            }
            assertTrue(hasVisiblePixel(image), () -> "Navigation icon did not paint: " + type);
        }
    }

    @Test
    void rejectsInvalidIconContracts() {
        assertThrows(IllegalArgumentException.class,
                () -> IconFactory.createPlusIcon(7, Color.WHITE));
        assertThrows(IllegalArgumentException.class,
                () -> IconFactory.createNavigationIcon("unknown", 16, Color.WHITE));
        assertThrows(NullPointerException.class,
                () -> IconFactory.createInfoIcon(16, null));
    }

    private static boolean hasVisiblePixel(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if ((image.getRGB(x, y) >>> 24) != 0) return true;
            }
        }
        return false;
    }
}
