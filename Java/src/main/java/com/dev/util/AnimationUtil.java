package com.dev.util;

import javax.swing.*;
import java.awt.*;

public class AnimationUtil {
    
    public static void pulse(JComponent component, int durationMs) {
        Dimension originalSize = component.getSize();
        int targetWidth = (int)(originalSize.width * 1.08);
        int targetHeight = (int)(originalSize.height * 1.08);
        
        Timer timer = new Timer(20, null);
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / (durationMs / 2));
            
            if (progress < 1.0f) {
                int width = (int)(originalSize.width + (targetWidth - originalSize.width) * progress);
                int height = (int)(originalSize.height + (targetHeight - originalSize.height) * progress);
                component.setPreferredSize(new Dimension(width, height));
                component.revalidate();
            } else {
                component.setPreferredSize(originalSize);
                component.revalidate();
                timer.stop();
            }
        });
        timer.start();
    }
    
    public static void shake(JComponent component, int intensity, int durationMs) {
        Point originalLocation = component.getLocation();
        
        Timer timer = new Timer(20, null);
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed < durationMs) {
                int offset = (int)(Math.sin(elapsed / 10.0) * intensity);
                component.setLocation(originalLocation.x + offset, originalLocation.y);
            } else {
                component.setLocation(originalLocation);
                timer.stop();
            }
        });
        timer.start();
    }
}
