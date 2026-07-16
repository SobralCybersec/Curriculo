package com.dev.view;

import com.dev.BuildInfo;
import com.dev.service.MissingXeLaTeXException;
import com.dev.service.ResumeService;
import com.dev.util.AnimationUtil;
import com.dev.util.LatexGenerator;
import com.dev.util.LatexParser;
import com.dev.util.UITheme;
import com.dev.view.components.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

public class ResumeEditorView extends JFrame {
    private final ResumeService service;
    private JTextArea mainTexArea;
    private JTextArea summaryTexArea;
    private JTextArea educationTexArea;
    private JTextArea experienceTexArea;
    private JTextArea skillsTexArea;
    private JTextField nameField, positionField, mobileField, emailField, githubField, linkedinField;
    private JTable summaryTable, educationTable, experienceTable, skillsTable;
    private JTextArea eduDescArea, expDescArea;
    private PDFPreviewPanel pdfPreview;
    private OptionsPanel optionsPanel;
    private ModernButton loadButton, saveButton, compileButton;
    private java.util.List<String> positions = new ArrayList<>();
    private boolean latexInstallerPrompted;
    
    public ResumeEditorView() {
        this.service = new ResumeService();
        initUI();
        loadFiles();
    }
    
    private void initUI() {
        setTitle("Currículo Maker - Editor Profissional");
        setSize(UITheme.WINDOW_WIDTH, UITheme.WINDOW_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setUndecorated(false);
        getContentPane().setBackground(UITheme.BACKGROUND);
        
        JPanel mainContainer = new JPanel(new BorderLayout(0, 10));
        mainContainer.setBackground(UITheme.BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.setBackground(UITheme.BACKGROUND);
        
        JPanel personalPanel = createPersonalPanel();
        JPanel summaryPanel = createSummaryPanel();
        JPanel educationPanel = createEducationPanel();
        JPanel experiencePanel = createExperiencePanel();
        JPanel skillsPanel = createSkillsPanel();
        optionsPanel = new OptionsPanel();
        JPanel mainTexPanel = createLatexTabbedPanel();
        
        cardPanel.add(personalPanel, "personal");
        cardPanel.add(summaryPanel, "summary");
        cardPanel.add(educationPanel, "education");
        cardPanel.add(experiencePanel, "experience");
        cardPanel.add(skillsPanel, "skills");
        cardPanel.add(optionsPanel, "options");
        cardPanel.add(mainTexPanel, "latex");
        cardPanel.add(new CreditsPanel(), "credits");
        
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        
        SideMenu sideMenu = new SideMenu();
        sideMenu.addMenuItem("Dados Pessoais", "person", "Informações pessoais", () -> cardLayout.show(cardPanel, "personal"));
        sideMenu.addMenuItem("Resumo", "document", "Resumo profissional", () -> cardLayout.show(cardPanel, "summary"));
        sideMenu.addMenuItem("Educação", "education", "Formação acadêmica", () -> cardLayout.show(cardPanel, "education"));
        sideMenu.addMenuItem("Experiência", "briefcase", "Experiência profissional", () -> cardLayout.show(cardPanel, "experience"));
        sideMenu.addMenuItem("Habilidades", "lightning", "Competências técnicas", () -> cardLayout.show(cardPanel, "skills"));
        sideMenu.addMenuItem("Opções", "settings", "Configurações", () -> cardLayout.show(cardPanel, "options"));
        sideMenu.addMenuItem("LaTeX", "code", "Código LaTeX", () -> cardLayout.show(cardPanel, "latex"));
        sideMenu.addMenuItem("Créditos", "info", "Sobre o projeto", () -> cardLayout.show(cardPanel, "credits"));
        sideMenu.addCredits("v" + BuildInfo.version(), "Matheus Sobral");
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setBackground(UITheme.BACKGROUND);
        contentPanel.add(sideMenu, BorderLayout.WEST);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(950);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);
        splitPane.setDividerSize(8);
        splitPane.setBackground(UITheme.BACKGROUND);
        
        pdfPreview = new PDFPreviewPanel();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {
                pdfPreview.clear();
            }
        });
        
        splitPane.setLeftComponent(cardPanel);
        splitPane.setRightComponent(pdfPreview);
        
        contentPanel.add(splitPane, BorderLayout.CENTER);
        
        ModernPanel bottomPanel = new ModernPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        loadButton = new ModernButton("  Carregar", IconFactory.createLoadIcon(16, UITheme.FOREGROUND));
        saveButton = new ModernButton("  Salvar", IconFactory.createSaveIcon(16, UITheme.FOREGROUND));
        compileButton = new ModernButton("  Compilar PDF", IconFactory.createCompileIcon(16, UITheme.FOREGROUND));
        
        loadButton.setPreferredSize(new Dimension(140, UITheme.BUTTON_HEIGHT));
        saveButton.setPreferredSize(new Dimension(140, UITheme.BUTTON_HEIGHT));
        compileButton.setPreferredSize(new Dimension(160, UITheme.BUTTON_HEIGHT));
        
        loadButton.addActionListener(e -> {
            loadFiles();
        });
        saveButton.addActionListener(e -> {
            saveAndPreview();
        });
        compileButton.addActionListener(e -> {
            compilePDF();
        });
        
        bottomPanel.add(loadButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(compileButton);
        
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainContainer);
    }
    
    private JPanel createPersonalPanel() {
        ModernPanel panel = new ModernPanel(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        BannerPanel banner = new BannerPanel("Dados Pessoais", "Informações básicas do seu currículo profissional", "https://i.imgur.com/zUoqZij.png");
        panel.add(banner, BorderLayout.NORTH);
        
        ModernPanel formPanel = new ModernPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        nameField = addModernField(formPanel, "Nome Completo:", gbc, 0);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel posLabel = new JLabel("Cargos:");
        posLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        posLabel.setForeground(UITheme.FOREGROUND);
        formPanel.add(posLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        
        positionField = new ModernTextField();
        positionField.setEditable(false);
        positionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        positionField.setPreferredSize(new Dimension(0, 40));
        
        formPanel.add(positionField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        ModernButton addPosBtn = new ModernButton("", IconFactory.createPlusIcon(16, UITheme.FOREGROUND));
        addPosBtn.setPreferredSize(new Dimension(45, 35));
        addPosBtn.addActionListener(e -> {
            AnimationUtil.pulse(addPosBtn, UITheme.PULSE_DURATION_MS);
            String newPos = JOptionPane.showInputDialog(this, "Digite o cargo:");
            if (newPos != null && !newPos.trim().isEmpty()) {
                positions.add(newPos.trim());
                positionField.setText(String.join(" | ", positions));
            }
        });
        formPanel.add(addPosBtn, gbc);
        gbc.gridx = 3; gbc.weightx = 0;
        ModernButton removePosBtn = new ModernButton("", IconFactory.createMinusIcon(16, UITheme.FOREGROUND));
        removePosBtn.setPreferredSize(new Dimension(45, 35));
        removePosBtn.addActionListener(e -> {
            if (positions.isEmpty()) return;
            AnimationUtil.pulse(removePosBtn, UITheme.PULSE_DURATION_MS);
            String[] posArray = positions.toArray(new String[0]);
            String selected = (String) JOptionPane.showInputDialog(this, "Selecione o cargo para remover:", 
                "Remover Cargo", JOptionPane.PLAIN_MESSAGE, null, posArray, posArray[0]);
            if (selected != null) {
                positions.remove(selected);
                positionField.setText(String.join(" | ", positions));
            }
        });
        formPanel.add(removePosBtn, gbc);
        
        mobileField = addModernField(formPanel, "Telefone:", gbc, 2);
        emailField = addModernField(formPanel, "Email:", gbc, 3);
        githubField = addModernField(formPanel, "GitHub:", gbc, 4);
        linkedinField = addModernField(formPanel, "LinkedIn:", gbc, 5);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return UITheme.wrapInScrollPanel(panel);
    }
    
    private JTextField addModernField(JPanel panel, String label, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(UITheme.FOREGROUND);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        
        ModernTextField field = new ModernTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 40));
        
        panel.add(field, gbc);
        gbc.gridwidth = 1;
        return field;
    }
    
    private JPanel createLatexTabbedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(UITheme.SURFACE);
        tabbedPane.setForeground(UITheme.FOREGROUND);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        mainTexArea = new JTextArea();
        summaryTexArea = new JTextArea();
        educationTexArea = new JTextArea();
        experienceTexArea = new JTextArea();
        skillsTexArea = new JTextArea();
        
        tabbedPane.addTab("main.tex", createTexEditorPanel(mainTexArea));
        tabbedPane.addTab("summary.tex", createTexEditorPanel(summaryTexArea));
        tabbedPane.addTab("education.tex", createTexEditorPanel(educationTexArea));
        tabbedPane.addTab("experience.tex", createTexEditorPanel(experienceTexArea));
        tabbedPane.addTab("skills.tex", createTexEditorPanel(skillsTexArea));
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createTexEditorPanel(JTextArea area) {
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setTabSize(2);
        area.setBackground(UITheme.SURFACE);
        area.setForeground(UITheme.FOREGROUND);
        area.setCaretColor(UITheme.FOREGROUND);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        RoundedScrollPane scroll = new RoundedScrollPane(area);
        scroll.setBackground(UITheme.SURFACE);
        scroll.getViewport().setBackground(UITheme.SURFACE);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND);
        panel.add(scroll);
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        ModernPanel mainPanel = new ModernPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        BannerPanel banner = new BannerPanel("Perfil Profissional", "Destaque suas principais qualificações e objetivos", "https://i.imgur.com/AROBkTg.png");
        mainPanel.add(banner, BorderLayout.NORTH);
        
        String[] cols = {"Resumo Profissional"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        summaryTable = new JTable(model) {
            @Override
            public javax.swing.table.TableCellRenderer getCellRenderer(int row, int column) {
                return new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        JTextArea textArea = new JTextArea();
                        textArea.setText(value != null ? value.toString() : "");
                        textArea.setWrapStyleWord(true);
                        textArea.setLineWrap(true);
                        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        textArea.setBackground(isSelected ? UITheme.SELECTED : UITheme.SURFACE);
                        textArea.setForeground(UITheme.FOREGROUND);
                        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        return textArea;
                    }
                };
            }
            
            @Override
            public javax.swing.table.TableCellEditor getCellEditor(int row, int column) {
                JTextArea textArea = new JTextArea();
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                textArea.setBackground(UITheme.SURFACE);
                textArea.setForeground(UITheme.FOREGROUND);
                textArea.setCaretColor(UITheme.FOREGROUND);
                textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                return new javax.swing.DefaultCellEditor(new JTextField()) {
                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object value,
                            boolean isSelected, int row, int column) {
                        textArea.setText(value != null ? value.toString() : "");
                        return new JScrollPane(textArea);
                    }
                    
                    @Override
                    public Object getCellEditorValue() {
                        return textArea.getText();
                    }
                };
            }
        };
        summaryTable.setRowHeight(200);
        ModernPanel contentPanel = new ModernPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        UITheme.styleTable(summaryTable);
        
        RoundedScrollPane scroll = new RoundedScrollPane(summaryTable);
        scroll.setBackground(UITheme.SURFACE);
        scroll.getViewport().setBackground(UITheme.SURFACE);
        scroll.setPreferredSize(new Dimension(0, 400));
        
        contentPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return UITheme.wrapInScrollPanel(mainPanel);
    }
    
    private JPanel createEducationPanel() {
        ModernPanel mainPanel = new ModernPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        BannerPanel banner = new BannerPanel("Educação", "Sua formação acadêmica e certificações", "https://i.imgur.com/0zpdhsj.png");
        mainPanel.add(banner, BorderLayout.NORTH);
        
        String[] cols = {"Grau", "Instituição", "Local", "Período"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        educationTable = new JTable(model);
        eduDescArea = new JTextArea(10, 50);
        
        JPanel entryPanel = createEntryPanel(educationTable, model, eduDescArea);
        mainPanel.add(entryPanel, BorderLayout.CENTER);
        
        return UITheme.wrapInScrollPanel(mainPanel);
    }
    
    private JPanel createExperiencePanel() {
        ModernPanel mainPanel = new ModernPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        BannerPanel banner = new BannerPanel("Experiência Profissional", "Seu histórico de trabalho e conquistas", "https://i.imgur.com/DPghpwl.png");
        mainPanel.add(banner, BorderLayout.NORTH);
        
        String[] cols = {"Cargo", "Empresa", "Local", "Período"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        experienceTable = new JTable(model);
        expDescArea = new JTextArea(10, 50);
        
        JPanel entryPanel = createEntryPanel(experienceTable, model, expDescArea);
        mainPanel.add(entryPanel, BorderLayout.CENTER);
        
        return UITheme.wrapInScrollPanel(mainPanel);
    }
    
    private JPanel createSkillsPanel() {
        ModernPanel mainPanel = new ModernPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        BannerPanel banner = new BannerPanel("Habilidades", "Suas competências técnicas e soft skills", "https://i.imgur.com/NiHnCJp.png");
        mainPanel.add(banner, BorderLayout.NORTH);
        
        String[] cols = {"Categoria", "Habilidades"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        skillsTable = new JTable(model);
        
        JPanel tablePanel = createTablePanel(skillsTable, model);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return UITheme.wrapInScrollPanel(mainPanel);
    }
    
    private JPanel createEntryPanel(JTable table, DefaultTableModel model, JTextArea descArea) {
        ModernPanel mainPanel = new ModernPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        UITheme.styleTable(table);
        
        RoundedScrollPane tableScroll = new RoundedScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(0, 180));
        tableScroll.setBackground(UITheme.SURFACE);
        tableScroll.getViewport().setBackground(UITheme.SURFACE);
        
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(UITheme.SURFACE);
        descArea.setForeground(UITheme.FOREGROUND);
        descArea.setCaretColor(UITheme.FOREGROUND);
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        RoundedScrollPane descScroll = new RoundedScrollPane(descArea);
        descScroll.setBackground(UITheme.SURFACE);
        descScroll.getViewport().setBackground(UITheme.SURFACE);
        
        ModernPanel descPanel = new ModernPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel descLabel = new JLabel("Descrição (um bullet point por linha)");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descLabel.setForeground(UITheme.FOREGROUND);
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        descPanel.add(descLabel, BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        
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
                    while (model.getColumnCount() <= 4) model.addColumn("Descricao");
                    model.setValueAt(descArea.getText(), row, 4);
                }
            }
        });
        
        ModernPanel btnPanel = new ModernPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        ModernButton addBtn = new ModernButton("  Adicionar", IconFactory.createPlusIcon(14, UITheme.FOREGROUND));
        ModernButton removeBtn = new ModernButton("  Remover", IconFactory.createMinusIcon(14, UITheme.FOREGROUND));
        
        addBtn.setPreferredSize(new Dimension(140, 40));
        removeBtn.setPreferredSize(new Dimension(140, 40));
        
        addBtn.addActionListener(e -> {
            AnimationUtil.pulse(addBtn, UITheme.PULSE_DURATION_MS);
            while (model.getColumnCount() <= 4) model.addColumn("Descricao");
            model.addRow(new Object[model.getColumnCount()]);
            table.setRowSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
        });
        
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                AnimationUtil.pulse(removeBtn, UITheme.PULSE_DURATION_MS);
                model.removeRow(row);
                descArea.setText("");
            }
        });
        
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        
        mainPanel.add(tableScroll, BorderLayout.NORTH);
        mainPanel.add(descPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createTablePanel(JTable table, DefaultTableModel model) {
        ModernPanel panel = new ModernPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        UITheme.styleTable(table);
        
        RoundedScrollPane scroll = new RoundedScrollPane(table);
        scroll.setBackground(UITheme.SURFACE);
        scroll.getViewport().setBackground(UITheme.SURFACE);
        panel.add(scroll, BorderLayout.CENTER);
        
        ModernPanel btnPanel = new ModernPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        ModernButton addBtn = new ModernButton("  Adicionar", IconFactory.createPlusIcon(14, UITheme.FOREGROUND));
        ModernButton removeBtn = new ModernButton("  Remover", IconFactory.createMinusIcon(14, UITheme.FOREGROUND));
        
        addBtn.setPreferredSize(new Dimension(140, 40));
        removeBtn.setPreferredSize(new Dimension(140, 40));
        
        addBtn.addActionListener(e -> {
            AnimationUtil.pulse(addBtn, UITheme.PULSE_DURATION_MS);
            model.addRow(new Object[model.getColumnCount()]);
        });
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                AnimationUtil.pulse(removeBtn, UITheme.PULSE_DURATION_MS);
                model.removeRow(row);
            }
        });
        
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadFiles() {
        try {
            mainTexArea.setText(service.loadMainTex());
            summaryTexArea.setText(service.loadSummaryTex());
            educationTexArea.setText(service.loadEducationTex());
            experienceTexArea.setText(service.loadExperienceTex());
            skillsTexArea.setText(service.loadSkillsTex());
            
            parsePersonalInfo(mainTexArea.getText());
            parseOptions(mainTexArea.getText());
            LatexParser.parseSummary(summaryTexArea.getText(), (DefaultTableModel) summaryTable.getModel());
            LatexParser.parseEducation(educationTexArea.getText(), (DefaultTableModel) educationTable.getModel());
            LatexParser.parseExperience(experienceTexArea.getText(), (DefaultTableModel) experienceTable.getModel());
            LatexParser.parseSkills(skillsTexArea.getText(), (DefaultTableModel) skillsTable.getModel());
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
        compileInBackground(true);
    }
    
    private CompilationRequest createCompilationRequest() {
        String mainTex = updatePersonalInfo(mainTexArea.getText());
        DefaultTableModel summaryModel = (DefaultTableModel) summaryTable.getModel();
        String summary = summaryModel.getRowCount() > 0 && summaryModel.getValueAt(0, 0) != null
            ? summaryModel.getValueAt(0, 0).toString() : "";
        return new CompilationRequest(mainTex,
            LatexGenerator.generateSummary((DefaultTableModel) summaryTable.getModel()),
            LatexGenerator.generateEducation(educationTable),
            LatexGenerator.generateExperience(experienceTable),
            LatexGenerator.generateSkills((DefaultTableModel) skillsTable.getModel()),
            optionsPanel.getPhotoPath(),
            nameField.getText(),
            List.copyOf(positions),
            summary,
            copyTableModel((DefaultTableModel) skillsTable.getModel()));
    }

    private DefaultTableModel copyTableModel(DefaultTableModel source) {
        String[] columns = new String[source.getColumnCount()];
        for (int column = 0; column < source.getColumnCount(); column++) {
            columns[column] = source.getColumnName(column);
        }
        DefaultTableModel copy = new DefaultTableModel(columns, 0);
        for (int row = 0; row < source.getRowCount(); row++) {
            Object[] values = new Object[source.getColumnCount()];
            for (int column = 0; column < source.getColumnCount(); column++) {
                values[column] = source.getValueAt(row, column);
            }
            copy.addRow(values);
        }
        return copy;
    }

    private void compileInBackground(boolean previewAfterCompile) {
        CompilationRequest request = createCompilationRequest();
        setActionsEnabled(false);
        new SwingWorker<File, Void>() {
            @Override
            protected File doInBackground() throws Exception {
                service.saveAll(request.mainTex(), request.summaryTex(), request.educationTex(),
                    request.experienceTex(), request.skillsTex(), request.photoPath());
                if (service.compilePDF() != 0) {
                    throw new IOException("Erro na compilação");
                }
                service.addPDFMetadata(request.name(), request.positions(), request.summary(), request.skillsModel());
                return service.getPDFFile();
            }

            @Override
            protected void done() {
                setActionsEnabled(true);
                try {
                    File pdfFile = get();
                    JOptionPane.showMessageDialog(ResumeEditorView.this, "Arquivos salvos com sucesso!");
                    if (previewAfterCompile && pdfFile.exists()) {
                        pdfPreview.loadPDF(pdfFile);
                    } else if (!previewAfterCompile) {
                        exportPDF();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showCompileError(e.getMessage());
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof MissingXeLaTeXException missingXeLaTeX) {
                        showMissingXeLaTeXPrompt(missingXeLaTeX);
                    } else {
                        showCompileError(cause == null ? e.getMessage() : cause.getMessage());
                    }
                }
            }
        }.execute();
    }

    private void setActionsEnabled(boolean enabled) {
        loadButton.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        compileButton.setEnabled(enabled);
        setCursor(enabled ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void showCompileError(String message) {
        JOptionPane.showMessageDialog(this, "Erro: " + message +
            "\n\nCertifique-se de ter o XeLaTeX instalado.", "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void showMissingXeLaTeXPrompt(MissingXeLaTeXException error) {
        if (latexInstallerPrompted) {
            showCompileError(error.getMessage());
            return;
        }
        latexInstallerPrompted = true;
        int choice = JOptionPane.showConfirmDialog(this,
            "XeLaTeX é necessário para gerar PDFs.\n\nAbrir o instalador oficial agora?\n"
                + "Depois da instalação, reinicie o editor.",
            "Instalar XeLaTeX", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            openInstallerPage(error.getInstallationUri());
        }
    }

    private void openInstallerPage(URI installationUri) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(installationUri);
                return;
            }
        } catch (Exception ignored) {
        }
        JOptionPane.showMessageDialog(this, "Abra este link para instalar:\n" + installationUri,
            "Instalador XeLaTeX", JOptionPane.INFORMATION_MESSAGE);
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
        
        tex = replaceCommand(tex, "\\\\name\\{[^}]*\\}\\{[^}]*\\}", "\\name{" + firstName + "}{" + lastName + "}");
        tex = replaceCommand(tex, "\\\\mobile\\{[^}]*\\}", "\\mobile{" + mobileField.getText() + "}");
        tex = replaceCommand(tex, "\\\\email\\{[^}]*\\}", "\\email{" + emailField.getText() + "}");
        tex = replaceCommand(tex, "\\\\github\\{[^}]*\\}", "\\github{" + githubField.getText() + "}");
        tex = replaceCommand(tex, "\\\\linkedin\\{[^}]*\\}", "\\linkedin{" + linkedinField.getText() + "}");
        
        tex = updateOptions(tex);
        
        mainTexArea.setText(tex);
        return tex;
    }

    private String replaceCommand(String tex, String pattern, String replacement) {
        return tex.replaceFirst(pattern, Matcher.quoteReplacement(replacement));
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
        compileInBackground(false);
    }

    private record CompilationRequest(String mainTex, String summaryTex, String educationTex,
                                      String experienceTex, String skillsTex, String photoPath,
                                      String name, List<String> positions, String summary,
                                      DefaultTableModel skillsModel) {
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
