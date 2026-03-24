package com.dev.view;

import com.dev.service.ResumeService;
import com.dev.util.AnimationUtil;
import com.dev.util.LatexGenerator;
import com.dev.util.LatexParser;
import com.dev.view.components.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

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
    private java.util.List<String> positions = new ArrayList<>();
    
    public ResumeEditorView() {
        this.service = new ResumeService();
        initUI();
        loadFiles();
    }
    
    private void initUI() {
        setTitle("Currículo Maker - Editor Profissional");
        setSize(1600, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);
        getContentPane().setBackground(new Color(45, 45, 48));
        
        JPanel mainContainer = new JPanel(new BorderLayout(0, 10));
        mainContainer.setBackground(new Color(45, 45, 48));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.setBackground(new Color(45, 45, 48));
        
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
        sideMenu.addCredits("v1.3", "Matheus Sobral");
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setBackground(new Color(45, 45, 48));
        contentPanel.add(sideMenu, BorderLayout.WEST);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(950);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);
        splitPane.setDividerSize(8);
        splitPane.setBackground(new Color(45, 45, 48));
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int width = getWidth();
                splitPane.setDividerLocation((int)(width * 0.6));
            }
        });
        
        pdfPreview = new PDFPreviewPanel();
        
        splitPane.setLeftComponent(cardPanel);
        splitPane.setRightComponent(pdfPreview);
        
        contentPanel.add(splitPane, BorderLayout.CENTER);
        
        ModernPanel bottomPanel = new ModernPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        ModernButton loadBtn = new ModernButton("  Carregar", IconFactory.createLoadIcon(16, new Color(220, 220, 220)));
        ModernButton saveBtn = new ModernButton("  Salvar", IconFactory.createSaveIcon(16, new Color(220, 220, 220)));
        ModernButton compileBtn = new ModernButton("  Compilar PDF", IconFactory.createCompileIcon(16, new Color(220, 220, 220)));
        
        loadBtn.setPreferredSize(new Dimension(140, 42));
        saveBtn.setPreferredSize(new Dimension(140, 42));
        compileBtn.setPreferredSize(new Dimension(160, 42));
        
        loadBtn.setToolTipText("Carregar arquivos LaTeX existentes");
        saveBtn.setToolTipText("Salvar alterações nos arquivos");
        compileBtn.setToolTipText("Compilar e exportar PDF");
        
        loadBtn.addActionListener(e -> {
            // AnimationUtil.pulse(loadBtn, 200);
            loadFiles();
        });
        saveBtn.addActionListener(e -> {
            // AnimationUtil.pulse(saveBtn, 200);
            saveAndPreview();
        });
        compileBtn.addActionListener(e -> {
            // AnimationUtil.pulse(compileBtn, 200);
            compilePDF();
        });
        
        bottomPanel.add(loadBtn);
        bottomPanel.add(saveBtn);
        bottomPanel.add(compileBtn);
        
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
        posLabel.setForeground(new Color(175, 177, 179));
        formPanel.add(posLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        
        positionField = new ModernTextField();
        positionField.setEditable(false);
        positionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        positionField.setPreferredSize(new Dimension(0, 40));
        
        formPanel.add(positionField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        ModernButton addPosBtn = new ModernButton("", IconFactory.createPlusIcon(16, new Color(220, 220, 220)));
        addPosBtn.setPreferredSize(new Dimension(45, 35));
        addPosBtn.setToolTipText("Adicionar cargo");
        addPosBtn.addActionListener(e -> {
            AnimationUtil.pulse(addPosBtn, 200);
            String newPos = JOptionPane.showInputDialog(this, "Digite o cargo:");
            if (newPos != null && !newPos.trim().isEmpty()) {
                positions.add(newPos.trim());
                positionField.setText(String.join(" | ", positions));
            }
        });
        formPanel.add(addPosBtn, gbc);
        gbc.gridx = 3; gbc.weightx = 0;
        ModernButton removePosBtn = new ModernButton("", IconFactory.createMinusIcon(16, new Color(220, 220, 220)));
        removePosBtn.setPreferredSize(new Dimension(45, 35));
        removePosBtn.setToolTipText("Remover cargo");
        removePosBtn.addActionListener(e -> {
            if (positions.isEmpty()) return;
            AnimationUtil.pulse(removePosBtn, 200);
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
        
        RoundedScrollPane scrollPane = new RoundedScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(45, 45, 48));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(45, 45, 48));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }
    
    private JTextField addField(JPanel panel, String label, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField field = new JTextField(30);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field, gbc);
        return field;
    }
    
    private JTextField addModernField(JPanel panel, String label, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(175, 177, 179));
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
        panel.setBackground(new Color(45, 45, 48));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(26, 26, 26));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(new Color(34, 34, 34));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(new Color(26, 26, 26));
        tabbedPane.setForeground(new Color(175, 177, 179));
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
        area.setBackground(new Color(26, 26, 26));
        area.setForeground(new Color(175, 177, 179));
        area.setCaretColor(new Color(175, 177, 179));
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        RoundedScrollPane scroll = new RoundedScrollPane(area);
        scroll.setBackground(new Color(26, 26, 26));
        scroll.getViewport().setBackground(new Color(26, 26, 26));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 48));
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
                        textArea.setBackground(isSelected ? new Color(62, 62, 64) : new Color(26, 26, 26));
                        textArea.setForeground(new Color(175, 177, 179));
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
                textArea.setBackground(new Color(26, 26, 26));
                textArea.setForeground(new Color(175, 177, 179));
                textArea.setCaretColor(new Color(175, 177, 179));
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
        
        summaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        summaryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        summaryTable.setBackground(new Color(26, 26, 26));
        summaryTable.setForeground(new Color(175, 177, 179));
        summaryTable.setGridColor(new Color(34, 34, 34));
        summaryTable.setSelectionBackground(new Color(62, 62, 64));
        summaryTable.setSelectionForeground(new Color(175, 177, 179));
        summaryTable.getTableHeader().setBackground(new Color(26, 26, 26));
        summaryTable.getTableHeader().setForeground(new Color(175, 177, 179));
        summaryTable.setShowGrid(true);
        summaryTable.setIntercellSpacing(new Dimension(1, 1));
        
        RoundedScrollPane scroll = new RoundedScrollPane(summaryTable);
        scroll.setBackground(new Color(26, 26, 26));
        scroll.getViewport().setBackground(new Color(26, 26, 26));
        scroll.setPreferredSize(new Dimension(0, 400));
        
        contentPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        RoundedScrollPane scrollPane = new RoundedScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(45, 45, 48));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(45, 45, 48));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
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
        
        RoundedScrollPane scrollPane = new RoundedScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(45, 45, 48));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(45, 45, 48));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
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
        
        RoundedScrollPane scrollPane = new RoundedScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(45, 45, 48));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(45, 45, 48));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }
    
    private JPanel createSkillsPanel() {
        ModernPanel mainPanel = new ModernPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(45, 45, 48));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        BannerPanel banner = new BannerPanel("Habilidades", "Suas competências técnicas e soft skills", "https://i.imgur.com/NiHnCJp.png");
        mainPanel.add(banner, BorderLayout.NORTH);
        
        String[] cols = {"Categoria", "Habilidades"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        skillsTable = new JTable(model);
        
        JPanel tablePanel = createTablePanel(skillsTable, model);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        RoundedScrollPane scrollPane = new RoundedScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(45, 45, 48));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(45, 45, 48));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }
    
    private JPanel createEntryPanel(JTable table, DefaultTableModel model, JTextArea descArea) {
        ModernPanel mainPanel = new ModernPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setBackground(new Color(26, 26, 26));
        table.setForeground(new Color(175, 177, 179));
        table.setGridColor(new Color(34, 34, 34));
        table.setSelectionBackground(new Color(62, 62, 64));
        table.setSelectionForeground(new Color(175, 177, 179));
        table.getTableHeader().setBackground(new Color(26, 26, 26));
        table.getTableHeader().setForeground(new Color(175, 177, 179));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        RoundedScrollPane tableScroll = new RoundedScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(0, 180));
        tableScroll.setBackground(new Color(26, 26, 26));
        tableScroll.getViewport().setBackground(new Color(26, 26, 26));
        
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(new Color(26, 26, 26));
        descArea.setForeground(new Color(175, 177, 179));
        descArea.setCaretColor(new Color(175, 177, 179));
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        RoundedScrollPane descScroll = new RoundedScrollPane(descArea);
        descScroll.setBackground(new Color(26, 26, 26));
        descScroll.getViewport().setBackground(new Color(26, 26, 26));
        
        ModernPanel descPanel = new ModernPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel descLabel = new JLabel("Descrição (um bullet point por linha)");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descLabel.setForeground(new Color(175, 177, 179));
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
        
        ModernButton addBtn = new ModernButton("  Adicionar", IconFactory.createPlusIcon(14, new Color(220, 220, 220)));
        ModernButton removeBtn = new ModernButton("  Remover", IconFactory.createMinusIcon(14, new Color(220, 220, 220)));
        
        addBtn.setPreferredSize(new Dimension(140, 40));
        removeBtn.setPreferredSize(new Dimension(140, 40));
        
        addBtn.addActionListener(e -> {
            AnimationUtil.pulse(addBtn, 200);
            while (model.getColumnCount() <= 4) model.addColumn("Descricao");
            model.addRow(new Object[model.getColumnCount()]);
            table.setRowSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
        });
        
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                AnimationUtil.pulse(removeBtn, 200);
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
        
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setBackground(new Color(26, 26, 26));
        table.setForeground(new Color(175, 177, 179));
        table.setGridColor(new Color(34, 34, 34));
        table.setSelectionBackground(new Color(62, 62, 64));
        table.setSelectionForeground(new Color(175, 177, 179));
        table.getTableHeader().setBackground(new Color(26, 26, 26));
        table.getTableHeader().setForeground(new Color(175, 177, 179));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        RoundedScrollPane scroll = new RoundedScrollPane(table);
        scroll.setBackground(new Color(26, 26, 26));
        scroll.getViewport().setBackground(new Color(26, 26, 26));
        panel.add(scroll, BorderLayout.CENTER);
        
        ModernPanel btnPanel = new ModernPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        ModernButton addBtn = new ModernButton("  Adicionar", IconFactory.createPlusIcon(14, new Color(220, 220, 220)));
        ModernButton removeBtn = new ModernButton("  Remover", IconFactory.createMinusIcon(14, new Color(220, 220, 220)));
        
        addBtn.setPreferredSize(new Dimension(140, 40));
        removeBtn.setPreferredSize(new Dimension(140, 40));
        
        addBtn.addActionListener(e -> {
            AnimationUtil.pulse(addBtn, 200);
            model.addRow(new Object[model.getColumnCount()]);
        });
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                AnimationUtil.pulse(removeBtn, 200);
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
