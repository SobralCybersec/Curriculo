package com.dev.view.components;

import com.dev.util.AnimationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AnimatedMenu extends JPanel {
    private List<MenuItem> menuItems;
    private int selectedIndex = -1;
    
    public AnimatedMenu() {
        menuItems = new ArrayList<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(26, 26, 26));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(34, 34, 34)));
    }
    
    public void addMenuItem(String text, Icon icon, Runnable action) {
        MenuItem item = new MenuItem(text, icon, action);
        menuItems.add(item);
        add(item);
        add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    private class MenuItem extends JPanel {
        private String text;
        private Icon icon;
        private Runnable action;
        private boolean isHovered = false;
        private boolean isSelected = false;
        
        public MenuItem(String text, Icon icon, Runnable action) {
            this.text = text;
            this.icon = icon;
            this.action = action;
            
            setLayout(new BorderLayout(10, 0));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            setPreferredSize(new Dimension(200, 45));
            setBackground(new Color(26, 26, 26));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            
            if (icon != null) {
                JLabel iconLabel = new JLabel(icon);
                add(iconLabel, BorderLayout.WEST);
            }
            
            JLabel textLabel = new JLabel(text);
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            textLabel.setForeground(new Color(175, 177, 179));
            add(textLabel, BorderLayout.CENTER);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    AnimationUtil.animateColorTransition(MenuItem.this, getBackground(), new Color(62, 62, 64), 200);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    if (!isSelected) {
                        AnimationUtil.animateColorTransition(MenuItem.this, getBackground(), new Color(26, 26, 26), 200);
                    }
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    AnimationUtil.pulse(MenuItem.this, 200);
                    if (action != null) {
                        action.run();
                    }
                }
            });
        }
        
        public void setSelected(boolean selected) {
            this.isSelected = selected;
            if (selected) {
                setBackground(new Color(78, 78, 78));
            } else {
                setBackground(new Color(26, 26, 26));
            }
        }
    }
}
