package com.dev.view.components;

import com.dev.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

public class SideMenu extends JPanel {
    private static final int ITEM_HEIGHT = 48;
    private static final int ANIMATION_DURATION_MS = 220;

    private final List<MenuButton> menuButtons = new ArrayList<>();
    private final List<JPanel> creditsPanels = new ArrayList<>();
    private final ButtonGroup menuGroup = new ButtonGroup();
    private final int collapsedWidth = UITheme.SIDE_MENU_COLLAPSED_W;
    private final int expandedWidth = UITheme.SIDE_MENU_EXPANDED_W;
    private final JPanel buttonPanel;
    private final JButton toggleButton;
    private boolean expanded;
    private Timer animationTimer;

    public SideMenu() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(collapsedWidth, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel innerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                try {
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(UITheme.SURFACE);
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    g.setColor(UITheme.BORDER);
                    g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                } finally {
                    g.dispose();
                }
                super.paintComponent(graphics);
            }
        };
        innerPanel.setOpaque(false);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 6, 10, 6));

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        innerPanel.add(scrollPane, BorderLayout.CENTER);

        toggleButton = new JButton();
        toggleButton.setIcon(IconFactory.createMenuToggleIcon(20, UITheme.FOREGROUND));
        toggleButton.setRolloverIcon(IconFactory.createMenuToggleIcon(20, UITheme.ACCENT));
        toggleButton.setFont(UITheme.font(Font.BOLD, 13));
        toggleButton.setForeground(UITheme.TEXT_STRONG);
        toggleButton.setHorizontalAlignment(SwingConstants.CENTER);
        toggleButton.setIconTextGap(12);
        toggleButton.setFocusPainted(true);
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.setPreferredSize(new Dimension(collapsedWidth, 58));
        toggleButton.setToolTipText("Expandir ou recolher navegação");
        toggleButton.getAccessibleContext().setAccessibleName("Alternar navegação");
        toggleButton.addActionListener(event -> toggleMenu());

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                graphics.setColor(UITheme.BORDER);
                graphics.drawLine(12, getHeight() - 1, getWidth() - 12, getHeight() - 1);
            }
        };
        header.setOpaque(false);
        header.add(toggleButton, BorderLayout.CENTER);
        innerPanel.add(header, BorderLayout.NORTH);
        add(innerPanel, BorderLayout.CENTER);
    }

    public void addMenuItem(String text, String iconType, String tooltip, Runnable action) {
        MenuButton button = new MenuButton(text, iconType, tooltip, action);
        menuButtons.add(button);
        menuGroup.add(button);
        if (menuButtons.size() == 1) button.setSelected(true);
        buttonPanel.add(button);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    public void addCredits(String version, String author) {
        buttonPanel.add(Box.createVerticalGlue());

        JPanel creditsPanel = new JPanel();
        creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
        creditsPanel.setOpaque(false);
        creditsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        creditsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        creditsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        creditsPanel.setVisible(false);

        JLabel versionLabel = new JLabel(version);
        versionLabel.setFont(UITheme.font(Font.BOLD, 10));
        versionLabel.setForeground(UITheme.ACCENT);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel(author);
        authorLabel.setFont(UITheme.font(Font.PLAIN, 10));
        authorLabel.setForeground(UITheme.TEXT_MUTED);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        creditsPanel.add(versionLabel);
        creditsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        creditsPanel.add(authorLabel);
        buttonPanel.add(creditsPanel);
        creditsPanels.add(creditsPanel);
    }

    private void toggleMenu() {
        if (animationTimer != null && animationTimer.isRunning()) return;

        expanded = !expanded;
        int targetWidth = expanded ? expandedWidth : collapsedWidth;
        int startWidth = getPreferredSize().width;
        for (MenuButton button : menuButtons) button.setExpanded(expanded);
        for (JPanel panel : creditsPanels) panel.setVisible(expanded);
        toggleButton.setText(expanded ? "Currículo Maker" : null);
        toggleButton.setHorizontalAlignment(expanded ? SwingConstants.LEFT : SwingConstants.CENTER);
        toggleButton.setBorder(expanded
                ? BorderFactory.createEmptyBorder(0, 18, 0, 8)
                : BorderFactory.createEmptyBorder());

        long startTime = System.currentTimeMillis();
        animationTimer = new Timer(15, event -> {
            float progress = Math.min(1f,
                    (System.currentTimeMillis() - startTime) / (float) ANIMATION_DURATION_MS);
            float eased = easeInOutCubic(progress);
            int currentWidth = Math.round(startWidth + (targetWidth - startWidth) * eased);
            setPreferredSize(new Dimension(currentWidth, getHeight()));
            revalidate();
            if (progress >= 1f) animationTimer.stop();
        });
        animationTimer.start();
    }

    private static float easeInOutCubic(float value) {
        return value < 0.5f ? 4 * value * value * value
                : 1 - (float) Math.pow(-2 * value + 2, 3) / 2;
    }

    private final class MenuButton extends JToggleButton {
        private final String label;
        private final String tooltip;

        private MenuButton(String text, String iconType, String tooltip, Runnable action) {
            this.label = text;
            this.tooltip = tooltip;
            setIcon(IconFactory.createNavigationIcon(iconType, 20, UITheme.TEXT_MUTED));
            setRolloverIcon(IconFactory.createNavigationIcon(iconType, 20, UITheme.ACCENT_MUTED));
            setSelectedIcon(IconFactory.createNavigationIcon(iconType, 20, UITheme.ACCENT));
            setDisabledIcon(IconFactory.createNavigationIcon(iconType, 20, UITheme.BORDER));
            setFont(UITheme.font(Font.BOLD, 13));
            setForeground(UITheme.FOREGROUND);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, ITEM_HEIGHT));
            setPreferredSize(new Dimension(collapsedWidth - 12, ITEM_HEIGHT));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(tooltip);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setExpanded(false);
            getAccessibleContext().setAccessibleName(text);
            getAccessibleContext().setAccessibleDescription(tooltip);
            addActionListener(event -> {
                setSelected(true);
                if (action != null) action.run();
            });
        }

        private void setExpanded(boolean isExpanded) {
            setText(isExpanded ? label : null);
            setHorizontalAlignment(isExpanded ? SwingConstants.LEFT : SwingConstants.CENTER);
            setIconTextGap(isExpanded ? 14 : 0);
            setBorder(isExpanded
                    ? BorderFactory.createEmptyBorder(0, 14, 0, 12)
                    : BorderFactory.createEmptyBorder());
            setToolTipText(isExpanded ? tooltip : label + " — " + tooltip);
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g.setColor(UITheme.SELECTED);
                    g.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 10, 10);
                    g.setColor(UITheme.ACCENT);
                    g.fillRoundRect(0, 12, 3, getHeight() - 24, 3, 3);
                } else if (getModel().isRollover()) {
                    g.setColor(UITheme.SURFACE_RAISED);
                    g.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 10, 10);
                }

                if (hasFocus()) {
                    g.setColor(UITheme.ACCENT);
                    g.setStroke(new BasicStroke(1.5f));
                    g.drawRoundRect(1, 3, getWidth() - 3, getHeight() - 7, 10, 10);
                }
            } finally {
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }
}
