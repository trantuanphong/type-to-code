package phongtt.type2code;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainUI {

    private JFrame frame;
    private JTextField urlField;
    private JLabel statusLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainUI().createUI());
    }

    private void createUI() {
        frame = new JFrame("Type2Code - Loader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 320);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // ================= ROOT PANEL =================
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // ================= TITLE =================
        JLabel title = new JLabel("Type2Code");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter JSON test URL to start typing session");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // spacing
        root.add(title);
        root.add(Box.createVerticalStrut(5));
        root.add(subtitle);
        root.add(Box.createVerticalStrut(25));

        // ================= INPUT =================
        urlField = new JTextField();
        urlField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        urlField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        root.add(urlField);
        root.add(Box.createVerticalStrut(15));

        // ================= BUTTON =================
        JButton loadBtn = new JButton("Load & Start");
        loadBtn.setFocusPainted(false);
        loadBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadBtn.setMaximumSize(new Dimension(200, 40));

        loadBtn.addActionListener(e -> loadTests());

        root.add(loadBtn);
        root.add(Box.createVerticalStrut(20));

        // ================= STATUS =================
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(120, 120, 120));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(statusLabel);

        // ================= STYLE FRAME =================
        frame.setContentPane(root);
        frame.getContentPane().setBackground(Color.WHITE);

        frame.setVisible(true);
    }

    private void loadTests() {
        String url = urlField.getText().trim();

        if (url.isEmpty()) {
            statusLabel.setText("URL cannot be empty");
            return;
        }

        statusLabel.setText("Loading...");
        new Thread(() -> {

            try {
                List<TypingCase> cases = TypingCaseLoader.loadFromUrl(url);

                if (cases == null || cases.isEmpty()) {
                    throw new RuntimeException("No tests found");
                }

                SwingUtilities.invokeLater(() -> {
                    frame.dispose();
                    new TypingUI(cases); // OPEN typing UI
                });

            } catch (RuntimeException e) {

                SwingUtilities.invokeLater(()
                        -> statusLabel.setText("Failed: " + e.getMessage())
                );
            }

        }).start();
    }
}
