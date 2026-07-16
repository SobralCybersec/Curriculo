package com.dev.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimationUtil {

    public static void pulse(JComponent component, int duration) {
        final int originalW = component.getWidth();
        final int originalH = component.getHeight();
        final int delta = 10;
        final int steps = 20;
        final int delay = duration / steps;
        final Timer timer = new Timer(delay, null);
        final int[] count = {0};
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                count[0]++;
                if (count[0] >= steps) {
                    component.setSize(originalW, originalH);
                    timer.stop();
                    return;
                }
                double phase = Math.sin(Math.PI * count[0] / steps); // 0 -> 1 -> 0
                int dw = (int) (delta * phase);
                int dh = (int) (delta * phase);
                component.setSize(originalW + dw, originalH + dh);
            }
        });
        timer.start();
    }

    public static void shake(JComponent component, int intensity, int duration) {
        final int originalX = component.getX();
        final int originalY = component.getY();
        final Timer timer = new Timer(50, null);
        final int[] count = {0};
        final int maxShakes = duration / 50;
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count[0] >= maxShakes) {
                    component.setLocation(originalX, originalY);
                    timer.stop();
                    return;
                }
                int offsetX = (int) (Math.random() * intensity * 2) - intensity;
                int offsetY = (int) (Math.random() * intensity * 2) - intensity;
                component.setLocation(originalX + offsetX, originalY + offsetY);
                count[0]++;
            }
        });
        timer.start();
    }
}
