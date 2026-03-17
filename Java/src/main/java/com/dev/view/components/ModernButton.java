package com.dev.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    private Color hoverColor;
    private Color pressedColor;
    private Color normalColor;
    private int cornerRadius = 12;
    private float shadowAlpha = 0.0f;
    
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
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        normalColor = new Color(26, 26, 26);
        hoverColor = new Color(62, 62, 64);
        pressedColor = new Color(78, 78, 78);
        
        setBackground(normalColor);
        setForeground(new Color(220, 220, 220));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                shadowAlpha = 0.3f;
                setBackground(hoverColor);
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                shadowAlpha = 0.0f;
                setBackground(normalColor);
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedColor);
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (contains(e.getPoint())) {
                    setBackground(hoverColor);
                } else {
                    setBackground(normalColor);
                }
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        if (shadowAlpha > 0) {
            g2.setColor(new Color(0, 0, 0, (int)(shadowAlpha * 100)));
            g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 3, cornerRadius, cornerRadius);
        }
        
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        g2.dispose();
        super.paintComponent(g);
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
