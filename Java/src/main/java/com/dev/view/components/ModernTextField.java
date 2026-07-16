package com.dev.view.components;

import com.dev.util.AnimationUtil;
import com.dev.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernTextField extends JTextField {
    private String placeholder;
    private Icon icon;
    private Color placeholderColor = UITheme.TEXT_MUTED;
    private boolean isFocused = false;
    private boolean isEmpty = true;
    private int cornerRadius = 12;
    
    public ModernTextField() {
        init();
    }
    
    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        init();
    }
    
    public ModernTextField(String placeholder, Icon icon) {
        this.placeholder = placeholder;
        this.icon = icon;
        init();
    }
    
    private void init() {
        setFont(UITheme.font(Font.PLAIN, 13));
        setBackground(UITheme.SURFACE);
        setForeground(UITheme.FOREGROUND);
        setCaretColor(UITheme.ACCENT_MUTED);
        setOpaque(false);
        updateInsets();
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                isEmpty = getText().trim().isEmpty();
                
                if (isEmpty && placeholder != null) {
                    AnimationUtil.shake(ModernTextField.this, 5, 400);
                }
                repaint();
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isFocused) {
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isFocused) {
                    repaint();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (isFocused) {
            g2.setColor(UITheme.SURFACE_RAISED);
        } else {
            g2.setColor(getBackground());
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        if (isFocused) {
            g2.setColor(UITheme.ACCENT);
            g2.setStroke(new BasicStroke(2));
        } else {
            g2.setColor(UITheme.BORDER);
            g2.setStroke(new BasicStroke(1));
        }
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        g2.dispose();
        super.paintComponent(g);
        
        if (icon != null) {
            Graphics2D g3 = (Graphics2D) g.create();
            int iconY = (getHeight() - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g3, 10, iconY);
            g3.dispose();
        }
        
        if (getText().isEmpty() && !isFocused && placeholder != null) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g3.setColor(placeholderColor);
            g3.setFont(getFont());
            FontMetrics fm = g3.getFontMetrics();
            int x = icon != null ? 35 : 15;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g3.drawString(placeholder, x, y);
            g3.dispose();
        }
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    public void setIcon(Icon icon) {
        this.icon = icon;
        updateInsets();
        revalidate();
        repaint();
    }

    private void updateInsets() {
        setBorder(BorderFactory.createEmptyBorder(10, icon != null ? 35 : 15, 10, 15));
    }
    
    public boolean validateNotEmpty() {
        isEmpty = getText().trim().isEmpty();
        if (isEmpty) {
            AnimationUtil.shake(this, 5, 400);
            return false;
        }
        return true;
    }
}
