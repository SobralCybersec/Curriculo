package com.dev.view.components;

import com.dev.util.UITheme;
import javax.swing.*;
import java.awt.*;

public class BannerPanel extends JPanel {
    private String title;
    private String subtitle;
    private Color gradientStart;
    private Color gradientEnd;
    
    public BannerPanel(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.gradientStart = UITheme.SELECTED;
        this.gradientEnd = UITheme.SURFACE;
        
        setPreferredSize(new Dimension(0, 100));
        setOpaque(false);
    }
    
    public BannerPanel(String title, String subtitle, String imageUrl) {
        this(title, subtitle);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        GradientPaint gradient = new GradientPaint(
            0, 0, gradientStart,
            getWidth(), getHeight(), gradientEnd
        );
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        g2.setColor(UITheme.BORDER);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        
        g2.setColor(UITheme.ACCENT);
        g2.setFont(UITheme.font(Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int titleY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 10;
        g2.drawString(fitText(title, fm, getWidth() - 48), 24, titleY);
        
        g2.setColor(UITheme.FOREGROUND);
        g2.setFont(UITheme.font(Font.PLAIN, 13));
        fm = g2.getFontMetrics();
        int subtitleY = titleY + fm.getHeight() + 5;
        g2.drawString(fitText(subtitle, fm, getWidth() - 48), 24, subtitleY);
        
        g2.setColor(UITheme.ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(24, getHeight() - 14, Math.max(24, Math.min(190, getWidth() - 24)), getHeight() - 14);
        
        g2.dispose();
    }

    private static String fitText(String text, FontMetrics metrics, int maxWidth) {
        if (text == null) return "";
        if (maxWidth <= 0 || metrics.stringWidth(text) <= maxWidth) return text;
        String ellipsis = "…";
        int end = text.length();
        while (end > 0 && metrics.stringWidth(text.substring(0, end) + ellipsis) > maxWidth) end--;
        return text.substring(0, end) + ellipsis;
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
}
