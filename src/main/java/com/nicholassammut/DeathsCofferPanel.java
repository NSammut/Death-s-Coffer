package com.nicholassammut;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class DeathsCofferPanel extends PluginPanel {

    private static final Color BACKGROUND_COLOR = new Color(25, 25, 25);
    private static final Color BORDER_COLOR = new Color(60, 60, 60);
    private static final Color HEADER_COLOR = new Color(220, 220, 220);
    private static final Color BODY_TEXT_COLOR = new Color(200, 200, 200);
    private static final Color ACCENT_COLOR = new Color(255, 215, 0); // Gold
    private static final Color INFO_TEXT_COLOR = new Color(180, 180, 180);
    private static final Color FOOTER_TEXT_COLOR = new Color(140, 140, 140);
    private static final Color LINK_COLOR = new Color(100, 180, 255);

    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private JLabel cofferValueLabel;

    public DeathsCofferPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel headerLabel = new JLabel("<html><div style='text-align:center;'><b>☠️ Death's Coffer ☠️</b></div></html>");
        headerLabel.setForeground(HEADER_COLOR);
        headerLabel.setFont(FONT_HEADER);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(headerLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        return panel;
    }

    private JPanel createBodyPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Coffer value display
        JPanel cofferPanel = new JPanel(new BorderLayout());
        cofferPanel.setOpaque(false);

        Border lineBorder = BorderFactory.createLineBorder(BORDER_COLOR, 1, true);
        Border emptyBorder = new EmptyBorder(8, 12, 8, 12);
        cofferPanel.setBorder(new CompoundBorder(lineBorder, emptyBorder));

        JLabel titleLabel = new JLabel("Coffer Value");
        titleLabel.setForeground(BODY_TEXT_COLOR);
        titleLabel.setFont(FONT_BODY);

        cofferValueLabel = new JLabel("Not Logged In");
        cofferValueLabel.setForeground(ACCENT_COLOR);
        cofferValueLabel.setFont(FONT_BODY);
        cofferValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        cofferPanel.add(titleLabel, BorderLayout.WEST);
        cofferPanel.add(cofferValueLabel, BorderLayout.EAST);

        JLabel infoLabel = new JLabel("<html><div style='text-align:center;'>"
                + "Type <b>!dc</b>, <b>!coffer</b>, or <b>!deathscoffer</b> in game to show off your coffer value to others!"
                + "</div><br/>" // line break
                + "If you see player not found, you need to do one of the following:"
                + "<ol style='padding-left:15px; margin-top:5px;'>"
                + "<li>Visit Death's Coffer</li>"
                + "<li>Click the Collect option on Death</li>"
                + "<li>Die and loot your items to pay for the fee</li>"
                + "</ol></html>");
        infoLabel.setForeground(INFO_TEXT_COLOR);
        infoLabel.setFont(FONT_BODY);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(cofferPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(infoLabel);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(BORDER_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel creditsLabel = new JLabel("Made by Nick");
        creditsLabel.setFont(FONT_SMALL);
        creditsLabel.setForeground(FOOTER_TEXT_COLOR);
        creditsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel donationLabel = new JLabel("<html><div style='text-align:center;'><a href=''>Buy me a coffee ☕</a></div></html>");
        donationLabel.setFont(FONT_SMALL);
        donationLabel.setForeground(LINK_COLOR);
        donationLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        donationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        donationLabel.setHorizontalAlignment(SwingConstants.CENTER);

        donationLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.paypal.com/donate/?hosted_button_id=XDH4UECGZG5PS"));
                } catch (Exception ignored) {
                    // Handle exception if needed
                }
            }
        });

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(creditsLabel);
        panel.add(donationLabel);

        return panel;
    }

    public void setCofferValue(String value) {
        SwingUtilities.invokeLater(() ->
                cofferValueLabel.setText(value)
        );
    }
}