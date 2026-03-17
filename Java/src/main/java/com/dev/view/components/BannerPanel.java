package com.dev.view.components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class BannerPanel extends JPanel {
    private String title;
    private String subtitle;
    private Color gradientStart;
    private Color gradientEnd;
    private BufferedImage backgroundImage;
    private float imageOpacity = 0.35f;
    
    public BannerPanel(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.gradientStart = new Color(62, 62, 64);
        this.gradientEnd = new Color(26, 26, 26);
        
        setPreferredSize(new Dimension(0, 120));
        setOpaque(false);
        
        loadBackgroundPattern();
    }
    
    public BannerPanel(String title, String subtitle, String imageUrl) {
        this.title = title;
        this.subtitle = subtitle;
        this.gradientStart = new Color(62, 62, 64);
        this.gradientEnd = new Color(26, 26, 26);
        
        setPreferredSize(new Dimension(0, 120));
        setOpaque(false);
        
        loadBackgroundFromUrl(imageUrl);
    }
    
    private void loadBackgroundPattern() {
        try {
            int width = 200;
            int height = 200;
            backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = backgroundImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(255, 255, 255, 30));
            for (int i = 0; i < 10; i++) {
                int x = (int)(Math.random() * width);
                int y = (int)(Math.random() * height);
                int size = (int)(Math.random() * 40 + 20);
                g2.fillOval(x, y, size, size);
            }
            
            for (int i = 0; i < 15; i++) {
                int x1 = (int)(Math.random() * width);
                int y1 = (int)(Math.random() * height);
                int x2 = (int)(Math.random() * width);
                int y2 = (int)(Math.random() * height);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x1, y1, x2, y2);
            }
            
            g2.dispose();
        } catch (Exception e) {
            backgroundImage = null;
        }
    }
    
    private void loadBackgroundFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            loadBackgroundPattern();
            return;
        }
        
        try {
            URL url = new URL(imageUrl);
            backgroundImage = ImageIO.read(url);
            
            if (backgroundImage != null) {
                int targetWidth = 800;
                double aspectRatio = (double) backgroundImage.getHeight() / backgroundImage.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                setPreferredSize(new Dimension(0, targetHeight));
                setMinimumSize(new Dimension(0, targetHeight));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, targetHeight));
            }
        } catch (Exception e) {
            loadBackgroundPattern();
        }
    }
    
   @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        // Aplicar o gradiente
        GradientPaint gradient = new GradientPaint(
            0, 0, gradientStart,
            getWidth(), getHeight(), gradientEnd
        );
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        // Desenhar a imagem de fundo
        if (backgroundImage != null) {
            g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, imageOpacity));
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2.setClip(null);
        }
        
        // Desenhar o título
        g2.setColor(new Color(122, 162, 247));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        int titleY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 10;
        g2.drawString(title, 30, titleY);
        
        // Desenhar o subtítulo
        g2.setColor(new Color(200, 200, 200));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fm = g2.getFontMetrics();
        int subtitleY = titleY + fm.getHeight() + 5;
        g2.drawString(subtitle, 30, subtitleY);
        
        // Desenhar a linha abaixo do título
        g2.setColor(new Color(122, 162, 247, 100));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(30, getHeight() - 15, 200, getHeight() - 15);
        
        g2.dispose();
    }
    
    public void setTitle(String title) {
        this.title = title;
        repaint();
    }
    
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        repaint();
    }
    
    public void setGradientColors(Color start, Color end) {
        this.gradientStart = start;
        this.gradientEnd = end;
        repaint();
    }
    
    public void setBackgroundImage(String imageUrl) {
        loadBackgroundFromUrl(imageUrl);
        repaint();
    }
    
    public void setImageOpacity(float opacity) {
        this.imageOpacity = Math.max(0.0f, Math.min(1.0f, opacity));
        repaint();
    }
}
