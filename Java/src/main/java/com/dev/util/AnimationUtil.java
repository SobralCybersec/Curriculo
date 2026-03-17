package com.dev.util;

import org.pushingpixels.radiance.animation.api.Timeline;
import org.pushingpixels.radiance.animation.api.ease.Spline;

import javax.swing.*;
import java.awt.*;

public class AnimationUtil {
    
    public static void fadeIn(JComponent component, int durationMs) {
        float[] alpha = {0.0f};
        
        Timer timer = new Timer(20, null);
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / durationMs);
            alpha[0] = progress;
            
            component.putClientProperty("fadeAlpha", alpha[0]);
            component.repaint();
            
            if (progress >= 1.0f) {
                timer.stop();
            }
        });
        
        component.setVisible(true);
        timer.start();
    }
    
    public static void pulse(JComponent component, int durationMs) {
        Dimension originalSize = component.getSize();
        int targetWidth = (int)(originalSize.width * 1.08);
        int targetHeight = (int)(originalSize.height * 1.08);
        
        Timeline timeline = Timeline.builder(component)
            .addPropertyToInterpolate("size", originalSize, new Dimension(targetWidth, targetHeight))
            .setDuration(durationMs / 2)
            .setEase(new Spline(0.4f))
            .build();
        
        timeline.playLoop(1, Timeline.RepeatBehavior.REVERSE);
    }
    
    public static void animateColorTransition(JComponent component, Color from, Color to, int durationMs) {
        Timeline timeline = Timeline.builder(component)
            .addPropertyToInterpolate("background", from, to)
            .setDuration(durationMs)
            .setEase(new Spline(0.4f))
            .build();
        
        timeline.play();
    }
    
    public static void slideIn(JComponent component, String direction, int durationMs) {
        Point originalLocation = component.getLocation();
        Dimension size = component.getSize();
        Container parent = component.getParent();
        
        Point startLocation = switch (direction.toLowerCase()) {
            case "left" -> new Point(-size.width, originalLocation.y);
            case "right" -> new Point(parent != null ? parent.getWidth() : 0, originalLocation.y);
            case "top" -> new Point(originalLocation.x, -size.height);
            case "bottom" -> new Point(originalLocation.x, parent != null ? parent.getHeight() : 0);
            default -> originalLocation;
        };
        
        component.setLocation(startLocation);
        
        Timeline timeline = Timeline.builder(component)
            .addPropertyToInterpolate("location", startLocation, originalLocation)
            .setDuration(durationMs)
            .setEase(new Spline(0.3f))
            .build();
        
        timeline.play();
    }
    
    public static void shake(JComponent component, int intensity, int durationMs) {
        Point originalLocation = component.getLocation();
        Point shakeLocation = new Point(originalLocation.x + intensity, originalLocation.y);
        
        Timeline timeline = Timeline.builder(component)
            .addPropertyToInterpolate("location", originalLocation, shakeLocation)
            .setDuration(durationMs / 8)
            .setEase(new Spline(0.5f))
            .build();
        
        timeline.playLoop(4, Timeline.RepeatBehavior.REVERSE);
    }
}
