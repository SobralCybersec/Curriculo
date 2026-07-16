package com.dev.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.table.DefaultTableModel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeServiceTest {

    @Test
    void reloadsFilesSavedAfterServiceConstruction(@TempDir Path tempDir) throws Exception {
        ResumeService service = new ResumeService(tempDir);

        service.saveAll("saved main", "saved summary", "saved education", "saved experience", "saved skills", null);

        assertEquals("saved main", service.loadMainTex());
        assertEquals("saved summary", service.loadSummaryTex());
        assertEquals("saved education", service.loadEducationTex());
        assertEquals("saved experience", service.loadExperienceTex());
        assertEquals("saved skills", service.loadSkillsTex());
    }

    @Test
    void fallsBackToBundledTemplateBeforeFirstSave(@TempDir Path tempDir) throws Exception {
        ResumeService service = new ResumeService(tempDir);

        assertTrue(service.loadMainTex().contains("\\documentclass"));
    }

    @Test
    void copiesPhotoWithoutExtension(@TempDir Path tempDir) throws Exception {
        Path photo = tempDir.resolve("photo");
        Files.writeString(photo, "image bytes");
        ResumeService service = new ResumeService(tempDir.resolve("output"));

        service.saveAll("main", "summary", "education", "experience", "skills", photo.toString());

        assertEquals("image bytes", Files.readString(tempDir.resolve("output/profile")));
    }

    @Test
    void exportsPdfAndWritesMetadata(@TempDir Path tempDir) throws Exception {
        ResumeService service = new ResumeService(tempDir);
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage(new PDRectangle(100, 100)));
            document.save(service.getPDFFile());
        }
        DefaultTableModel skills = new DefaultTableModel(new Object[][]{{"Backend", "Java, \\href{https://example.com}{Spring}"}}, new String[]{"Category", "Skills"});

        service.addPDFMetadata("Ana", List.of("Developer", "Engineer"), "\\textbf{Builds} systems", skills);
        Path exported = tempDir.resolve("exported.pdf");
        service.exportPDF(exported);

        assertTrue(Files.size(exported) > 0);
        try (PDDocument document = PDDocument.load(exported.toFile())) {
            assertEquals("Ana", document.getDocumentInformation().getAuthor());
            assertEquals("Developer | Engineer", document.getDocumentInformation().getTitle());
            assertEquals("Java, Spring, Developer, Engineer", document.getDocumentInformation().getKeywords());
            assertEquals("Builds systems", document.getDocumentInformation().getCustomMetadataValue("Description"));
        }
    }

    @Test
    void failsExportWhenNoPdfHasBeenCompiled(@TempDir Path tempDir) {
        ResumeService service = new ResumeService(tempDir);

        assertThrows(java.nio.file.NoSuchFileException.class, () -> service.exportPDF(tempDir.resolve("output.pdf")));
    }

    @Test
    void ignoresMissingPhotoPaths(@TempDir Path tempDir) throws Exception {
        ResumeService service = new ResumeService(tempDir);

        service.saveAll("main", "summary", "education", "experience", "skills", tempDir.resolve("missing.png").toString());

        assertEquals("main", service.loadMainTex());
        assertTrue(Files.notExists(tempDir.resolve("profile.png")));
    }
}
