/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package views.components;

import utils.UIConstants;

import javax.swing.*;
import java.awt.*;

public class ShadowPanel extends JPanel {
    public ShadowPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Shadow
        g2.setColor(UIConstants.SHADOW_COLOR);
        g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, UIConstants.CORNER_RADIUS, UIConstants.CORNER_RADIUS);
        // Background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.CORNER_RADIUS, UIConstants.CORNER_RADIUS);
        g2.dispose();
        super.paintComponent(g);
    }
}