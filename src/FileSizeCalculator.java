import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;

public class FileSizeCalculator extends JFrame implements ActionListener {
    private JButton chooseButton;
    private JLabel fileNameLabel;
    private JPanel statsPanel;
    private File currentFile;

    // Apple-inspired color scheme
    private final Color PRIMARY_COLOR = new Color(0, 122, 255); // Vibrant blue
    private final Color SECONDARY_COLOR = new Color(28, 146, 255); // Lighter blue for hover
    private final Color BACKGROUND_COLOR = new Color(242, 242, 247); // Light gray background
    private static final Color CARD_COLOR = new Color(255, 255, 255, 230); // Semi-transparent white
    private final Color TEXT_PRIMARY = new Color(0, 0, 0, 230); // Dark text
    private final Color TEXT_SECONDARY = new Color(60, 60, 67, 140); // Muted text

    public FileSizeCalculator() {
        setTitle("File Analyzer");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with solid background and subtle shadow
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();

        // Upload Area
        JPanel uploadPanel = createUploadPanel();

        // Stats Panel
        statsPanel = createStatsPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(uploadPanel, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("File Analyzer");
        titleLabel.setFont(new Font("San Francisco", Font.BOLD, 28)); // Approximating SF Pro
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Inspect your files with detailed insights");
        subtitleLabel.setFont(new Font("San Francisco", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createUploadPanel() {
        JPanel uploadPanel = new JPanel(new BorderLayout(15, 15));
        uploadPanel.setOpaque(false);
        uploadPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Upload card with shadow
        JPanel uploadCard = new JPanel();
        uploadCard.setLayout(new BoxLayout(uploadCard, BoxLayout.Y_AXIS));
        uploadCard.setBackground(CARD_COLOR);
        uploadCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(0, 0, 0, 20), 1, true),
                        BorderFactory.createEmptyBorder(30, 20, 30, 20)
                )
        ));
        uploadCard.setBorder(new ShadowBorder()); // Custom shadow border

        // Icon
        JLabel iconLabel = new JLabel("📁");
        iconLabel.setFont(new Font("San Francisco", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Choose button
        chooseButton = new JButton("Select File");
        chooseButton.setFont(new Font("San Francisco", Font.BOLD, 15));
        chooseButton.setForeground(Color.WHITE);
        chooseButton.setBackground(PRIMARY_COLOR);
        chooseButton.setFocusPainted(false);
        chooseButton.setBorderPainted(false);
        chooseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chooseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        chooseButton.setMaximumSize(new Dimension(180, 38));
        chooseButton.setOpaque(true);
        chooseButton.addActionListener(this);

        // Hover effect
        chooseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                chooseButton.setBackground(SECONDARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                chooseButton.setBackground(PRIMARY_COLOR);
            }
        });

        // File name label
        fileNameLabel = new JLabel("No file selected");
        fileNameLabel.setFont(new Font("San Francisco", Font.PLAIN, 13));
        fileNameLabel.setForeground(TEXT_SECONDARY);
        fileNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        uploadCard.add(iconLabel);
        uploadCard.add(Box.createRigidArea(new Dimension(0, 15)));
        uploadCard.add(chooseButton);
        uploadCard.add(Box.createRigidArea(new Dimension(0, 10)));
        uploadCard.add(fileNameLabel);

        uploadPanel.add(uploadCard, BorderLayout.CENTER);

        return uploadPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 3, 10, 10));
        statsPanel.setOpaque(false);
        statsPanel.setVisible(false);

        return statsPanel;
    }

    private JPanel createStatCard(String icon, String label, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(new ShadowBorder());

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("San Francisco", Font.PLAIN, 24));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("San Francisco", Font.PLAIN, 12));
        labelText.setForeground(TEXT_SECONDARY);
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("San Francisco", Font.BOLD, 18));
        valueText.setForeground(TEXT_PRIMARY);
        valueText.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(labelText);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(valueText);

        return card;
    }

    // Custom shadow border for Apple-style cards
    private static class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRoundRect(x, y, width - 1, height - 1, 12, 12);
            g2d.setColor(CARD_COLOR);
            g2d.fillRoundRect(x + 1, y + 1, width - 3, height - 3, 10, 10);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 6, 6, 6);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to analyze");
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            fileNameLabel.setText("📄 " + currentFile.getName());
            fileNameLabel.setForeground(PRIMARY_COLOR);
            fileNameLabel.setFont(new Font("San Francisco", Font.BOLD, 14));
            analyzeFile(currentFile);
        }
    }

    private void analyzeFile(File file) {
        try {
            // File size
            long fileSizeBytes = Files.size(file.toPath());
            double fileSizeKB = fileSizeBytes / 1024.0;
            double fileSizeMB = fileSizeKB / 1024.0;

            String sizeDisplay = fileSizeMB >= 1 ?
                    String.format("%.2f MB", fileSizeMB) :
                    String.format("%.2f KB", fileSizeKB);

            // Read file contents
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append(" ");
                lineCount++;
            }
            reader.close();

            String content = contentBuilder.toString().trim();

            // Word and character count
            String[] words = content.isEmpty() ? new String[0] : content.split("\\s+");
            int wordCount = words.length;
            int charCount = content.replaceAll("\\s+", "").length();

            // Find longest word
            String longestWord = "";
            for (String w : words) {
                if (w.length() > longestWord.length()) {
                    longestWord = w;
                }
            }

            // Average word length
            double avgWordLength = (wordCount > 0) ? (double) charCount / wordCount : 0;

            // Update stats panel
            statsPanel.removeAll();
            statsPanel.add(createStatCard("💾", "File Size", sizeDisplay));
            statsPanel.add(createStatCard("📝", "Words", String.format("%,d", wordCount)));
            statsPanel.add(createStatCard("🔤", "Characters", String.format("%,d", charCount)));
            statsPanel.add(createStatCard("📏", "Avg Word Length", String.format("%.1f", avgWordLength)));
            statsPanel.add(createStatCard("📋", "Lines", String.format("%,d", lineCount)));
//            statsPanel.add(createStatCard("⭐", "Longest Word", longestWord.isEmpty() ? "N/A" :
//                    (longestWord.length() > 12 ? longestWord.substring(0, 12) + "..." : longestWord)));

            statsPanel.setVisible(true);
            statsPanel.revalidate();
            statsPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new FileSizeCalculator().setVisible(true);
        });
    }
}