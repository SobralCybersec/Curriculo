package com.dev.util;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class LatexGenerator {
    
    public static String generateSummary(DefaultTableModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("%\tSECTION TITLE\n");
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("\\cvsection{Perfil Profissional}\n\n\n");
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("%\tCONTENT\n");
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("\\begin{cvparagraph}\n\n");
        sb.append("%---------------------------------------------------------\n");
        
        if (model.getRowCount() > 0 && model.getValueAt(0, 0) != null) {
            sb.append(model.getValueAt(0, 0).toString());
        }
        
        sb.append("\n\\end{cvparagraph}\n");
        return sb.toString();
    }
    
    public static String generateEducation(JTable table) {
        return generateEntry(table, "Educação", "Degree", "Institution");
    }
    
    public static String generateExperience(JTable table) {
        return generateEntry(table, "Experiência", "Job title", "Organization");
    }
    
    public static String generateSkills(DefaultTableModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("%\tSECTION TITLE\n");
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("\\cvsection{Habilidades}\n\n\n");
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("%\tCONTENT\n");
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("\\begin{cvskills}\n\n");
        
        for (int i = 0; i < model.getRowCount(); i++) {
            sb.append("%---------------------------------------------------------\n");
            sb.append("  \\cvskill\n");
            sb.append("    {").append(model.getValueAt(i, 0)).append("}\n");
            sb.append("    {").append(model.getValueAt(i, 1)).append("}\n\n");
        }
        
        sb.append("%---------------------------------------------------------\n");
        sb.append("\\end{cvskills}\n");
        return sb.toString();
    }
    
    private static String generateEntry(JTable table, String section, String label1, String label2) {
        StringBuilder sb = new StringBuilder();
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("%\tSECTION TITLE\n");
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("\\cvsection{").append(section).append("}\n\n\n");
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("%\tCONTENT\n");
        sb.append("%-------------------------------------------------------------------------------\n");
        sb.append("\\begin{cventries}\n\n");
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            sb.append("%---------------------------------------------------------\n");
            sb.append("  \\cventry\n");
            sb.append("    {").append(model.getValueAt(i, 0)).append("} % ").append(label1).append("\n");
            sb.append("    {").append(model.getValueAt(i, 1)).append("} % ").append(label2).append("\n");
            sb.append("    {").append(model.getValueAt(i, 2)).append("} % Location\n");
            sb.append("    {").append(model.getValueAt(i, 3)).append("} % Date(s)\n");
            sb.append("    {\n");
            sb.append("      \\begin{cvitems} % Description(s) bullet points\n");
            
            if (model.getColumnCount() > 4) {
                String desc = String.valueOf(model.getValueAt(i, 4));
                if (desc != null && !desc.equals("null")) {
                    for (String bullet : desc.split("\n")) {
                        bullet = bullet.trim();
                        if (!bullet.isEmpty()) {
                            sb.append("\t\\item {").append(bullet).append("}\n");
                        }
                    }
                }
            }
            
            sb.append("      \\end{cvitems}\n");
            sb.append("    }\n\n");
        }
        
        sb.append("%---------------------------------------------------------\n");
        sb.append("\\end{cventries}\n");
        return sb.toString();
    }
}
