package com.nicholassammut;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class DeathsCofferPanel extends PluginPanel {

    private final JLabel cofferValueLabel;

    // Constructor to build the panel
    public DeathsCofferPanel(DeathsCofferService dcService) {
        // Main panel setup
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 25));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // === MAIN CONTENT ===
        // This panel will hold all the content, from header to info label
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Header Label
        JLabel headerLabel = new JLabel("<html><div style='text-align:center;'><b>☠️ Death's Coffer ☠️</b></div></html>");
        headerLabel.setForeground(new Color(220, 220, 220));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(headerLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Coffer Value Panel
        JPanel cofferPanel = new JPanel(new BorderLayout());
        cofferPanel.setOpaque(false);
        cofferPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel titleLabel = new JLabel("Coffer Value");
        titleLabel.setForeground(new Color(200, 200, 200));

        cofferValueLabel = new JLabel("-1");
        cofferValueLabel.setForeground(new Color(255, 215, 0));
        cofferValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        cofferPanel.add(titleLabel, BorderLayout.WEST);
        cofferPanel.add(cofferValueLabel, BorderLayout.EAST);

        // Add the coffer panel to the main content panel
        contentPanel.add(cofferPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Info label under the Coffer Value
        JLabel infoLabel = new JLabel("<html><div style='text-align:center;'>Type <b>!dc</b> or <b>!deathscoffer</b> in game to show off your coffer value to others!</div></html>");
        infoLabel.setForeground(new Color(180, 180, 180));
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add a small spacing and then the label
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        contentPanel.add(infoLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // This is the main change: add the content panel to the NORTH of a new container panel.
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(contentPanel, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);

        // === FOOTER ===
        // ... (The rest of your footer code goes here, it's already correct) ...

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(60, 60, 60));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        footerPanel.add(separator);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel creditsLabel = new JLabel("Made by Nick");
        creditsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        creditsLabel.setForeground(new Color(140, 140, 140));
        creditsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel donationLabel = new JLabel("<html><div style='text-align:center;'><a href=''>Buy me a coffee ☕</a></div></html>");
        donationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        donationLabel.setForeground(new Color(100, 180, 255));
        donationLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        donationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        donationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        donationLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.paypal.com/donate/?hosted_button_id=XDH4UECGZG5PS"));
                } catch (Exception ignored) {
                }
            }
        });

        footerPanel.add(creditsLabel);
        footerPanel.add(donationLabel);

        add(footerPanel, BorderLayout.SOUTH);
    }

    public void setCofferValue(long value) {
        SwingUtilities.invokeLater(() -> cofferValueLabel.setText(String.format("%,d gp", value)));

    }
}