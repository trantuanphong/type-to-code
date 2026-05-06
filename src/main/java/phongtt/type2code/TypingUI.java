package phongtt.type2code;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class TypingUI {

    // ================= UI =================
    private JFrame frame;
    private JTextPane targetPane;
    private JTextArea inputArea;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    // ================= DATA =================
    private final List<TypingCase> cases;

    private int index = 0;
    private TypingEngine engine;

    private int lastLength = 0;

    public TypingUI(List<TypingCase> cases) {
        this.cases = cases;
        createUI();
    }

    // ================= UI SETUP =================
    private void createUI() {
        initFrame();
        initEditorAreas();
        JPanel topBar = buildTopBar();
        JPanel bottomBar = buildBottomBar();
        JSplitPane splitPane = buildSplitPane();

        Utils.disableCopyPaste(inputArea);

        attachListeners();

        frame.setLayout(new BorderLayout());
        frame.add(topBar, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(bottomBar, BorderLayout.SOUTH);

        loadTest();

        frame.setVisible(true);
    }

    private void initFrame() {
        frame = new JFrame("Java Typing Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
    }

    private void initEditorAreas() {
        targetPane = new JTextPane();
        targetPane.setEditable(false);
        targetPane.setFont(new Font("Monospaced", Font.BOLD, 22));

        inputArea = new JTextArea();
        inputArea.setFont(new Font("Monospaced", Font.BOLD, 22));
    }

    private JPanel buildTopBar() {
        JButton nextBtn = new JButton("Next");
        JButton exitBtn = new JButton("Exit");

        nextBtn.addActionListener(e -> nextTest());
        exitBtn.addActionListener(e -> System.exit(0));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JPanel right = new JPanel();
        right.add(nextBtn);
        right.add(exitBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Java Typing Engine"), BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        top.add(progressBar, BorderLayout.SOUTH);

        return top;
    }

    private JPanel buildBottomBar() {
        statusLabel = new JLabel("Start typing...");

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.SOUTH);

        return bottom;
    }

    private JSplitPane buildSplitPane() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(targetPane),
                new JScrollPane(inputArea)
        );

        split.setResizeWeight(0.5);

        return split;
    }

    private void attachListeners() {
        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    // ================= LOAD TEST =================
    private void loadTest() {
        TypingCase test = cases.get(index);

        engine = new TypingEngine(test.getContent());

        targetPane.setText(test.getContent());
        inputArea.setText("");

        progressBar.setValue(0);
        statusLabel.setText("Test: " + test.getTitle());
    }

    // ================= UPDATE (OPTIMIZED) =================
    private void update() {

        String input = Utils.normalize(inputArea.getText());
        String target = engine.getTarget();

        // incremental optimization
        if (input.length() > lastLength) {
            renderChar(input.length() - 1, input);
        } else {
            renderFull(input);
        }

        lastLength = input.length();

        updateProgress(input, target);

        if (input.equals(target)) {
            statusLabel.setText("Completed!");
            JOptionPane.showMessageDialog(null, "Completed!");
        }
    }

    // ================= FULL RENDER (SAFE) =================
    private void renderFull(String input) {

        targetPane.setText("");

        StyledDocument doc = targetPane.getStyledDocument();

        String target = engine.getTarget();

        for (int i = 0; i < target.length(); i++) {
            try {
                doc.insertString(i, String.valueOf(target.charAt(i)), styleFor(i, input, target));
            } catch (BadLocationException ex) {
            }
        }
    }

    // ================= SINGLE CHAR UPDATE =================
    private void renderChar(int i, String input) {

        StyledDocument doc = targetPane.getStyledDocument();
        String target = engine.getTarget();

        if (i >= target.length()) {
            return;
        }

        try {
            doc.remove(i, 1);
            doc.insertString(i, String.valueOf(target.charAt(i)),
                    styleFor(i, input, target));
        } catch (BadLocationException ignored) {
        }
    }

    // ================= STYLE RULE =================
    private SimpleAttributeSet styleFor(int i, String input, String target) {
        final SimpleAttributeSet attr = new SimpleAttributeSet();

        if (i < input.length()) {

            if (input.charAt(i) == target.charAt(i)) {
                StyleConstants.setForeground(attr, new Color(0, 180, 0)); // green
            } else {
                StyleConstants.setForeground(attr, Color.RED);
            }

        } else {
            StyleConstants.setForeground(attr, Color.GRAY);
        }

        return attr;
    }

    // ================= PROGRESS =================
    private void updateProgress(String input, String target) {
        final int p = (int) ((input.length() * 100.0) / target.length());
        progressBar.setValue(Math.min(p, 100));
    }

    // ================= NEXT =================
    private void nextTest() {
        index++;

        if (index >= cases.size()) {
            statusLabel.setText("Completed all tests!");
            inputArea.setEditable(false);
            return;
        }

        loadTest();
    }

}
