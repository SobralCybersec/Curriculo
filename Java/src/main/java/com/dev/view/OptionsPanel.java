package com.dev.view;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class OptionsPanel extends JPanel {
    private JTextField quoteField;
    private JCheckBox quoteEnabled;
    private JCheckBox photoEnabled;
    private JTextField photoPathField;
    private JButton photoUploadBtn;
    private JComboBox<String> photoFormatCombo;
    private JButton colorPickerBtn;
    private JLabel colorPreview;
    private String selectedColor = "2B0A3D";
    private Map<String, JTextField> socialFields;
    private Map<String, JCheckBox> socialCheckboxes;
    
    public OptionsPanel() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        quoteEnabled = new JCheckBox("Frase:");
        mainPanel.add(quoteEnabled, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        quoteField = new JTextField(30);
        quoteField.setEnabled(false);
        mainPanel.add(quoteField, gbc);
        
        quoteEnabled.addActionListener(e -> quoteField.setEnabled(quoteEnabled.isSelected()));
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.gridwidth = 1;
        photoEnabled = new JCheckBox("Foto:");
        mainPanel.add(photoEnabled, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        photoPathField = new JTextField(20);
        photoPathField.setEnabled(false);
        photoPathField.setEditable(false);
        mainPanel.add(photoPathField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        photoUploadBtn = new JButton("Upload");
        photoUploadBtn.setEnabled(false);
        mainPanel.add(photoUploadBtn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Formato:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        photoFormatCombo = new JComboBox<>(new String[]{"Padrão", "Retângulo", "Circular", "Lado Direito"});
        photoFormatCombo.setEnabled(false);
        mainPanel.add(photoFormatCombo, gbc);
        
        photoEnabled.addActionListener(e -> {
            boolean enabled = photoEnabled.isSelected();
            photoPathField.setEnabled(enabled);
            photoUploadBtn.setEnabled(enabled);
            photoFormatCombo.setEnabled(enabled);
        });
        
        photoUploadBtn.addActionListener(e -> selectPhoto());
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0;
        mainPanel.add(new JLabel("Cor Tema:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0;
        colorPickerBtn = new JButton("Escolher Cor");
        mainPanel.add(colorPickerBtn, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        colorPreview = new JLabel("      ");
        colorPreview.setOpaque(true);
        colorPreview.setBackground(Color.decode("#" + selectedColor));
        colorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mainPanel.add(colorPreview, gbc);
        
        colorPickerBtn.addActionListener(e -> selectColor());
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        mainPanel.add(createSocialPanel(), gbc);
        
        gbc.gridy = 5; gbc.weighty = 1;
        mainPanel.add(Box.createVerticalGlue(), gbc);
        
        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }
    
    private JPanel createSocialPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Redes Sociais Adicionais"));
        
        socialFields = new HashMap<>();
        socialCheckboxes = new HashMap<>();
        
        String[] socials = {
            "gitlab", "stackoverflow", "twitter", "x", "skype", 
            "reddit", "medium", "kaggle", "hackerrank", "telegram"
        };
        
        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        for (int i = 0; i < socials.length; i++) {
            String social = socials[i];
            
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JCheckBox checkbox = new JCheckBox(capitalize(social) + ":");
            socialCheckboxes.put(social, checkbox);
            grid.add(checkbox, gbc);
            
            gbc.gridx = 1; gbc.weightx = 1;
            JTextField field = new JTextField(20);
            field.setEnabled(false);
            socialFields.put(social, field);
            grid.add(field, gbc);
            
            checkbox.addActionListener(e -> field.setEnabled(checkbox.isSelected()));
        }
        
        panel.add(grid);
        return panel;
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
