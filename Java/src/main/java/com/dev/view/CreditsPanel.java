package com.dev.view;

import com.dev.view.components.BannerPanel;
import com.dev.view.components.ModernPanel;
import com.dev.view.components.RoundedScrollPane;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class CreditsPanel extends JPanel {

    public CreditsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 48));

        ModernPanel contentPanel = new ModernPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        BannerPanel banner = new BannerPanel("Créditos", "Tecnologias e referências utilizadas no projeto", "https://i.imgur.com/DWFJX8f.png");
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(banner);
        contentPanel.add(Box.createVerticalStrut(20));

        addSection(contentPanel, "Desenvolvedor",
            "Matheus Sobral",
            "Desenvolvedor Full Stack focado em Java");

        addSection(contentPanel, "Design & UI",
            "FlatLaf Dark Theme",
            "Modern Look and Feel para aplicações Swing");

        addSection(contentPanel, "Animações",
            "Radiance Animation Library 8.5.0",
            "Framework de animações suaves e profissionais");

        addSection(contentPanel, "Processamento PDF",
            "Apache PDFBox 2.0.30",
            "Manipulação e injeção de metadados ATS");

        addSection(contentPanel, "LaTeX Engine",
            "XeLaTeX",
            "Compilação de documentos LaTeX com suporte Unicode");

        addSection(contentPanel, "Inspiração de Design",
            "Antonio Pelusi - JavaFX Dark Theme",
            "https://github.com/antoniopelusi/JavaFX-Dark-Theme");

        addSection(contentPanel, "Referências Educacionais",
            "• Augusto Galego - Dicas de Currículo Dev",
            "• Fernanda Kipper - Otimização de Currículo",
            "• O Novo Programador - Burlar ATS",
            "• Hackeando a Carreira - Playlist Completa");

        contentPanel.add(Box.createVerticalStrut(20));
        addFooter(contentPanel);
        contentPanel.add(Box.createVerticalGlue());

        RoundedScrollPane scrollPane = new RoundedScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(45, 45, 48));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(45, 45, 48));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapper.add(scrollPane, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private void addSection(JPanel panel, String title, String... lines) {
        JPanel section = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(26, 26, 26));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(new Color(34, 34, 34));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(192, 202, 245));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(titleLabel);
        section.add(Box.createVerticalStrut(8));

        for (String line : lines) {
            JLabel lineLabel = createClickableLabel(line);
            lineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            section.add(lineLabel);
            section.add(Box.createVerticalStrut(4));
        }

        panel.add(section);
        panel.add(Box.createVerticalStrut(15));
    }

    private JLabel createClickableLabel(String text) {
        JLabel label = new JLabel("<html>" + text + "</html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(175, 177, 179));

        if (text.startsWith("http")) {
            label.setForeground(new Color(122, 162, 247));
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    try { Desktop.getDesktop().browse(new URI(text)); } catch (Exception ex) { }
                }
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    label.setText("<html><u>" + text + "</u></html>");
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    label.setText("<html>" + text + "</html>");
                }
            });
        }

        return label;
    }

    private void addFooter(JPanel panel) {
        JLabel versionLabel = new JLabel("Versão 1.3");
        versionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        versionLabel.setForeground(new Color(120, 120, 120));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("Desenvolvido para facilitar a criação de currículos profissionais");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(150, 150, 150));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(versionLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(descLabel);
    }
}
