package com.dev.view;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.SwingUtilities;
import javax.swing.JCheckBox;
import java.awt.Component;
import java.awt.Container;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PDFPreviewPanelTest {

    @Test
    void disablesMagnifierByDefaultToAvoidHighResolutionAllocation() throws Exception {
        PDFPreviewPanel panel = onEdt(PDFPreviewPanel::new);

        JCheckBox magnifier = onEdt(() -> findMagnifier(panel));

        assertNotNull(magnifier);
        assertFalse(magnifier.isSelected());
    }

    @Test
    void loadsAndClearsAnA4Preview(@TempDir Path tempDir) throws Exception {
        Path pdf = tempDir.resolve("preview.pdf");
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage(PDRectangle.A4));
            document.save(pdf.toFile());
        }
        PDFPreviewPanel panel = onEdt(PDFPreviewPanel::new);

        assertDoesNotThrow(() -> onEdt(() -> {
            panel.loadPDF(pdf.toFile());
            panel.clear();
            return null;
        }));
    }

    @Test
    void recoversFromAnInvalidPdfLoad(@TempDir Path tempDir) throws Exception {
        Path validPdf = tempDir.resolve("valid.pdf");
        Path invalidPdf = tempDir.resolve("invalid.pdf");
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage(PDRectangle.A4));
            document.save(validPdf.toFile());
        }
        Files.writeString(invalidPdf, "not a PDF");
        PDFPreviewPanel panel = onEdt(PDFPreviewPanel::new);

        assertDoesNotThrow(() -> onEdt(() -> {
            panel.loadPDF(validPdf.toFile());
            panel.loadPDF(invalidPdf.toFile());
            panel.loadPDF(validPdf.toFile());
            panel.clear();
            return null;
        }));
    }

    private static <T> T onEdt(Callable<T> action) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) return action.call();
        FutureTask<T> task = new FutureTask<>(action);
        SwingUtilities.invokeAndWait(task);
        return task.get();
    }

    private static JCheckBox findMagnifier(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JCheckBox checkBox && "Lupa".equals(checkBox.getText())) {
                return checkBox;
            }
            if (component instanceof Container child) {
                JCheckBox result = findMagnifier(child);
                if (result != null) return result;
            }
        }
        return null;
    }
}
