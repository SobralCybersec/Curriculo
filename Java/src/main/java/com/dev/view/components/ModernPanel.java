package com.dev.view.components;

import javax.swing.*;
import java.awt.*;

public class ModernPanel extends JPanel {
    private int cornerRadius = 15;
    
    public ModernPanel() {
        setOpaque(false);
        setBackground(new Color(45, 45, 48));
    }
    
    public ModernPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBackground(new Color(45, 45, 48));
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    public void setArc(int arc) {
        setCornerRadius(arc);
    }
    
    public void setWithShadow(boolean withShadow) {
        // Mantido para compatibilidade
    }
    
    public void setGradient(Color start, Color end) {
        // Mantido para compatibilidade
    }
}
