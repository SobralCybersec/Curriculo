package com.dev.service;

import com.dev.model.ResumeData;
import com.dev.util.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ResumeService {
    private Path basePath;
    private boolean useExternalFiles;
    
    public ResumeService() {
        this.basePath = findBasePath();
        this.useExternalFiles = Files.exists(basePath);
    }
    
    private Path findBasePath() {
        Path base = Paths.get("src/main/resources/exemplos");
        if (!Files.exists(base)) base = Paths.get("Java/src/main/resources/exemplos");
        if (!Files.exists(base)) base = Paths.get("Curriculo/Java/src/main/resources/exemplos");
        if (!Files.exists(base)) base = Paths.get(System.getProperty("user.home"), ".curriculo-editor");
        return base;
    }
    
    private String loadResource(String path) throws IOException {
        if (useExternalFiles && Files.exists(basePath.resolve(path))) {
            return Files.readString(basePath.resolve(path));
        }
        
        try (InputStream is = getClass().getResourceAsStream("/exemplos/" + path)) {
            if (is == null) throw new FileNotFoundException("Resource not found: " + path);
            return new String(is.readAllBytes());
        }
    }
    
    public String loadMainTex() throws IOException {
        return loadResource("resume.tex");
    }
    
    public String loadSummaryTex() throws IOException {
        return loadResource("resume/summary.tex");
    }
    
    public String loadEducationTex() throws IOException {
        return loadResource("resume/education.tex");
    }
    
    public String loadExperienceTex() throws IOException {
        return loadResource("resume/experience.tex");
    }
    
    public String loadSkillsTex() throws IOException {
        return loadResource("resume/skills.tex");
    }
    
    public void saveAll(String mainTex, String summaryTex, String educationTex, 
                       String experienceTex, String skillsTex) throws IOException {
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
            Files.createDirectories(basePath.resolve("resume"));
        }
        
        Files.writeString(basePath.resolve("resume.tex"), mainTex);
        Files.writeString(basePath.resolve("resume/summary.tex"), summaryTex);
        Files.writeString(basePath.resolve("resume/education.tex"), educationTex);
        Files.writeString(basePath.resolve("resume/experience.tex"), experienceTex);
        Files.writeString(basePath.resolve("resume/skills.tex"), skillsTex);
    }
    
    public int compilePDF() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("xelatex", "-interaction=nonstopmode", "resume.tex");
        pb.directory(basePath.toFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            // Consume output
        }
        
        return p.waitFor();
    }
    
    public void addPDFMetadata(String name, List<String> positions, String summary, 
                              DefaultTableModel skillsModel) throws IOException {
        File pdfFile = basePath.resolve("resume.pdf").toFile();
        if (!pdfFile.exists()) throw new FileNotFoundException("PDF não encontrado");
        
        try (PDDocument doc = PDDocument.load(pdfFile)) {
            PDDocumentInformation info = doc.getDocumentInformation();
            
            String title = String.join(" | ", positions);
            String subject = String.join(", ", positions);
            String keywords = extractKeywords(skillsModel, positions);
            String description = cleanLatex(summary);
            
            info.setProducer(name);
            info.setCreator(name);
            info.setAuthor(name);
            info.setTitle(title);
            info.setSubject(subject);
            info.setKeywords(keywords);
            info.setCustomMetadataValue("Description", description);
            info.setCustomMetadataValue("Category", "CV");
            
            doc.setDocumentInformation(info);
            doc.save(pdfFile);
        }
    }
    
    public void exportPDF(Path destination) throws IOException {
        Files.copy(basePath.resolve("resume.pdf"), destination, StandardCopyOption.REPLACE_EXISTING);
    }
    
    private String extractKeywords(DefaultTableModel skillsModel, List<String> positions) {
        Set<String> keywords = new LinkedHashSet<>();
        
        for (int i = 0; i < skillsModel.getRowCount(); i++) {
            String skills = String.valueOf(skillsModel.getValueAt(i, 1));
            if (skills != null && !skills.equals("null")) {
                String cleaned = skills.replaceAll("\\\\href\\{[^}]*\\}\\{([^}]*)\\}", "$1")
                                      .replaceAll("[{}\\\\]", "")
                                      .replaceAll("\\s+", " ");
                String[] words = cleaned.split("[,;]");
                for (String word : words) {
                    word = word.trim();
                    if (!word.isEmpty()) keywords.add(word);
                }
            }
        }
        
        keywords.addAll(positions);
        return String.join(", ", keywords);
    }
    
    private String cleanLatex(String text) {
        if (text == null) return "";
        return text.replaceAll("\\\\textbf\\{([^}]*)\\}", "$1")
                   .replaceAll("\\\\[a-zA-Z]+\\{([^}]*)\\}", "$1")
                   .replaceAll("[{}\\\\]", "")
                   .trim();
    }
}
