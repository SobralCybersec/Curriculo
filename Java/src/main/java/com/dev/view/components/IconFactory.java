package com.dev.view.components;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.Objects;

/** Lightweight, dependency-free vector icons with one consistent visual language. */
public final class IconFactory {
    private static final float STROKE_RATIO = 0.105f;

    private IconFactory() {
    }

    public static Icon createPlusIcon(int size, Color color) {
        return createIcon(size, color, (g, s) -> {
            float inset = s * 0.22f;
            float center = s / 2f;
            g.drawLine(Math.round(center), Math.round(inset), Math.round(center), Math.round(s - inset));
            g.drawLine(Math.round(inset), Math.round(center), Math.round(s - inset), Math.round(center));
        });
    }

    public static Icon createMinusIcon(int size, Color color) {
        return createIcon(size, color, (g, s) -> {
            float inset = s * 0.22f;
            float center = s / 2f;
            g.drawLine(Math.round(inset), Math.round(center), Math.round(s - inset), Math.round(center));
        });
    }

    public static Icon createSaveIcon(int size, Color color) {
        return createIcon(size, color, (g, s) -> {
            float p = s * 0.16f;
            Path2D.Float body = new Path2D.Float();
            body.moveTo(p, p);
            body.lineTo(s * 0.70f, p);
            body.lineTo(s - p, s * 0.32f);
            body.lineTo(s - p, s - p);
            body.lineTo(p, s - p);
            body.closePath();
            g.draw(body);
            g.drawRoundRect(Math.round(s * 0.30f), Math.round(s * 0.54f),
                    Math.round(s * 0.40f), Math.round(s * 0.30f), 2, 2);
            g.drawLine(Math.round(s * 0.32f), Math.round(p), Math.round(s * 0.32f), Math.round(s * 0.38f));
            g.drawLine(Math.round(s * 0.32f), Math.round(s * 0.38f), Math.round(s * 0.64f), Math.round(s * 0.38f));
        });
    }

    public static Icon createLoadIcon(int size, Color color) {
        return createIcon(size, color, (g, s) -> {
            float p = s * 0.13f;
            Path2D.Float folder = new Path2D.Float();
            folder.moveTo(p, s * 0.28f);
            folder.lineTo(s * 0.40f, s * 0.28f);
            folder.lineTo(s * 0.50f, s * 0.40f);
            folder.lineTo(s - p, s * 0.40f);
            folder.lineTo(s - p, s * 0.84f);
            folder.lineTo(p, s * 0.84f);
            folder.closePath();
            g.draw(folder);
            g.drawLine(Math.round(s * 0.50f), Math.round(s * 0.10f), Math.round(s * 0.50f), Math.round(s * 0.60f));
            g.drawLine(Math.round(s * 0.34f), Math.round(s * 0.46f), Math.round(s * 0.50f), Math.round(s * 0.62f));
            g.drawLine(Math.round(s * 0.66f), Math.round(s * 0.46f), Math.round(s * 0.50f), Math.round(s * 0.62f));
        });
    }

    public static Icon createCompileIcon(int size, Color color) {
        return createIcon(size, color, (g, s) -> {
            Path2D.Float play = new Path2D.Float();
            play.moveTo(s * 0.30f, s * 0.18f);
            play.lineTo(s * 0.78f, s * 0.50f);
            play.lineTo(s * 0.30f, s * 0.82f);
            play.closePath();
            g.draw(play);
        });
    }

    public static Icon createInfoIcon(int size, Color color) {
        return createIcon(size, color, (g, s) -> {
            float p = s * 0.12f;
            g.drawOval(Math.round(p), Math.round(p), Math.round(s - 2 * p), Math.round(s - 2 * p));
            int center = Math.round(s / 2f);
            g.fillOval(center - 1, Math.round(s * 0.30f), 3, 3);
            g.drawLine(center, Math.round(s * 0.46f), center, Math.round(s * 0.69f));
        });
    }

    public static Icon createMagnifierIcon(int size, Color color) {
        return createIcon(size, color, (g, s) -> {
            float d = s * 0.54f;
            float p = s * 0.13f;
            g.drawOval(Math.round(p), Math.round(p), Math.round(d), Math.round(d));
            g.drawLine(Math.round(s * 0.62f), Math.round(s * 0.62f),
                    Math.round(s * 0.86f), Math.round(s * 0.86f));
        });
    }

    public static Icon createMenuToggleIcon(int size, Color color) {
        return createIcon(size, color, (g, s) -> {
            int left = Math.round(s * 0.18f);
            int right = Math.round(s * 0.82f);
            for (float y : new float[]{0.28f, 0.50f, 0.72f}) {
                g.drawLine(left, Math.round(s * y), right, Math.round(s * y));
            }
        });
    }

    public static Icon createNavigationIcon(String type, int size, Color color) {
        Objects.requireNonNull(type, "type");
        switch (type) {
            case "person", "document", "education", "briefcase", "lightning", "settings", "code", "info" -> {
                // Supported application destinations.
            }
            default -> throw new IllegalArgumentException("Unknown navigation icon: " + type);
        }
        return createIcon(size, color, (g, s) -> paintNavigationIcon(g, type, s));
    }

    private static void paintNavigationIcon(Graphics2D g, String type, float s) {
        switch (type) {
            case "person" -> {
                g.drawOval(Math.round(s * 0.36f), Math.round(s * 0.12f), Math.round(s * 0.28f), Math.round(s * 0.28f));
                Path2D.Float shoulders = new Path2D.Float();
                shoulders.moveTo(s * 0.19f, s * 0.84f);
                shoulders.curveTo(s * 0.23f, s * 0.57f, s * 0.37f, s * 0.49f, s * 0.50f, s * 0.49f);
                shoulders.curveTo(s * 0.63f, s * 0.49f, s * 0.77f, s * 0.57f, s * 0.81f, s * 0.84f);
                g.draw(shoulders);
            }
            case "document" -> {
                Path2D.Float page = new Path2D.Float();
                page.moveTo(s * 0.23f, s * 0.10f);
                page.lineTo(s * 0.62f, s * 0.10f);
                page.lineTo(s * 0.79f, s * 0.27f);
                page.lineTo(s * 0.79f, s * 0.88f);
                page.lineTo(s * 0.23f, s * 0.88f);
                page.closePath();
                g.draw(page);
                g.drawLine(Math.round(s * 0.62f), Math.round(s * 0.10f), Math.round(s * 0.62f), Math.round(s * 0.28f));
                g.drawLine(Math.round(s * 0.62f), Math.round(s * 0.28f), Math.round(s * 0.79f), Math.round(s * 0.28f));
                g.drawLine(Math.round(s * 0.35f), Math.round(s * 0.48f), Math.round(s * 0.67f), Math.round(s * 0.48f));
                g.drawLine(Math.round(s * 0.35f), Math.round(s * 0.65f), Math.round(s * 0.62f), Math.round(s * 0.65f));
            }
            case "education" -> {
                Path2D.Float cap = new Path2D.Float();
                cap.moveTo(s * 0.08f, s * 0.38f);
                cap.lineTo(s * 0.50f, s * 0.16f);
                cap.lineTo(s * 0.92f, s * 0.38f);
                cap.lineTo(s * 0.50f, s * 0.60f);
                cap.closePath();
                g.draw(cap);
                g.drawLine(Math.round(s * 0.24f), Math.round(s * 0.48f), Math.round(s * 0.24f), Math.round(s * 0.71f));
                g.drawArc(Math.round(s * 0.24f), Math.round(s * 0.48f), Math.round(s * 0.52f), Math.round(s * 0.34f), 180, 180);
                g.drawLine(Math.round(s * 0.82f), Math.round(s * 0.42f), Math.round(s * 0.82f), Math.round(s * 0.72f));
            }
            case "briefcase" -> {
                g.drawRoundRect(Math.round(s * 0.10f), Math.round(s * 0.30f), Math.round(s * 0.80f), Math.round(s * 0.55f), 3, 3);
                g.drawRoundRect(Math.round(s * 0.34f), Math.round(s * 0.14f), Math.round(s * 0.32f), Math.round(s * 0.20f), 3, 3);
                g.drawLine(Math.round(s * 0.10f), Math.round(s * 0.53f), Math.round(s * 0.42f), Math.round(s * 0.64f));
                g.drawLine(Math.round(s * 0.58f), Math.round(s * 0.64f), Math.round(s * 0.90f), Math.round(s * 0.53f));
                g.drawRoundRect(Math.round(s * 0.43f), Math.round(s * 0.58f), Math.round(s * 0.14f), Math.round(s * 0.13f), 2, 2);
            }
            case "lightning" -> {
                Path2D.Float bolt = new Path2D.Float();
                bolt.moveTo(s * 0.57f, s * 0.08f);
                bolt.lineTo(s * 0.21f, s * 0.55f);
                bolt.lineTo(s * 0.48f, s * 0.55f);
                bolt.lineTo(s * 0.41f, s * 0.92f);
                bolt.lineTo(s * 0.79f, s * 0.43f);
                bolt.lineTo(s * 0.52f, s * 0.43f);
                bolt.closePath();
                g.draw(bolt);
            }
            case "settings" -> {
                g.drawOval(Math.round(s * 0.34f), Math.round(s * 0.34f), Math.round(s * 0.32f), Math.round(s * 0.32f));
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4d;
                    int x1 = Math.round((float) (s * 0.50f + Math.cos(angle) * s * 0.29f));
                    int y1 = Math.round((float) (s * 0.50f + Math.sin(angle) * s * 0.29f));
                    int x2 = Math.round((float) (s * 0.50f + Math.cos(angle) * s * 0.43f));
                    int y2 = Math.round((float) (s * 0.50f + Math.sin(angle) * s * 0.43f));
                    g.drawLine(x1, y1, x2, y2);
                }
            }
            case "code" -> {
                Path2D.Float left = new Path2D.Float();
                left.moveTo(s * 0.38f, s * 0.23f);
                left.lineTo(s * 0.13f, s * 0.50f);
                left.lineTo(s * 0.38f, s * 0.77f);
                g.draw(left);
                Path2D.Float right = new Path2D.Float();
                right.moveTo(s * 0.62f, s * 0.23f);
                right.lineTo(s * 0.87f, s * 0.50f);
                right.lineTo(s * 0.62f, s * 0.77f);
                g.draw(right);
                g.drawLine(Math.round(s * 0.56f), Math.round(s * 0.16f), Math.round(s * 0.44f), Math.round(s * 0.84f));
            }
            case "info" -> createInfoIcon(Math.round(s), g.getColor()).paintIcon(null, g, 0, 0);
            default -> throw new IllegalArgumentException("Unknown navigation icon: " + type);
        }
    }

    private static Icon createIcon(int size, Color color, IconPainter painter) {
        if (size < 8) throw new IllegalArgumentException("Icon size must be at least 8");
        Objects.requireNonNull(color, "color");
        return new VectorIcon(size, color, painter);
    }

    @FunctionalInterface
    private interface IconPainter {
        void paint(Graphics2D graphics, float size);
    }

    private record VectorIcon(int size, Color color, IconPainter painter) implements Icon {
        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.translate(x, y);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g.setColor(color);
                g.setStroke(new BasicStroke(Math.max(1.5f, size * STROKE_RATIO),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                painter.paint(g, size);
            } finally {
                g.dispose();
            }
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
