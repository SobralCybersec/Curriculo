package com.dev.view;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;

public class PDFPreviewPanel extends JPanel {
    private PDFImageLabel imageLabel;
    private JScrollPane scrollPane;
    private JComboBox<Integer> pageSelector;
    private JCheckBox magnifierToggle;
    private File currentPdfFile;
    private long renderRequest;
    private SwingWorker<RenderedPage, Void> renderWorker;
    private boolean updatingPageSelector;
    
    public PDFPreviewPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 48));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(34, 34, 34), 1),
            "Preview do PDF",
            0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(175, 177, 179)
        ));
        
        imageLabel = new PDFImageLabel();
        scrollPane = new JScrollPane(imageLabel);
        scrollPane.getViewport().setBackground(new Color(42, 42, 44));
        scrollPane.setBorder(null);
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(45, 45, 48));
        
        JLabel pageLabel = new JLabel("Página:");
        pageLabel.setForeground(new Color(175, 177, 179));
        topPanel.add(pageLabel);
        
        pageSelector = new JComboBox<>();
        pageSelector.setBackground(new Color(26, 26, 26));
        pageSelector.setForeground(new Color(175, 177, 179));
        pageSelector.addActionListener(e -> {
            if (!updatingPageSelector) renderCurrentPage();
        });
        topPanel.add(pageSelector);
        
        topPanel.add(Box.createHorizontalStrut(20));
        magnifierToggle = new JCheckBox("Lupa", false);
        magnifierToggle.setBackground(new Color(45, 45, 48));
        magnifierToggle.setForeground(new Color(175, 177, 179));
        magnifierToggle.addActionListener(e -> {
            imageLabel.setMagnifierEnabled(magnifierToggle.isSelected());
            if (magnifierToggle.isSelected()) {
                renderCurrentPage();
            } else {
                imageLabel.clearHighResolutionImage();
            }
        });
        topPanel.add(magnifierToggle);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void loadPDF(File pdfFile) {
        currentPdfFile = pdfFile;
        updatingPageSelector = true;
        try {
            pageSelector.removeAllItems();
        } finally {
            updatingPageSelector = false;
        }
        imageLabel.setImages(null, null);
        imageLabel.setText("Carregando PDF...");
        renderPage(0, true);
    }
    
    private void renderCurrentPage() {
        if (currentPdfFile == null || pageSelector.getSelectedItem() == null) return;
        renderPage((Integer) pageSelector.getSelectedItem() - 1, false);
    }

    private void renderPage(int pageIndex, boolean populatePageSelector) {
        File pdfFile = currentPdfFile;
        if (pdfFile == null) return;
        long request = ++renderRequest;
        if (renderWorker != null) renderWorker.cancel(true);
        boolean renderMagnifier = magnifierToggle.isSelected();
        renderWorker = new SwingWorker<>() {
            @Override
            protected RenderedPage doInBackground() throws Exception {
                try (PDDocument document = PDDocument.load(pdfFile)) {
                    if (pageIndex < 0 || pageIndex >= document.getNumberOfPages()) {
                        throw new IllegalArgumentException("Página inválida");
                    }
                    PDFRenderer pdfRenderer = new PDFRenderer(document);
                    BufferedImage displayImage = pdfRenderer.renderImageWithDPI(pageIndex, 72);
                    BufferedImage highResImage = renderMagnifier ? pdfRenderer.renderImageWithDPI(pageIndex, 200) : null;
                    return new RenderedPage(document.getNumberOfPages(), displayImage, highResImage);
                }
            }

            @Override
            protected void done() {
                if (request != renderRequest || !pdfFile.equals(currentPdfFile) || isCancelled()) return;
                try {
                    RenderedPage page = get();
                    if (populatePageSelector) populatePageSelector(page.pageCount(), pageIndex);
                    imageLabel.setImages(page.displayImage(), page.highResImage());
                } catch (Exception e) {
                    imageLabel.setImages(null, null);
                    imageLabel.setText("Erro ao carregar PDF: " + e.getMessage());
                }
            }
        };
        renderWorker.execute();
    }

    private void populatePageSelector(int pageCount, int pageIndex) {
        updatingPageSelector = true;
        try {
            pageSelector.removeAllItems();
            for (int page = 1; page <= pageCount; page++) {
                pageSelector.addItem(page);
            }
            pageSelector.setSelectedIndex(pageIndex);
        } finally {
            updatingPageSelector = false;
        }
    }
    
    public void clear() {
        currentPdfFile = null;
        renderRequest++;
        if (renderWorker != null) renderWorker.cancel(true);
        renderWorker = null;
        pageSelector.removeAllItems();
        imageLabel.setText("Nenhum PDF carregado");
        imageLabel.setIcon(null);
        imageLabel.setImages(null, null);
    }

    int getLoadedPageCount() {
        return pageSelector.getItemCount();
    }

    private record RenderedPage(int pageCount, BufferedImage displayImage, BufferedImage highResImage) {
    }
    
    private static class PDFImageLabel extends JLabel {
        private BufferedImage displayImage;
        private BufferedImage highResImage;
        private Point mousePos;
        private boolean magnifierEnabled;
        private final Timer repaintTimer;
        private boolean repaintPending;
        private static final int MAGNIFIER_SIZE = 100;
        private static final float MAGNIFIER_ZOOM = 3.0f;
        
        public PDFImageLabel() {
            super("Nenhum PDF carregado", SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            setHorizontalAlignment(SwingConstants.CENTER);
            setForeground(new Color(175, 177, 179));
            setBackground(new Color(42, 42, 44));
            setOpaque(true);
            repaintTimer = new Timer(16, e -> {
                repaintPending = false;
                repaint();
            });
            repaintTimer.setRepeats(false);
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (magnifierEnabled && displayImage != null) {
                        mousePos = e.getPoint();
                        scheduleRepaint();
                    }
                }
            });
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    mousePos = null;
                    repaintTimer.stop();
                    repaintPending = false;
                    repaint();
                }
            });
        }
        
        public void setImages(BufferedImage display, BufferedImage highRes) {
            flushIfReplaced(displayImage, display, highRes);
            flushIfReplaced(highResImage, display, highRes);
            this.displayImage = display;
            this.highResImage = highRes;
            if (display != null) {
                setIcon(new ImageIcon(display));
                setText(null);
            } else {
                setIcon(null);
            }
            mousePos = null;
            repaint();
        }

        private void flushIfReplaced(BufferedImage current, BufferedImage display, BufferedImage highRes) {
            if (current != null && current != display && current != highRes) {
                current.flush();
            }
        }
        
        public void setMagnifierEnabled(boolean enabled) {
            this.magnifierEnabled = enabled;
            mousePos = null;
            if (!enabled) {
                repaintTimer.stop();
                repaintPending = false;
            }
            repaint();
        }

        public void clearHighResolutionImage() {
            flushIfReplaced(highResImage, displayImage, null);
            highResImage = null;
            mousePos = null;
            repaint();
        }

        private void scheduleRepaint() {
            if (repaintPending) return;
            repaintPending = true;
            repaintTimer.restart();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (magnifierEnabled && mousePos != null && displayImage != null && highResImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                Icon icon = getIcon();
                if (icon != null) {
                    int iconX = (getWidth() - icon.getIconWidth()) / 2;
                    int iconY = (getHeight() - icon.getIconHeight()) / 2;
                    
                    int relativeX = mousePos.x - iconX;
                    int relativeY = mousePos.y - iconY;
                    
                    if (relativeX >= 0 && relativeY >= 0 && relativeX < icon.getIconWidth() && relativeY < icon.getIconHeight()) {
                        float scale = (float) highResImage.getWidth() / displayImage.getWidth();
                        
                        int highResX = (int) (relativeX * scale);
                        int highResY = (int) (relativeY * scale);
                        int highResSize = (int) (MAGNIFIER_SIZE * scale / MAGNIFIER_ZOOM);
                        
                        int srcX = highResX - highResSize / 2;
                        int srcY = highResY - highResSize / 2;
                        
                        srcX = Math.max(0, Math.min(srcX, highResImage.getWidth() - highResSize));
                        srcY = Math.max(0, Math.min(srcY, highResImage.getHeight() - highResSize));
                        
                        if (srcX >= 0 && srcY >= 0 && 
                            srcX + highResSize <= highResImage.getWidth() && 
                            srcY + highResSize <= highResImage.getHeight()) {
                            
                            BufferedImage magnified = highResImage.getSubimage(srcX, srcY, highResSize, highResSize);
                            
                            int magnifierX = mousePos.x - MAGNIFIER_SIZE / 2;
                            int magnifierY = mousePos.y - MAGNIFIER_SIZE / 2;
                            
                            magnifierX = Math.max(10, Math.min(magnifierX, getWidth() - MAGNIFIER_SIZE - 10));
                            magnifierY = Math.max(10, Math.min(magnifierY, getHeight() - MAGNIFIER_SIZE - 10));
                            
                            g2d.setColor(new Color(0, 0, 0, 180));
                            g2d.fillOval(magnifierX - 3, magnifierY - 3, MAGNIFIER_SIZE + 6, MAGNIFIER_SIZE + 6);
                            
                            g2d.setClip(new java.awt.geom.Ellipse2D.Float(magnifierX, magnifierY, MAGNIFIER_SIZE, MAGNIFIER_SIZE));
                            g2d.drawImage(magnified, magnifierX, magnifierY, MAGNIFIER_SIZE, MAGNIFIER_SIZE, null);
                            
                            g2d.setClip(null);
                            g2d.setColor(new Color(34, 34, 34));
                            g2d.setStroke(new BasicStroke(4));
                            g2d.drawOval(magnifierX, magnifierY, MAGNIFIER_SIZE, MAGNIFIER_SIZE);
                            
                            g2d.setColor(new Color(175, 177, 179));
                            g2d.setStroke(new BasicStroke(2));
                            g2d.drawOval(magnifierX + 2, magnifierY + 2, MAGNIFIER_SIZE - 4, MAGNIFIER_SIZE - 4);
                        }
                    }
                }
                
                g2d.dispose();
            }
        }
    }
}
