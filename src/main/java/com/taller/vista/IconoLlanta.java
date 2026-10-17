package com.taller.vista;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class IconoLlanta extends JComponent {

    public IconoLlanta() {
        setPreferredSize(new Dimension(160, 160));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int diametro = Math.min(getWidth(), getHeight()) - 10;
        int x = (getWidth() - diametro) / 2;
        int y = (getHeight() - diametro) / 2;
        int centroX = x + diametro / 2;
        int centroY = y + diametro / 2;

        g2.setColor(new Color(35, 35, 35));
        g2.fillOval(x, y, diametro, diametro);

        int rin = diametro * 6 / 10;
        g2.setColor(new Color(170, 175, 180));
        g2.fillOval(centroX - rin / 2, centroY - rin / 2, rin, rin);

        int centro = diametro / 4;
        g2.setColor(new Color(90, 95, 100));
        g2.fillOval(centroX - centro / 2, centroY - centro / 2, centro, centro);

        int tuerca = diametro / 12;
        g2.setColor(new Color(235, 235, 235));
        for (int i = 0; i < 5; i++) {
            double angulo = i * 2 * Math.PI / 5 - Math.PI / 2;
            int tx = centroX + (int) (Math.cos(angulo) * diametro * 0.2) - tuerca / 2;
            int ty = centroY + (int) (Math.sin(angulo) * diametro * 0.2) - tuerca / 2;
            g2.fillOval(tx, ty, tuerca, tuerca);
        }
    }
}
