package views;

import controllers.WelcomeController;
import utils.UIConstants;
import views.components.RoundedButton;
import views.components.RoundedLabel;
import views.components.ShadowPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WelcomeView extends JFrame {
    private WelcomeController controller;
    private JPanel topBar;
    private JLabel bagIcon;
    private RoundedLabel titleLabel;
    private JLabel subtitleLabel;
    private RoundedButton getStartedButton;
    private RoundedButton haveAccountButton;
    private JPanel featuresPanel;

    public WelcomeView() {
        controller = new WelcomeController(this);
        initializeUI();
        setTitle("PackPal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(375, 667));
        setMinimumSize(new Dimension(350, 600));
        setResizable(true);

        // Top Bar
        topBar = createTopBar("PackPal", false);
        add(topBar, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Pink Bag Icon
        bagIcon = new JLabel();
        bagIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        bagIcon.setHorizontalAlignment(SwingConstants.CENTER);
        bagIcon.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/assets/bag.JPEG"));
            if (icon.getImage() != null) {
                Image pinkImage = tintImage(icon.getImage(), UIConstants.PINK_ACCENT);
                Image scaled = pinkImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                bagIcon.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception e) {
            bagIcon.setText("👜");
            bagIcon.setFont(new Font("Arial", Font.PLAIN, 80));
            bagIcon.setForeground(UIConstants.PINK_ACCENT);
        }
        mainPanel.add(bagIcon);

        // Title & Subtitle
        titleLabel = new RoundedLabel("Welcome to PackPal");
        titleLabel.setFont(UIConstants.TITLE_FONT.deriveFont(24f));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        subtitleLabel = new JLabel("Never forget essential items again! Create smart packing lists for any trip.");
        subtitleLabel.setFont(UIConstants.BODY_FONT);
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(subtitleLabel);

        // Buttons
        getStartedButton = new RoundedButton("Get Started", UIConstants.PRIMARY_BLUE);
        getStartedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getStartedButton.setPreferredSize(new Dimension(300, 50));
        getStartedButton.addActionListener(e -> controller.handleGetStarted());
        mainPanel.add(getStartedButton);

        haveAccountButton = new RoundedButton("I have an account", UIConstants.PRIMARY_BLUE, true);
        haveAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        haveAccountButton.setPreferredSize(new Dimension(300, 50));
        haveAccountButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        haveAccountButton.addActionListener(e -> controller.handleLogin());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(haveAccountButton);

        // Feature Highlights
        featuresPanel = createFeaturesPanel();
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(featuresPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTopBar(String title, boolean showBack) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.PRIMARY_BLUE);
        bar.setPreferredSize(new Dimension(375, 60));
        RoundedLabel appTitle = new RoundedLabel(title);
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(UIConstants.TITLE_FONT.deriveFont(18f));
        bar.add(appTitle, BorderLayout.CENTER);
        if (showBack) {
            JButton back = new JButton("← Back");
            back.setForeground(Color.WHITE);
            back.setContentAreaFilled(false);
            back.setBorderPainted(false);
            back.setFocusPainted(false);
            back.addActionListener(e -> {
                new WelcomeView();
                dispose();
            });
            bar.add(back, BorderLayout.WEST);
        }
        return bar;
    }

    private JPanel createFeaturesPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        String[] features = {
            "• Smart packing lists tailored to your trip",
            "• Weather-based suggestions for essentials",
            "• Easy sharing and exporting options"
        };
        for (String feature : features) {
            ShadowPanel card = new ShadowPanel(new BorderLayout());
            card.setPreferredSize(new Dimension(300, 60));
            JLabel label = new JLabel(feature);
            label.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            label.setFont(UIConstants.BODY_FONT);
            card.add(label, BorderLayout.CENTER);
            panel.add(card);
        }
        return panel;
    }

    private Image tintImage(Image image, Color tint) {
        if (image == null) return null;
        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.setColor(tint);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.dispose();
        return bi;
    }

    // Getters
    public void showBackButton(boolean show) {
        Component[] components = topBar.getComponents();
        if (components.length > 1) {
            components[0].setVisible(show);
        }
    }
}

