package com.dev.util;

import javax.swing.table.DefaultTableModel;

public class LatexParser {
    
    public static String extractValue(String text, String start, String end) {
        int i = text.indexOf(start);
        if (i < 0) return "";
        i += start.length();
        
        if (start.contains("position")) {
            int depth = 1;
            StringBuilder result = new StringBuilder();
            while (i < text.length() && depth > 0) {
                char c = text.charAt(i);
                if (c == '{') depth++;
                else if (c == '}') depth--;
                if (depth > 0) result.append(c);
                i++;
            }
            return result.toString();
        }
        
        int j = text.indexOf(end, i);
        return j > i ? text.substring(i, j) : "";
    }
    
    public static void parseSummary(String tex, DefaultTableModel model) {
        model.setRowCount(0);
        int start = tex.indexOf("\\begin{cvparagraph}");
        int end = tex.indexOf("\\end{cvparagraph}");
        if (start >= 0 && end > start) {
            String content = tex.substring(start + 20, end).replaceAll("%-+", "").trim();
            model.addRow(new Object[]{content});
        }
    }
    
    public static void parseEducation(String tex, DefaultTableModel model) {
        model.setRowCount(0);
        while (model.getColumnCount() < 5) model.addColumn("Descrição");
        String[] entries = tex.split("\\\\cventry");
        for (int i = 1; i < entries.length; i++) {
            model.addRow(new Object[]{
                extractBraces(entries[i], 0),
                extractBraces(entries[i], 1),
                extractBraces(entries[i], 2),
                extractBraces(entries[i], 3),
                extractItems(entries[i])
            });
        }
    }
    
    public static void parseExperience(String tex, DefaultTableModel model) {
        parseEducation(tex, model);
    }
    
    public static void parseSkills(String tex, DefaultTableModel model) {
        model.setRowCount(0);
        String[] entries = tex.split("\\\\cvskill");
        for (int i = 1; i < entries.length; i++) {
            model.addRow(new Object[]{extractBraces(entries[i], 0), extractBraces(entries[i], 1)});
        }
    }
    
    private static String extractBraces(String text, int index) {
        int count = 0, start = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '{') {
                if (count == index) start = i + 1;
                count++;
            } else if (text.charAt(i) == '}' && start > 0) {
                return text.substring(start, i).trim();
            }
        }
        return "";
    }
    
    private static String extractItems(String text) {
        int start = text.indexOf("\\item");
        if (start < 0) return "";
        int end = text.indexOf("\\end{cvitems}", start);
        if (end < 0) return "";
        
        String itemsSection = text.substring(start, end);
        StringBuilder result = new StringBuilder();
        int pos = 0;
        
        while ((pos = itemsSection.indexOf("\\item", pos)) >= 0) {
            int openBrace = itemsSection.indexOf('{', pos);
            if (openBrace < 0) break;
            
            int braceCount = 0, closeBrace = -1;
            for (int i = openBrace; i < itemsSection.length(); i++) {
                if (itemsSection.charAt(i) == '{') braceCount++;
                else if (itemsSection.charAt(i) == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        closeBrace = i;
                        break;
                    }
                }
            }
            
            if (closeBrace > openBrace) {
                String bullet = itemsSection.substring(openBrace + 1, closeBrace).trim();
                if (result.length() > 0) result.append("\n");
                result.append(bullet);
            }
            
            pos = closeBrace > 0 ? closeBrace + 1 : openBrace + 1;
        }
        
        return result.toString();
    }
}
