/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.components;

import utils.UIConstants;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class RoundedButton extends JButton {
    private Color backgroundColor;
    private boolean isOutlined;

    public RoundedButton(String text, Color bgColor) {
        super(text);
        this.backgroundColor = bgColor;
        this.isOutlined = false;
        setupUI();
    }

    public RoundedButton(String text, Color bgColor, boolean outlined) {
        super(text);
        this.backgroundColor = bgColor;
        this.isOutlined = outlined;
        setupUI();
    }

    private void setupUI() {
        setFont(UIConstants.BODY_FONT);
        setForeground(isOutlined ? backgroundColor : Color.WHITE);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(new EmptyBorder(12, 24, 12, 24));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isOutlined) {
            g2.setColor(getBackground());
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConstants.CORNER_RADIUS, UIConstants.CORNER_RADIUS);
        } else {
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.CORNER_RADIUS, UIConstants.CORNER_RADIUS);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    public void addActionListener(ActionListener l) {
        super.addActionListener(l);
    }

    // Inner class for underline border (used in tabs)
    public static class UnderlineBorder extends AbstractBorder {
        private Color color;
        private int thickness;

        public UnderlineBorder(Color color, int thickness) {
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            g2.fillRect(0, height - thickness, width, thickness);
            g2.dispose();
        }
    }
}
