package com.dev.view.components;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BannerPanelTest {

    @Test
    void paintsLongCopySafelyAtCompactWidths() {
        BannerPanel panel = new BannerPanel("Dados Pessoais",
                "Informações básicas do seu currículo profissional");
        panel.setGradientColors(Color.BLUE, Color.BLACK);
        panel.setTitle("Um título profissional muito longo");
        panel.setSubtitle("Uma descrição longa que deve permanecer dentro do cartão");
        panel.setSize(220, 100);
        BufferedImage image = new BufferedImage(220, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            panel.paint(graphics);
        } finally {
            graphics.dispose();
        }

        assertEquals(100, panel.getPreferredSize().height);
        assertTrue((image.getRGB(110, 50) >>> 24) != 0);
    }
}
