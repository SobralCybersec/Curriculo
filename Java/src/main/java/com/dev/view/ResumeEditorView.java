package com.dev.view;

import com.dev.service.ResumeService;
import com.dev.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.File;
import java.util.*;

public class ResumeEditorView extends JFrame {
    private final ResumeService service;
    private JTextArea mainTexArea;
    private JTextField nameField, positionField, mobileField, emailField, githubField, linkedinField;
    private JTable summaryTable, educationTable, experienceTable, skillsTable;
    private JTextArea eduDescArea, expDescArea;
    private PDFPreviewPanel pdfPreview;
    private OptionsPanel optionsPanel;
    private java.util.List<String> positions = new ArrayList<>();
    
    public ResumeEditorView() {
        this.service = new ResumeService();
        initUI();
        loadFiles();
    }
    
    private void initUI() {
        setTitle("Currículo Maker");
        setSize(1600, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(950);
        splitPane.setResizeWeight(0.6);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dados Pessoais", createPersonalPanel());
        tabs.addTab("Resumo", createSummaryPanel());
        tabs.addTab("Educação", createEducationPanel());
        tabs.addTab("Experiência", createExperiencePanel());
        tabs.addTab("Habilidades", createSkillsPanel());
        tabs.addTab("Opções", optionsPanel = new OptionsPanel());
        tabs.addTab("Main (resume.tex)", createTextPanel(mainTexArea = new JTextArea()));
        
        pdfPreview = new PDFPreviewPanel();
        
        splitPane.setLeftComponent(tabs);
        splitPane.setRightComponent(pdfPreview);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loadBtn = new JButton("Carregar");
        JButton saveBtn = new JButton("Salvar");
        JButton compileBtn = new JButton("Compilar PDF");
        
        loadBtn.addActionListener(e -> loadFiles());
        saveBtn.addActionListener(e -> saveAndPreview());
        compileBtn.addActionListener(e -> compilePDF());
        
        bottomPanel.add(loadBtn);
        bottomPanel.add(saveBtn);
        bottomPanel.add(compileBtn);
        
        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPersonalPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        nameField = addField(panel, "Nome Completo:", gbc, 0);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Cargos:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        positionField = new JTextField(30);
        positionField.setEditable(false);
        panel.add(positionField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        JButton addPosBtn = new JButton("+");
        addPosBtn.addActionListener(e -> {
            String newPos = JOptionPane.showInputDialog(this, "Digite o cargo:");
            if (newPos != null && !newPos.trim().isEmpty()) {
                positions.add(newPos.trim());
                positionField.setText(String.join(" | ", positions));
            }
        });
        panel.add(addPosBtn, gbc);
        gbc.gridx = 3; gbc.weightx = 0;
        JButton removePosBtn = new JButton("-");
        removePosBtn.addActionListener(e -> {
            if (positions.isEmpty()) return;
            String[] posArray = positions.toArray(new String[0]);
            String selected = (String) JOptionPane.showInputDialog(this, "Selecione o cargo para remover:", 
                "Remover Cargo", JOptionPane.PLAIN_MESSAGE, null, posArray, posArray[0]);
            if (selected != null) {
                positions.remove(selected);
                positionField.setText(String.join(" | ", positions));
            }
        });
        panel.add(removePosBtn, gbc);
        
        mobileField = addField(panel, "Telefone:", gbc, 2);
        emailField = addField(panel, "Email:", gbc, 3);
        githubField = addField(panel, "GitHub:", gbc, 4);
        linkedinField = addField(panel, "LinkedIn:", gbc, 5);
        
        return panel;
    }
    
    private JTextField addField(JPanel panel, String label, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField field = new JTextField(30);
        panel.add(field, gbc);
        return field;
    }
    
    private JPanel createTextPanel(JTextArea area) {
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setTabSize(2);
        JScrollPane scroll = new JScrollPane(area);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll);
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        String[] cols = {"Resumo Profissional"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        summaryTable = new JTable(model);
        summaryTable.setRowHeight(100);
        return createTablePanel(summaryTable, model);
    }
    
    private JPanel createEducationPanel() {
        String[] cols = {"Grau", "Instituição", "Local", "Período"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        educationTable = new JTable(model);
        eduDescArea = new JTextArea(10, 50);
        return createEntryPanel(educationTable, model, eduDescArea);
    }
    
    private JPanel createExperiencePanel() {
        String[] cols = {"Cargo", "Empresa", "Local", "Período"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        experienceTable = new JTable(model);
        expDescArea = new JTextArea(10, 50);
        return createEntryPanel(experienceTable, model, expDescArea);
    }
    
    private JPanel createSkillsPanel() {
        String[] cols = {"Categoria", "Habilidades"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        skillsTable = new JTable(model);
        return createTablePanel(skillsTable, model);
    }
    
    private JPanel createEntryPanel(JTable table, DefaultTableModel model, JTextArea descArea) {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(0, 150));
        
        descArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createTitledBorder("Descrição (um bullet point por linha)"));
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && model.getColumnCount() > 4) {
                    Object desc = model.getValueAt(row, 4);
                    descArea.setText(desc != null ? desc.toString() : "");
                }
            }
        });
        
        descArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void insertUpdate(DocumentEvent e) { update(); }
            void update() {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    while (model.getColumnCount() <= 4) model.addColumn("Descrição");
                    model.setValueAt(descArea.getText(), row, 4);
                }
            }
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Adicionar");
        JButton removeBtn = new JButton("Remover");
        
        addBtn.addActionListener(e -> {
            while (model.getColumnCount() <= 4) model.addColumn("Descrição");
            model.addRow(new Object[model.getColumnCount()]);
            table.setRowSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
        });
        
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                model.removeRow(row);
                descArea.setText("");
            }
        });
        
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        
        mainPanel.add(tableScroll, BorderLayout.NORTH);
        mainPanel.add(descScroll, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createTablePanel(JTable table, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Adicionar");
        JButton removeBtn = new JButton("Remover");
        
        addBtn.addActionListener(e -> model.addRow(new Object[model.getColumnCount()]));
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) model.removeRow(row);
        });
        
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadFiles() {
        try {
            mainTexArea.setText(service.loadMainTex());
            parsePersonalInfo(mainTexArea.getText());
            parseOptions(mainTexArea.getText());
            LatexParser.parseSummary(service.loadSummaryTex(), (DefaultTableModel) summaryTable.getModel());
            LatexParser.parseEducation(service.loadEducationTex(), (DefaultTableModel) educationTable.getModel());
            LatexParser.parseExperience(service.loadExperienceTex(), (DefaultTableModel) experienceTable.getModel());
            LatexParser.parseSkills(service.loadSkillsTex(), (DefaultTableModel) skillsTable.getModel());
            JOptionPane.showMessageDialog(this, "Arquivos carregados!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void parsePersonalInfo(String tex) {
        String firstName = LatexParser.extractValue(tex, "\\name{", "}{");
        String lastName = LatexParser.extractValue(tex, "\\name{" + firstName + "}{", "}");
        nameField.setText(firstName + " " + lastName);
        
        String rawPos = LatexParser.extractValue(tex, "\\position{", "}");
        positions.clear();
        String[] parts = rawPos.split("\\\\enskip\\\\cdotp\\\\enskip");
        for (String pos : parts) {
            String cleaned = pos.replaceAll("[{}]", "").trim();
            if (!cleaned.isEmpty()) positions.add(cleaned);
        }
        positionField.setText(String.join(" | ", positions));
        
        mobileField.setText(LatexParser.extractValue(tex, "\\mobile{", "}"));
        emailField.setText(LatexParser.extractValue(tex, "\\email{", "}"));
        githubField.setText(LatexParser.extractValue(tex, "\\github{", "}"));
        linkedinField.setText(LatexParser.extractValue(tex, "\\linkedin{", "}"));
    }
    
    private void parseOptions(String tex) {
        int quoteStart = tex.indexOf("\\quote{");
        if (quoteStart >= 0 && !tex.substring(Math.max(0, quoteStart - 10), quoteStart).contains("%")) {
            String quote = LatexParser.extractValue(tex, "\\quote{", "}");
            quote = quote.replaceAll("``", "").replaceAll("''", "").replaceAll("&quot;", "").trim();
            optionsPanel.setQuote(quote);
        } else {
            optionsPanel.setQuote(null);
        }
        
        int photoStart = tex.indexOf("\\photo");
        boolean photoEnabled = photoStart >= 0 && !tex.substring(Math.max(0, photoStart - 10), photoStart).contains("%");
        optionsPanel.setPhotoEnabled(photoEnabled);
        
        if (photoEnabled) {
            String photoLine = tex.substring(photoStart, tex.indexOf('\n', photoStart));
            if (photoLine.contains("[rectangle]")) optionsPanel.setPhotoFormat("rectangle");
            else if (photoLine.contains("[edge]")) optionsPanel.setPhotoFormat("edge");
            else if (photoLine.contains("[right]")) optionsPanel.setPhotoFormat("right");
            
            String photoPath = LatexParser.extractValue(photoLine, "{}", "}");
            if (photoPath == null || photoPath.isEmpty()) {
                photoPath = LatexParser.extractValue(photoLine, "{", "}");
            }
            optionsPanel.setPhotoPath(photoPath);
        }
        
        String colorPattern = "\\definecolor{cordeescolha}{HTML}{";
        int colorStart = tex.indexOf(colorPattern);
        if (colorStart >= 0) {
            int valueStart = colorStart + colorPattern.length();
            int valueEnd = tex.indexOf('}', valueStart);
            if (valueEnd > valueStart) {
                String color = tex.substring(valueStart, valueEnd).trim();
                if (color.matches("[0-9A-Fa-f]{6}")) {
                    optionsPanel.setThemeColor(color);
                }
            }
        } else {
            int roxoStart = tex.indexOf("\\definecolor{roxopresenca}{HTML}{");
            if (roxoStart >= 0) {
                int valueStart = roxoStart + "\\definecolor{roxopresenca}{HTML}{".length();
                int valueEnd = tex.indexOf('}', valueStart);
                if (valueEnd > valueStart) {
                    String color = tex.substring(valueStart, valueEnd).trim();
                    if (color.matches("[0-9A-Fa-f]{6}")) {
                        optionsPanel.setThemeColor(color);
                    }
                }
            }
        }
        
        String[] socials = {"gitlab", "stackoverflow", "twitter", "x", "skype", "reddit", "medium", "kaggle", "hackerrank", "telegram"};
        for (String social : socials) {
            int socialStart = tex.indexOf("\\" + social + "{");
            if (socialStart >= 0 && !tex.substring(Math.max(0, socialStart - 10), socialStart).contains("%")) {
                String value = LatexParser.extractValue(tex, "\\" + social + "{", "}");
                optionsPanel.setSocial(social, value);
            } else {
                optionsPanel.setSocial(social, null);
            }
        }
    }
    
    private void saveAndPreview() {
        try {
            saveFiles();
            
            int exitCode = service.compilePDF();
            if (exitCode == 0) {
                DefaultTableModel summaryModel = (DefaultTableModel) summaryTable.getModel();
                String summary = summaryModel.getRowCount() > 0 && summaryModel.getValueAt(0, 0) != null 
                               ? summaryModel.getValueAt(0, 0).toString() : "";
                
                service.addPDFMetadata(nameField.getText(), positions, summary, 
                                      (DefaultTableModel) skillsTable.getModel());
                
                File pdfFile = service.getPDFFile();
                if (pdfFile.exists()) {
                    pdfPreview.loadPDF(pdfFile);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Erro na compilação", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveFiles() {
        try {
            String mainTex = updatePersonalInfo(mainTexArea.getText());
            service.saveAll(mainTex,
                LatexGenerator.generateSummary((DefaultTableModel) summaryTable.getModel()),
                LatexGenerator.generateEducation(educationTable),
                LatexGenerator.generateExperience(experienceTable),
                LatexGenerator.generateSkills((DefaultTableModel) skillsTable.getModel()),
                optionsPanel.getPhotoPath());
            
            JOptionPane.showMessageDialog(this, "Arquivos salvos com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String updatePersonalInfo(String tex) {
        String[] names = nameField.getText().trim().split(" ", 2);
        String firstName = names.length > 0 ? names[0] : "";
        String lastName = names.length > 1 ? names[1] : "";
        
        String formattedPos = String.join("{\\enskip\\cdotp\\enskip}", positions);
        
        int posStart = tex.indexOf("\\position{");
        if (posStart >= 0) {
            int posEnd = posStart + 10;
            int depth = 1;
            while (posEnd < tex.length() && depth > 0) {
                if (tex.charAt(posEnd) == '{') depth++;
                else if (tex.charAt(posEnd) == '}') depth--;
                posEnd++;
            }
            tex = tex.substring(0, posStart) + "\\position{" + formattedPos + "}" + tex.substring(posEnd);
        }
        
        tex = tex.replaceFirst("\\\\name\\{[^}]*\\}\\{[^}]*\\}", "\\\\name{" + firstName + "}{" + lastName + "}");
        tex = tex.replaceFirst("\\\\mobile\\{[^}]*\\}", "\\\\mobile{" + mobileField.getText() + "}");
        tex = tex.replaceFirst("\\\\email\\{[^}]*\\}", "\\\\email{" + emailField.getText() + "}");
        tex = tex.replaceFirst("\\\\github\\{[^}]*\\}", "\\\\github{" + githubField.getText() + "}");
        tex = tex.replaceFirst("\\\\linkedin\\{[^}]*\\}", "\\\\linkedin{" + linkedinField.getText() + "}");
        
        tex = updateOptions(tex);
        
        mainTexArea.setText(tex);
        return tex;
    }
    
    private String updateOptions(String tex) {
        String quote = optionsPanel.getQuote();
        int quoteStart = tex.indexOf("\\quote{");
        if (quoteStart < 0) quoteStart = tex.indexOf("% \\quote{");
        
        if (quoteStart >= 0) {
            int lineStart = tex.lastIndexOf('\n', quoteStart) + 1;
            int lineEnd = tex.indexOf('\n', quoteStart);
            if (lineEnd < 0) lineEnd = tex.length();
            
            String newLine;
            if (quote != null && !quote.isEmpty()) {
                newLine = "\\quote{``" + quote + "''}";
            } else {
                newLine = "% \\quote{``Everything has a price, but the knowledge is free..``}";
            }
            
            tex = tex.substring(0, lineStart) + newLine + tex.substring(lineEnd);
        }
        
        tex = updateColorOption(tex);
        tex = updatePhotoOption(tex);
        
        for (Map.Entry<String, String> entry : optionsPanel.getEnabledSocials().entrySet()) {
            tex = updateSocialOption(tex, entry.getKey(), entry.getValue());
        }
        
        String[] allSocials = {"gitlab", "stackoverflow", "twitter", "x", "skype", "reddit", "medium", "kaggle", "hackerrank", "telegram"};
        for (String social : allSocials) {
            if (!optionsPanel.getEnabledSocials().containsKey(social)) {
                tex = toggleOption(tex, "\\" + social, false);
            }
        }
        
        return tex;
    }
    
    private String updatePhotoOption(String tex) {
        int photoStart = tex.indexOf("\\photo");
        if (photoStart < 0) photoStart = tex.indexOf("% \\photo");
        
        if (photoStart >= 0) {
            int lineStart = tex.lastIndexOf('\n', photoStart) + 1;
            int lineEnd = tex.indexOf('\n', photoStart);
            if (lineEnd < 0) lineEnd = tex.length();
            
            String currentLine = tex.substring(lineStart, lineEnd);
            boolean isCommented = currentLine.trim().startsWith("%");
            
            if (optionsPanel.isPhotoEnabled()) {
                String photoPath = optionsPanel.getPhotoPath();
                if (photoPath != null && !photoPath.isEmpty()) {
                    String format = optionsPanel.getPhotoFormat();
                    String newLine;
                    if (format != null) {
                        newLine = "\\photo[" + format + "]{profile}";
                    } else {
                        newLine = "\\photo{profile}";
                    }
                    tex = tex.substring(0, lineStart) + newLine + tex.substring(lineEnd);
                } else if (!isCommented) {
                    tex = tex.substring(0, lineStart) + "% " + currentLine.trim() + tex.substring(lineEnd);
                }
            } else {
                if (!isCommented) {
                    tex = tex.substring(0, lineStart) + "% " + currentLine.trim() + tex.substring(lineEnd);
                }
            }
        }
        
        return tex;
    }
    
    private String updateColorOption(String tex) {
        String searchPattern = "\\definecolor{cordeescolha}{HTML}{";
        int colorStart = tex.indexOf(searchPattern);
        
        if (colorStart >= 0) {
            int valueStart = colorStart + searchPattern.length();
            int valueEnd = tex.indexOf('}', valueStart);
            
            if (valueEnd > valueStart) {
                String newColor = optionsPanel.getThemeColor();
                tex = tex.substring(0, valueStart) + newColor + tex.substring(valueEnd);
            }
        } else {
            int roxoPresencaPos = tex.indexOf("\\definecolor{roxopresenca}{HTML}{");
            if (roxoPresencaPos >= 0) {
                int lineEnd = tex.indexOf('\n', roxoPresencaPos);
                if (lineEnd > 0) {
                    String newLine = "\n\\definecolor{cordeescolha}{HTML}{" + optionsPanel.getThemeColor() + "}";
                    tex = tex.substring(0, lineEnd) + newLine + tex.substring(lineEnd);
                }
            }
        }
        
        if (tex.contains("\\colorlet{awesome}{roxopresenca}")) {
            tex = tex.replace("\\colorlet{awesome}{roxopresenca}", "\\colorlet{awesome}{cordeescolha}");
        } else if (tex.contains("\\colorlet{awesome}{verdeescuro}")) {
            tex = tex.replace("\\colorlet{awesome}{verdeescuro}", "\\colorlet{awesome}{cordeescolha}");
        }
        
        return tex;
    }
    
    private String updateSocialOption(String tex, String social, String value) {
        String command = "\\" + social + "{";
        int cmdStart = tex.indexOf(command);
        if (cmdStart < 0) cmdStart = tex.indexOf("% " + command);
        
        if (cmdStart >= 0) {
            int lineStart = tex.lastIndexOf('\n', cmdStart) + 1;
            int lineEnd = tex.indexOf('\n', cmdStart);
            if (lineEnd < 0) lineEnd = tex.length();
            
            String newLine = command + value + "}";
            tex = tex.substring(0, lineStart) + newLine + tex.substring(lineEnd);
        }
        
        return tex;
    }
    
    private String toggleOption(String tex, String command, boolean enabled) {
        int cmdStart = tex.indexOf(command);
        if (cmdStart < 0) return tex;
        
        int lineStart = tex.lastIndexOf('\n', cmdStart) + 1;
        String linePrefix = tex.substring(lineStart, cmdStart);
        
        if (enabled && linePrefix.trim().startsWith("%")) {
            tex = tex.substring(0, lineStart) + linePrefix.replaceFirst("% ", "") + tex.substring(cmdStart);
        } else if (!enabled && !linePrefix.trim().startsWith("%")) {
            tex = tex.substring(0, lineStart) + "% " + tex.substring(lineStart);
        }
        
        return tex;
    }
    
    private void compilePDF() {
        try {
            saveFiles();
            
            int exitCode = service.compilePDF();
            if (exitCode == 0) {
                DefaultTableModel summaryModel = (DefaultTableModel) summaryTable.getModel();
                String summary = summaryModel.getRowCount() > 0 && summaryModel.getValueAt(0, 0) != null 
                               ? summaryModel.getValueAt(0, 0).toString() : "";
                
                service.addPDFMetadata(nameField.getText(), positions, summary, 
                                      (DefaultTableModel) skillsTable.getModel());
                exportPDF();
            } else {
                JOptionPane.showMessageDialog(this, "Erro na compilação", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage() + 
                "\n\nCertifique-se de ter o XeLaTeX instalado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportPDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar PDF");
        chooser.setSelectedFile(new File(nameField.getText().replaceAll("\\s+", "_") + "_CV.pdf"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                service.exportPDF(chooser.getSelectedFile().toPath());
                JOptionPane.showMessageDialog(this, "PDF exportado com sucesso!\n" + chooser.getSelectedFile());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
