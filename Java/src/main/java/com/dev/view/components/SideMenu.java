package com.dev.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SideMenu extends JPanel {
    private List<MenuButton> menuButtons;
    private List<JPanel> creditsPanels = new ArrayList<>();
    private boolean expanded = false;
    private int collapsedWidth = 70;
    private int expandedWidth = 240;
    private JPanel buttonPanel;
    private JButton toggleButton;
    private javax.swing.Timer animationTimer;
    
    public SideMenu() {
        menuButtons = new ArrayList<>();
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(collapsedWidth, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        JPanel innerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2.setColor(new Color(26, 26, 26));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2.setColor(new Color(34, 34, 34));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                
                g2.dispose();
            }
        };
        innerPanel.setOpaque(false);
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        innerPanel.add(scrollPane, BorderLayout.CENTER);
        
        toggleButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2.setColor(getForeground());
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int lineWidth = 20;
                int spacing = 6;
                
                g2.drawLine(centerX - lineWidth/2, centerY - spacing, centerX + lineWidth/2, centerY - spacing);
                g2.drawLine(centerX - lineWidth/2, centerY, centerX + lineWidth/2, centerY);
                g2.drawLine(centerX - lineWidth/2, centerY + spacing, centerX + lineWidth/2, centerY + spacing);
                
                g2.dispose();
            }
        };
        toggleButton.setFocusPainted(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setForeground(new Color(175, 177, 179));
        toggleButton.setOpaque(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.setPreferredSize(new Dimension(collapsedWidth, 60));
        toggleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        toggleButton.setToolTipText("Expandir/Recolher Menu");
        toggleButton.addActionListener(e -> toggleMenu());
        
        toggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                toggleButton.setForeground(new Color(122, 162, 247));
                toggleButton.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                toggleButton.setForeground(new Color(175, 177, 179));
                toggleButton.repaint();
            }
        });
        
        JPanel togglePanel = new JPanel(new BorderLayout());
        togglePanel.setOpaque(false);
        togglePanel.add(toggleButton, BorderLayout.CENTER);
        innerPanel.add(togglePanel, BorderLayout.NORTH);
        
        add(innerPanel, BorderLayout.CENTER);
    }
    
    public void addMenuItem(String text, String iconType, String tooltip, Runnable action) {
        MenuButton button = new MenuButton(text, iconType, tooltip, action);
        menuButtons.add(button);
        buttonPanel.add(button);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    public void addCredits(String version, String author) {
        buttonPanel.add(Box.createVerticalGlue());

        JPanel creditsPanel = new JPanel();
        creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
        creditsPanel.setOpaque(false);
        creditsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        creditsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        creditsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        creditsPanel.setVisible(false);

        JLabel versionLabel = new JLabel(version);
        versionLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        versionLabel.setForeground(new Color(122, 162, 247));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel(author);
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        authorLabel.setForeground(new Color(100, 100, 100));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        creditsPanel.add(versionLabel);
        creditsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        creditsPanel.add(authorLabel);

        buttonPanel.add(creditsPanel);
        creditsPanels.add(creditsPanel);
    }

    
    private void toggleMenu() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }
        
        expanded = !expanded;
        int targetWidth = expanded ? expandedWidth : collapsedWidth;
        int startWidth = getPreferredSize().width;
        
        final long startTime = System.currentTimeMillis();
        final int duration = 400;
        
        animationTimer = new javax.swing.Timer(10, null);
        animationTimer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / duration);
            
            float easeProgress = easeInOutCubic(progress);
            int currentWidth = (int) (startWidth + (targetWidth - startWidth) * easeProgress);
            
            setPreferredSize(new Dimension(currentWidth, getHeight()));
            revalidate();
            
            if (progress >= 0.5f && !menuButtons.isEmpty()) {
                for (MenuButton btn : menuButtons) {
                    btn.setExpanded(expanded);
                }
            }
            
            if (progress >= 1.0f) {
                for (JPanel cp : creditsPanels) cp.setVisible(expanded);
                animationTimer.stop();
            }
        });
        
        animationTimer.start();
    }
    
    private float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }
    
    private class MenuButton extends JPanel {
        private String text;
        private String iconType;
        private String tooltip;
        private Runnable action;
        private boolean isHovered = false;
        private boolean isExpanded = false;
        private JPanel iconPanel;
        private JLabel textLabel;
        
        public MenuButton(String text, String iconType, String tooltip, Runnable action) {
            this.text = text;
            this.iconType = iconType;
            this.tooltip = tooltip;
            this.action = action;
            
            setLayout(new BorderLayout(10, 0));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            setPreferredSize(new Dimension(collapsedWidth, 50));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText(tooltip);
            
            iconPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    int size = 20;
                    
                    if (isHovered && !isExpanded) {
                        g2.setColor(new Color(62, 62, 64));
                        g2.fillRoundRect(centerX - 20, centerY - 20, 40, 40, 10, 10);
                        g2.setColor(new Color(122, 162, 247, 50));
                        g2.fillRoundRect(centerX - 20, centerY - 20, 40, 40, 10, 10);
                    }
                    
                    Color iconColor = isHovered ? new Color(122, 162, 247) : new Color(175, 177, 179);
                    g2.setColor(iconColor);
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    
                    drawIcon(g2, iconType, centerX, centerY, size, iconColor);
                    g2.dispose();
                }
            };
            iconPanel.setOpaque(false);
            iconPanel.setPreferredSize(new Dimension(60, 50));
            iconPanel.setMinimumSize(new Dimension(60, 50));
            iconPanel.setMaximumSize(new Dimension(60, 50));
            add(iconPanel, BorderLayout.WEST);
            
            textLabel = new JLabel(text);
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            textLabel.setForeground(new Color(175, 177, 179));
            textLabel.setVisible(false);
            add(textLabel, BorderLayout.CENTER);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    if (isExpanded) {
                        setBackground(new Color(62, 62, 64));
                    }
                    textLabel.setForeground(new Color(122, 162, 247));
                    iconPanel.repaint();
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    setBackground(new Color(26, 26, 26));
                    textLabel.setForeground(new Color(175, 177, 179));
                    iconPanel.repaint();
                    repaint();
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (action != null) {
                        action.run();
                    }
                }
            });
        }
        
        private void drawIcon(Graphics2D g2, String type, int x, int y, int size, Color iconColor) {
            switch (type) {
                case "person":
                    g2.drawOval(x - size/4, y - size/2 + 2, size/2, size/2);
                    g2.drawArc(x - size/2, y - size/6, size, size * 2/3, 0, -180);
                    break;
                case "document":
                    g2.drawRect(x - size/2 + 2, y - size/2, size - 4, (int)(size * 1.2));
                    g2.drawLine(x - size/3, y - size/4, x + size/3, y - size/4);
                    g2.drawLine(x - size/3, y, x + size/3, y);
                    g2.drawLine(x - size/3, y + size/4, x + size/3, y + size/4);
                    break;
                case "education":
                    int[] xPoints = {x - size/2, x, x + size/2};
                    int[] yPoints = {y + size/3, y - size/2, y + size/3};
                    g2.drawPolygon(xPoints, yPoints, 3);
                    g2.drawRect(x - size/2, y + size/3, size, size/3);
                    g2.drawLine(x, y + size/3, x, y + size/3 + size/3);
                    break;
                case "briefcase":
                    g2.drawRect(x - size/2, y - size/4, size, (int)(size * 0.7));
                    g2.drawRect(x - size/3, y - size/2, size * 2/3, size/4);
                    g2.drawLine(x - size/4, y, x + size/4, y);
                    break;
                case "lightning":
                    int[] xBolt = {x + size/3, x - size/6, x + size/6, x - size/3, x + size/6};
                    int[] yBolt = {y - size/2, y - size/6, y, y + size/2, y};
                    g2.drawPolyline(xBolt, yBolt, 5);
                    break;
                case "settings":
                    g2.drawOval(x - size/3, y - size/3, (int)(size * 0.66), (int)(size * 0.66));
                    for (int i = 0; i < 8; i++) {
                        double angle = Math.toRadians(i * 45);
                        int x1 = (int)(x + Math.cos(angle) * size/2);
                        int y1 = (int)(y + Math.sin(angle) * size/2);
                        int x2 = (int)(x + Math.cos(angle) * size/3);
                        int y2 = (int)(y + Math.sin(angle) * size/3);
                        g2.drawLine(x2, y2, x1, y1);
                    }
                    break;
                case "code":
                    g2.drawLine(x - size/2, y, x - size/4, y - size/3);
                    g2.drawLine(x - size/2, y, x - size/4, y + size/3);
                    g2.drawLine(x + size/2, y, x + size/4, y - size/3);
                    g2.drawLine(x + size/2, y, x + size/4, y + size/3);
                    g2.drawLine(x - size/6, y - size/4, x + size/6, y + size/4);
                    break;
                case "info":
                    IconFactory.createInfoIcon(size, iconColor).paintIcon(iconPanel, g2, x - size / 2, y - size / 2);
                    break;
            }
        }
        
        public void setExpanded(boolean expanded) {
            this.isExpanded = expanded;
            textLabel.setVisible(expanded);
            revalidate();
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isHovered && isExpanded) {
                g2.setColor(new Color(62, 62, 64));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
                
                g2.setColor(new Color(122, 162, 247, 50));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
            }
            
            g2.dispose();
        }
    }
}
