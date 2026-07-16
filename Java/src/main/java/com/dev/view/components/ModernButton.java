package com.dev.view.components;

import com.dev.util.UITheme;

import javax.swing.*;
import java.awt.*;

public class ModernButton extends JButton {
    public enum Style { PRIMARY, SECONDARY, GHOST, DANGER }

    private Color hoverColor;
    private Color pressedColor;
    private Color normalColor;
    private Color borderColor;
    private int cornerRadius = 12;
    
    public ModernButton(String text) {
        super(text);
        init();
    }
    
    public ModernButton(String text, Icon icon) {
        super(text, icon);
        init();
    }
    
    private void init() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setFont(UITheme.font(Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setStyle(Style.SECONDARY);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (!isEnabled()) g2.setComposite(AlphaComposite.SrcOver.derive(0.48f));
        Color fill = getModel().isPressed() ? pressedColor
                : getModel().isRollover() ? hoverColor : normalColor;
        g2.setColor(fill);
        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);

        g2.setColor(hasFocus() ? UITheme.ACCENT : borderColor);
        g2.setStroke(new BasicStroke(hasFocus() ? 2f : 1f));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }

    public void setStyle(Style style) {
        switch (style) {
            case PRIMARY -> {
                normalColor = UITheme.ACCENT;
                hoverColor = UITheme.ACCENT_MUTED;
                pressedColor = UITheme.ACCENT_PRESSED;
                borderColor = UITheme.ACCENT;
                setForeground(UITheme.BACKGROUND);
            }
            case SECONDARY -> {
                normalColor = UITheme.SURFACE_RAISED;
                hoverColor = UITheme.SELECTED;
                pressedColor = UITheme.PRESSED;
                borderColor = UITheme.BORDER;
                setForeground(UITheme.TEXT_STRONG);
            }
            case GHOST -> {
                normalColor = UITheme.SURFACE;
                hoverColor = UITheme.SURFACE_RAISED;
                pressedColor = UITheme.SELECTED;
                borderColor = UITheme.SURFACE;
                setForeground(UITheme.FOREGROUND);
            }
            case DANGER -> {
                normalColor = UITheme.SURFACE_RAISED;
                hoverColor = UITheme.SELECTED;
                pressedColor = UITheme.PRESSED;
                borderColor = UITheme.BORDER;
                setForeground(UITheme.DANGER);
            }
        }
        setBackground(normalColor);
        repaint();
    }
    
    public void setNormalColor(Color color) {
        this.normalColor = color;
        setBackground(color);
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }
    
    public void setPressedColor(Color color) {
        this.pressedColor = color;
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
