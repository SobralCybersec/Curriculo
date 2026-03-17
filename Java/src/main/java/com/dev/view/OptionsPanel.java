package com.dev.view;

import com.dev.view.components.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

public class OptionsPanel extends JPanel {
    private JTextField quoteField;
    private JCheckBox quoteEnabled;
    private JCheckBox photoEnabled;
    private JTextField photoPathField;
    private ModernButton photoUploadBtn;
    private JComboBox<String> photoFormatCombo;
    private ModernButton colorPickerBtn;
    private JLabel colorPreview;
    private String selectedColor = "2B0A3D";
    private Map<String, JTextField> socialFields;
    private Map<String, JCheckBox> socialCheckboxes;
    
    public OptionsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 48));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(45, 45, 48));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(createQuoteSection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createPhotoSection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createColorSection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createSocialPanel());
        mainPanel.add(Box.createVerticalGlue());
        
        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(45, 45, 48));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }
    
    private JPanel createRoundedSection(LayoutManager layout) {
        return new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(26, 26, 26));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(34, 34, 34));
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
    }

    private JPanel createQuoteSection() {
        JPanel section = createRoundedSection(new BorderLayout(10, 10));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel titleLabel = new JLabel("Frase Motivacional");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(192, 202, 245));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        quoteEnabled = new JCheckBox("Habilitar");
        quoteEnabled.setOpaque(false);
        quoteEnabled.setForeground(new Color(175, 177, 179));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(quoteEnabled);
        
        quoteField = new JTextField();
        quoteField.setEnabled(false);
        quoteField.setBackground(new Color(26, 26, 26));
        quoteField.setForeground(new Color(175, 177, 179));
        quoteField.setCaretColor(new Color(175, 177, 179));
        quoteField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 34, 34), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        quoteEnabled.addActionListener(e -> quoteField.setEnabled(quoteEnabled.isSelected()));
        
        section.add(topPanel, BorderLayout.NORTH);
        section.add(quoteField, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createPhotoSection() {
        JPanel section = createRoundedSection(null);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Foto de Perfil");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(192, 202, 245));
        photoEnabled = new JCheckBox("Habilitar");
        photoEnabled.setOpaque(false);
        photoEnabled.setForeground(new Color(175, 177, 179));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalStrut(20));
        headerPanel.add(photoEnabled);
        section.add(headerPanel);
        section.add(Box.createVerticalStrut(10));
        
        JPanel pathPanel = new JPanel(new BorderLayout(10, 0));
        pathPanel.setOpaque(false);
        pathPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        photoPathField = new JTextField();
        photoPathField.setEnabled(false);
        photoPathField.setEditable(false);
        photoPathField.setBackground(new Color(26, 26, 26));
        photoPathField.setForeground(new Color(175, 177, 179));
        photoPathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 34, 34), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        photoUploadBtn = new ModernButton("Selecionar Arquivo");
        photoUploadBtn.setEnabled(false);
        photoUploadBtn.setPreferredSize(new Dimension(150, 40));
        photoUploadBtn.addActionListener(e -> selectPhoto());
        
        pathPanel.add(photoPathField, BorderLayout.CENTER);
        pathPanel.add(photoUploadBtn, BorderLayout.EAST);
        section.add(pathPanel);
        section.add(Box.createVerticalStrut(10));
        
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.setOpaque(false);
        formatPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel formatLabel = new JLabel("Formato:");
        formatLabel.setForeground(new Color(175, 177, 179));
        photoFormatCombo = new JComboBox<>(new String[]{"Padrão", "Retângulo", "Circular", "Lado Direito"});
        photoFormatCombo.setEnabled(false);
        photoFormatCombo.setBackground(new Color(26, 26, 26));
        photoFormatCombo.setForeground(new Color(175, 177, 179));
        photoFormatCombo.setPreferredSize(new Dimension(200, 35));
        formatPanel.add(formatLabel);
        formatPanel.add(Box.createHorizontalStrut(10));
        formatPanel.add(photoFormatCombo);
        section.add(formatPanel);
        
        photoEnabled.addActionListener(e -> {
            boolean enabled = photoEnabled.isSelected();
            photoPathField.setEnabled(enabled);
            photoUploadBtn.setEnabled(enabled);
            photoFormatCombo.setEnabled(enabled);
        });
        
        return section;
    }
    
    private JPanel createColorSection() {
        JPanel section = createRoundedSection(new BorderLayout(10, 10));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel titleLabel = new JLabel("Cor do Tema");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(192, 202, 245));
        
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        contentPanel.setOpaque(false);
        
        colorPickerBtn = new ModernButton("Escolher Cor");
        colorPickerBtn.setPreferredSize(new Dimension(150, 40));
        colorPickerBtn.addActionListener(e -> selectColor());
        
        colorPreview = new JLabel("        ");
        colorPreview.setOpaque(true);
        colorPreview.setBackground(Color.decode("#" + selectedColor));
        colorPreview.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 34, 34), 2),
            BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        
        contentPanel.add(colorPickerBtn);
        contentPanel.add(colorPreview);
        
        section.add(titleLabel, BorderLayout.NORTH);
        section.add(contentPanel, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createSocialPanel() {
        JPanel section = createRoundedSection(null);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        JLabel titleLabel = new JLabel("Redes Sociais Adicionais");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(192, 202, 245));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(titleLabel);
        section.add(Box.createVerticalStrut(15));
        
        socialFields = new HashMap<>();
        socialCheckboxes = new HashMap<>();
        
        String[] socials = {
            "gitlab", "stackoverflow", "twitter", "x", "skype", 
            "reddit", "medium", "kaggle", "hackerrank", "telegram"
        };
        
        for (String social : socials) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            
            JCheckBox checkbox = new JCheckBox(capitalize(social));
            checkbox.setOpaque(false);
            checkbox.setForeground(new Color(175, 177, 179));
            checkbox.setPreferredSize(new Dimension(150, 35));
            socialCheckboxes.put(social, checkbox);
            
            JTextField field = new JTextField();
            field.setEnabled(false);
            field.setBackground(new Color(26, 26, 26));
            field.setForeground(new Color(175, 177, 179));
            field.setCaretColor(new Color(175, 177, 179));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 34, 34), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            socialFields.put(social, field);
            
            checkbox.addActionListener(e -> field.setEnabled(checkbox.isSelected()));
            
            row.add(checkbox, BorderLayout.WEST);
            row.add(field, BorderLayout.CENTER);
            
            section.add(row);
            section.add(Box.createVerticalStrut(8));
        }
        
        return section;
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    public String getQuote() {
        return quoteEnabled.isSelected() ? quoteField.getText() : null;
    }
    
    public void setQuote(String quote) {
        if (quote != null && !quote.isEmpty()) {
            quoteField.setText(quote);
            quoteEnabled.setSelected(true);
            quoteField.setEnabled(true);
        } else {
            quoteField.setText("");
            quoteEnabled.setSelected(false);
            quoteField.setEnabled(false);
        }
    }
    
    public boolean isPhotoEnabled() {
        return photoEnabled.isSelected();
    }
    
    public void setPhotoEnabled(boolean enabled) {
        photoEnabled.setSelected(enabled);
    }
    
    private void selectPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Imagens (jpg, png)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            photoPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void selectColor() {
        Color current = Color.decode("#" + selectedColor);
        Color color = JColorChooser.showDialog(this, "Escolher Cor do Tema", current);
        if (color != null) {
            selectedColor = String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
            colorPreview.setBackground(color);
        }
    }
    
    public String getPhotoPath() {
        return photoEnabled.isSelected() ? photoPathField.getText() : null;
    }
    
    public void setPhotoPath(String path) {
        if (path != null && !path.isEmpty()) {
            photoPathField.setText(path);
        }
    }
    
    public String getPhotoFormat() {
        if (!photoEnabled.isSelected()) return null;
        String format = (String) photoFormatCombo.getSelectedItem();
        if (format.equals("Padrão")) return null;
        if (format.equals("Retângulo")) return "rectangle";
        if (format.equals("Circular")) return "edge";
        if (format.equals("Lado Direito")) return "right";
        return null;
    }
    
    public void setPhotoFormat(String format) {
        if (format == null || format.isEmpty()) {
            photoFormatCombo.setSelectedIndex(0);
        } else if (format.equals("rectangle")) {
            photoFormatCombo.setSelectedItem("Retângulo");
        } else if (format.equals("edge")) {
            photoFormatCombo.setSelectedItem("Circular");
        } else if (format.equals("right")) {
            photoFormatCombo.setSelectedItem("Lado Direito");
        } else {
            photoFormatCombo.setSelectedIndex(0);
        }
    }
    
    public String getThemeColor() {
        return selectedColor;
    }
    
    public void setThemeColor(String color) {
        if (color != null && color.matches("[0-9A-Fa-f]{6}")) {
            selectedColor = color.toUpperCase();
            colorPreview.setBackground(Color.decode("#" + selectedColor));
        }
    }
    
    public Map<String, String> getEnabledSocials() {
        Map<String, String> enabled = new HashMap<>();
        for (String social : socialCheckboxes.keySet()) {
            if (socialCheckboxes.get(social).isSelected()) {
                enabled.put(social, socialFields.get(social).getText());
            }
        }
        return enabled;
    }
    
    public void setSocial(String social, String value) {
        if (socialCheckboxes.containsKey(social)) {
            if (value != null && !value.isEmpty()) {
                socialFields.get(social).setText(value);
                socialCheckboxes.get(social).setSelected(true);
                socialFields.get(social).setEnabled(true);
            } else {
                socialFields.get(social).setText("");
                socialCheckboxes.get(social).setSelected(false);
                socialFields.get(social).setEnabled(false);
            }
        }
    }
}
