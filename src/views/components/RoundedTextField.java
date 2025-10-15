/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package views.components;

import utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusListener;

public class RoundedTextField extends JTextField {
    private boolean hasFocus;

    public RoundedTextField(int columns) {
        super(columns);
        this.hasFocus = false;
        setupUI();
    }

    private void setupUI() {
        setFont(UIConstants.BODY_FONT);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setCaretColor(UIConstants.PRIMARY_BLUE);
        addFocusListener(new FocusListener() {
            public void focusGained(java.awt.event.FocusEvent e) { hasFocus = true; repaint(); }
            public void focusLost(java.awt.event.FocusEvent e) { hasFocus = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.CORNER_RADIUS, UIConstants.CORNER_RADIUS);
        g2.setColor(hasFocus ? UIConstants.PRIMARY_BLUE : Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConstants.CORNER_RADIUS, UIConstants.CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }

    public void addFocusListener(FocusListener l) {
        super.addFocusListener(l);
    }
}