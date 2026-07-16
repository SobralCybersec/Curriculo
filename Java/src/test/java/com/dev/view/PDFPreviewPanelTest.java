package com.dev.view;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.SwingUtilities;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Container;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dev.util.UITheme;

class PDFPreviewPanelTest {

    @Test
    void presentsAThemedDocumentWorkbenchShell() throws Exception {
        PDFPreviewPanel panel = onEdt(PDFPreviewPanel::new);
        JCheckBox magnifier = onEdt(() -> findMagnifier(panel));

        assertEquals(UITheme.SURFACE, panel.getBackground());
        assertNotNull(panel.getBorder());
        assertNotNull(magnifier.getIcon());
        assertNotNull(onEdt(() -> findLabel(panel, "Prévia do documento")));
        assertNotNull(onEdt(() -> findLabel(panel, "Aguardando compilação")));
    }

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
            return null;
        }));
        assertTrue(awaitCondition(() -> onEdt(() -> panel.getLoadedPageCount() == 1)));
        onEdt(() -> {
            panel.clear();
            return null;
        });
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
            panel.loadPDF(invalidPdf.toFile());
            panel.loadPDF(validPdf.toFile());
            return null;
        }));
        assertTrue(awaitCondition(() -> onEdt(() -> panel.getLoadedPageCount() == 1)));
        onEdt(() -> {
            panel.clear();
            return null;
        });
    }

    @Test
    void keepsTheMostRecentAsyncPdfLoad(@TempDir Path tempDir) throws Exception {
        Path singlePage = tempDir.resolve("one-page.pdf");
        Path twoPages = tempDir.resolve("two-pages.pdf");
        createPdf(singlePage, 1);
        createPdf(twoPages, 2);
        PDFPreviewPanel panel = onEdt(PDFPreviewPanel::new);

        onEdt(() -> {
            panel.loadPDF(singlePage.toFile());
            panel.loadPDF(twoPages.toFile());
            return null;
        });

        assertTrue(awaitCondition(() -> onEdt(() -> panel.getLoadedPageCount() == 2)));
    }

    @Test
    void populatesEveryPageAndClearsTheSelector(@TempDir Path tempDir) throws Exception {
        Path pdf = tempDir.resolve("three-pages.pdf");
        createPdf(pdf, 3);
        PDFPreviewPanel panel = onEdt(PDFPreviewPanel::new);

        onEdt(() -> {
            panel.loadPDF(pdf.toFile());
            return null;
        });
        assertTrue(awaitCondition(() -> onEdt(() -> panel.getLoadedPageCount() == 3)));

        onEdt(() -> {
            panel.clear();
            return null;
        });
        assertTrue(onEdt(() -> panel.getLoadedPageCount() == 0));
    }

    @Test
    void discardsHighResolutionRenderWhenMagnifierIsTurnedOff(@TempDir Path tempDir) throws Exception {
        Path pdf = tempDir.resolve("magnifier.pdf");
        createPdf(pdf, 1);
        PDFPreviewPanel panel = onEdt(PDFPreviewPanel::new);
        JCheckBox magnifier = onEdt(() -> findMagnifier(panel));
        onEdt(() -> {
            panel.loadPDF(pdf.toFile());
            return null;
        });
        assertTrue(awaitCondition(() -> onEdt(() -> panel.getLoadedPageCount() == 1)));

        onEdt(() -> {
            magnifier.doClick();
            magnifier.doClick();
            return null;
        });
        assertTrue(awaitCondition(() -> onEdt(panel::isRenderIdle)));

        assertFalse(onEdt(panel::hasHighResolutionPreview));
    }

    private static void createPdf(Path pdf, int pageCount) throws Exception {
        try (PDDocument document = new PDDocument()) {
            for (int page = 0; page < pageCount; page++) {
                document.addPage(new PDPage(PDRectangle.A4));
            }
            document.save(pdf.toFile());
        }
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

    private static JLabel findLabel(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel label && text.equals(label.getText())) return label;
            if (component instanceof Container child) {
                JLabel result = findLabel(child, text);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static boolean awaitCondition(Callable<Boolean> condition) throws Exception {
        long deadline = System.nanoTime() + 5_000_000_000L;
        while (System.nanoTime() < deadline) {
            if (condition.call()) return true;
            Thread.sleep(10);
        }
        return false;
    }
}
