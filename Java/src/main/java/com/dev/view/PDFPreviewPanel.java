package com.dev.view;

import com.dev.util.UITheme;
import com.dev.view.components.IconFactory;
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
    private JLabel statusLabel;
    private File currentPdfFile;
    private long renderRequest;
    private SwingWorker<RenderedPage, Void> renderWorker;
    private boolean updatingPageSelector;
    
    public PDFPreviewPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.SURFACE);
        setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        
        imageLabel = new PDFImageLabel();
        scrollPane = new JScrollPane(imageLabel);
        scrollPane.getViewport().setBackground(UITheme.PREVIEW_CANVAS);
        scrollPane.setBorder(null);
        
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(UITheme.SURFACE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(11, 14, 11, 14)));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Prévia do documento");
        titleLabel.setFont(UITheme.font(Font.BOLD, 14));
        titleLabel.setForeground(UITheme.TEXT_STRONG);
        statusLabel = new JLabel("Aguardando compilação");
        statusLabel.setFont(UITheme.font(Font.PLAIN, 11));
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        titlePanel.add(statusLabel);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        controls.setOpaque(false);
        
        JLabel pageLabel = new JLabel("Página:");
        pageLabel.setFont(UITheme.font(Font.PLAIN, 12));
        pageLabel.setForeground(UITheme.TEXT_MUTED);
        controls.add(pageLabel);
        
        pageSelector = new JComboBox<>();
        pageSelector.setBackground(UITheme.SURFACE_RAISED);
        pageSelector.setForeground(UITheme.FOREGROUND);
        pageSelector.setPreferredSize(new Dimension(64, 32));
        pageSelector.addActionListener(e -> {
            if (!updatingPageSelector) renderCurrentPage();
        });
        controls.add(pageSelector);
        
        magnifierToggle = new JCheckBox("Lupa", false);
        magnifierToggle.setIcon(IconFactory.createMagnifierIcon(16, UITheme.TEXT_MUTED));
        magnifierToggle.setSelectedIcon(IconFactory.createMagnifierIcon(16, UITheme.ACCENT));
        magnifierToggle.setIconTextGap(7);
        magnifierToggle.setFont(UITheme.font(Font.PLAIN, 12));
        magnifierToggle.setBackground(UITheme.SURFACE);
        magnifierToggle.setForeground(UITheme.FOREGROUND);
        magnifierToggle.setToolTipText("Ativar lupa de alta resolução");
        magnifierToggle.addActionListener(e -> {
            imageLabel.setMagnifierEnabled(magnifierToggle.isSelected());
            if (magnifierToggle.isSelected()) {
                renderCurrentPage();
            } else {
                imageLabel.clearHighResolutionImage();
            }
        });
        controls.add(magnifierToggle);
        topPanel.add(controls, BorderLayout.CENTER);
        
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
        imageLabel.setIcon(null);
        imageLabel.setText("Carregando PDF...");
        statusLabel.setText("Renderizando documento…");
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
                    BufferedImage highResImage = page.highResImage();
                    if (!magnifierToggle.isSelected() && highResImage != null) {
                        highResImage.flush();
                        highResImage = null;
                    }
                    imageLabel.setImages(page.displayImage(), highResImage);
                    statusLabel.setText("Página " + (pageIndex + 1) + " de " + page.pageCount());
                } catch (Exception e) {
                    imageLabel.setImages(null, null);
                    imageLabel.setText("Erro ao carregar PDF: " + e.getMessage());
                    statusLabel.setText("Não foi possível abrir o documento");
                } finally {
                    if (request == renderRequest) renderWorker = null;
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
        imageLabel.setImages(null, null);
        imageLabel.setIcon(IconFactory.createNavigationIcon("document", 36, UITheme.TEXT_MUTED));
        imageLabel.setIconTextGap(12);
        statusLabel.setText("Aguardando compilação");
    }

    int getLoadedPageCount() {
        return pageSelector.getItemCount();
    }

    boolean hasHighResolutionPreview() {
        return imageLabel.hasHighResolutionImage();
    }

    boolean isRenderIdle() {
        return renderWorker == null;
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
            setFont(UITheme.font(Font.PLAIN, 13));
            setForeground(UITheme.TEXT_MUTED);
            setBackground(UITheme.PREVIEW_CANVAS);
            setIcon(IconFactory.createNavigationIcon("document", 36, UITheme.TEXT_MUTED));
            setIconTextGap(12);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.BOTTOM);
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

        private boolean hasHighResolutionImage() {
            return highResImage != null;
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
                            g2d.setColor(UITheme.BORDER);
                            g2d.setStroke(new BasicStroke(4));
                            g2d.drawOval(magnifierX, magnifierY, MAGNIFIER_SIZE, MAGNIFIER_SIZE);
                            
                            g2d.setColor(UITheme.FOREGROUND);
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
