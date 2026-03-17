package com.dev.view.components;

import com.dev.util.AnimationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AnimatedTextArea extends JTextArea {
    private String placeholder;
    private Color placeholderColor = new Color(85, 85, 85);
    private boolean isFocused = false;
    private boolean isEmpty = true;
    
    public AnimatedTextArea() {
        init();
    }
    
    public AnimatedTextArea(String placeholder) {
        this.placeholder = placeholder;
        init();
    }
    
    private void init() {
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setBackground(new Color(26, 26, 26));
        setForeground(new Color(175, 177, 179));
        setCaretColor(new Color(175, 177, 179));
        setLineWrap(true);
        setWrapStyleWord(true);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 34, 34), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(122, 162, 247), 2),
                    new EmptyBorder(9, 9, 9, 9)
                ));
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                isEmpty = getText().trim().isEmpty();
                
                if (isEmpty) {
                    AnimationUtil.shake(AnimatedTextArea.this, 5, 400);
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 100, 100), 2),
                        new EmptyBorder(9, 9, 9, 9)
                    ));
                } else {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(34, 34, 34), 1),
                        new EmptyBorder(10, 10, 10, 10)
                    ));
                }
                repaint();
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isFocused) {
                    setBackground(new Color(32, 32, 32));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isFocused) {
                    setBackground(new Color(26, 26, 26));
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (getText().isEmpty() && !isFocused && placeholder != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(placeholderColor);
            g2.setFont(getFont());
            g2.drawString(placeholder, 10, 25);
            g2.dispose();
        }
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    public boolean validateNotEmpty() {
        isEmpty = getText().trim().isEmpty();
        if (isEmpty) {
            AnimationUtil.shake(this, 5, 400);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 100, 100), 2),
                new EmptyBorder(9, 9, 9, 9)
            ));
            return false;
        }
        return true;
    }
}
