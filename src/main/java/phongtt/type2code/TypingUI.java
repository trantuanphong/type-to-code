package phongtt.type2code;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Class TypingUI chịu trách nhiệm: - Khởi tạo và quản lý giao diện người dùng
 * (UI) - Hiển thị nội dung cần gõ (target) - Nhận input từ người dùng - Điều
 * phối tương tác giữa UI và TypingEngine
 *
 * Lưu ý: - Class này đóng vai trò Controller + View (simple app) - Không xử lý
 * logic business phức tạp
 */
public class TypingUI {

    // ================= UI COMPONENTS =================
    /**
     * Frame chính
     */
    private JFrame mainFrame;

    /**
     * Hiển thị text cần gõ (read-only, có style)
     */
    private JTextPane targetTextPane;

    /**
     * Vùng nhập liệu của user
     */
    private JTextArea inputTextArea;

    /**
     * Hiển thị trạng thái
     */
    private JLabel statusLabel;

    /**
     * Thanh tiến độ
     */
    private JProgressBar progressBar;

    /**
     * Nút sang bài tiếp theo
     */
    private JButton nextButton;

    // ================= DATA =================
    /**
     * Danh sách bài typing
     */
    private final List<TypingCase> typingCases;

    /**
     * Index hiện tại
     */
    private int currentIndex = 0;

    /**
     * Engine xử lý typing
     */
    private TypingEngine typingEngine;

    /**
     * Độ dài input trước đó (dùng để optimize render)
     */
    private int lastInputLength = 0;

    /**
     * Constructor
     *
     * @param cases danh sách bài typing (có thể null)
     */
    public TypingUI(final List<TypingCase> cases) {
        this.typingCases = cases != null ? cases : Collections.emptyList();
        initializeUI();
    }

    // ================= INITIALIZATION =================
    /**
     * Khởi tạo toàn bộ giao diện người dùng (UI) cho ứng dụng typing
     *
     * Luồng xử lý: 1. Khởi tạo frame chính 2. Khởi tạo các component nhập/xuất
     * text 3. Tạo các khu vực UI (top bar, bottom bar, split pane) 4. Gắn
     * behavior (disable copy/paste, listener) 5. Set layout tổng thể 6. Load
     * bài typing đầu tiên 7. Hiển thị UI
     *
     * Không có input/output. Side-effect: thay đổi trạng thái UI và hiển thị
     * cửa sổ
     */
    private void initializeUI() {
        // Khởi tạo frame chính (cửa sổ ứng dụng)
        initMainFrame();

        // Khởi tạo các vùng text (input + target)
        initEditorComponents();

        // Tạo các thành phần UI chính
        final JPanel topBar = buildTopBar();       // Thanh trên (title + button + progress)
        final JPanel bottomBar = buildBottomBar(); // Thanh dưới (status)
        final JSplitPane splitPane = buildSplitPane(); // Chia đôi màn hình (target | input)

        // Disable copy/paste để tránh gian lận khi typing
        TextUtils.disableCopyPaste(inputTextArea);

        // Gắn listener để theo dõi input user
        attachInputListener();

        // Thiết lập layout tổng thể cho frame
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(topBar, BorderLayout.NORTH);
        mainFrame.add(splitPane, BorderLayout.CENTER);
        mainFrame.add(bottomBar, BorderLayout.SOUTH);

        // Load bài typing đầu tiên
        loadCurrentTest();

        // Hiển thị UI
        mainFrame.setVisible(true);
    }

    /**
     * Khởi tạo frame chính của ứng dụng
     *
     * Cấu hình: - Title: "Java Typing Engine" - Đóng app khi đóng cửa sổ - Full
     * màn hình - Không có viền window (undecorated)
     */
    private void initMainFrame() {
        mainFrame = new JFrame("Java Typing Engine");

        // Khi đóng cửa sổ → thoát ứng dụng
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Mở full màn hình
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Bỏ thanh title bar của OS
        mainFrame.setUndecorated(true);
    }

    /**
     * Khởi tạo các component nhập và hiển thị text
     *
     * Bao gồm: - targetTextPane: hiển thị nội dung cần gõ (read-only) -
     * inputTextArea: nơi user nhập liệu
     *
     * Sử dụng font Monospaced để: - Căn ký tự chính xác - Dễ so sánh từng ký tự
     */
    private void initEditorComponents() {
        final Font font = new Font("Monospaced", Font.BOLD, 22);

        // Vùng hiển thị text mẫu (không cho chỉnh sửa)
        targetTextPane = new JTextPane();
        targetTextPane.setEditable(false);
        targetTextPane.setFont(font);

        // Vùng user nhập text
        inputTextArea = new JTextArea();
        inputTextArea.setFont(font);
    }

    /**
     * Tạo thanh trên (Top Bar)
     *
     * Bao gồm: - Nút "Next": chuyển sang bài tiếp theo - Nút "Exit": thoát ứng
     * dụng - ProgressBar: hiển thị % hoàn thành
     *
     * @return JPanel đại diện cho top bar
     */
    private JPanel buildTopBar() {
        // Nút sang bài tiếp theo (ban đầu disable)
        nextButton = new JButton("Next");
        nextButton.setEnabled(false);

        // Nút thoát ứng dụng
        final JButton exitButton = new JButton("Exit");

        // Gắn sự kiện click
        nextButton.addActionListener(e -> goToNextTest());
        exitButton.addActionListener(e -> System.exit(0));

        // Thanh tiến độ (0 → 100%)
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); // Hiển thị text %

        // Panel bên phải chứa button
        final JPanel rightPanel = new JPanel();
        rightPanel.add(nextButton);
        rightPanel.add(exitButton);

        // Panel chính
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Java Typing Engine"), BorderLayout.WEST); // Title
        panel.add(rightPanel, BorderLayout.EAST);                        // Button
        panel.add(progressBar, BorderLayout.SOUTH);                      // Progress

        return panel;
    }

    /**
     * Tạo thanh dưới (Bottom Bar)
     *
     * Chức năng: - Hiển thị trạng thái hiện tại (status)
     *
     * @return JPanel bottom bar
     */
    private JPanel buildBottomBar() {
        // Trạng thái mặc định
        statusLabel = new JLabel("Start typing...");

        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Tạo vùng chia đôi màn hình (Split Pane)
     *
     * Bên trái: - targetTextPane (text mẫu)
     *
     * Bên phải: - inputTextArea (user nhập)
     *
     * @return JSplitPane
     */
    private JSplitPane buildSplitPane() {
        final JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(targetTextPane),
                new JScrollPane(inputTextArea)
        );

        // Chia đều 2 bên (50/50)
        splitPane.setResizeWeight(0.5);

        return splitPane;
    }

    /**
     * Gắn listener theo dõi thay đổi input của user
     *
     * Cơ chế: - Mỗi khi user gõ hoặc xóa → DocumentListener trigger - Gọi
     * updateTypingState() để: + So sánh input vs target + Update màu chữ +
     * Update progress
     *
     * Lưu ý: - changedUpdate() không dùng cho plain text
     */
    private void attachInputListener() {
        inputTextArea.getDocument().addDocumentListener(new DocumentListener() {

            /**
             * Trigger khi user thêm ký tự
             */
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTypingState();
            }

            /**
             * Trigger khi user xóa ký tự
             */
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTypingState();
            }

            /**
             * Không dùng trong trường hợp text thường
             */
            @Override
            public void changedUpdate(DocumentEvent e) {
                // No-op
            }
        });
    }

    // ================= LOAD TEST =================
    /**
     * Load bài typing hiện tại lên giao diện
     *
     * Mục đích: - Lấy dữ liệu từ danh sách typingCases theo currentIndex - Khởi
     * tạo TypingEngine với nội dung target - Reset toàn bộ UI về trạng thái ban
     * đầu của bài test
     *
     * Luồng xử lý: 1. Validate dữ liệu (list rỗng / index out of bounds) 2. Lấy
     * TypingCase hiện tại 3. Khởi tạo engine với nội dung cần gõ 4. Reset UI
     * (text, progress, trạng thái)
     *
     * Không có input/output (side-effect: cập nhật UI và state nội bộ)
     */
    private void loadCurrentTest() {

        // Kiểm tra dữ liệu hợp lệ:
        // - Nếu danh sách rỗng
        // - Hoặc index vượt quá size
        if (typingCases.isEmpty() || currentIndex >= typingCases.size()) {
            statusLabel.setText("No typing data available");
            return;
        }

        // Lấy bài test hiện tại
        final TypingCase currentCase = typingCases.get(currentIndex);

        // Kiểm tra null để tránh lỗi runtime
        if (currentCase == null) {
            statusLabel.setText("Invalid typing case");
            return;
        }

        // Lấy nội dung target (null-safe)
        final String target = safeString(currentCase.getContent());

        // Khởi tạo engine để xử lý logic typing
        typingEngine = new TypingEngine(target);

        // Hiển thị nội dung target lên UI
        targetTextPane.setText(target);

        // Reset vùng nhập của user
        inputTextArea.setText("");

        // Reset progress về 0%
        progressBar.setValue(0);

        // Hiển thị tên bài test
        statusLabel.setText("Test: " + safeString(currentCase.getTitle()));

        // Reset biến phục vụ optimize render
        lastInputLength = 0;
    }

// ================= UPDATE =================
    /**
     * Xử lý mỗi khi user thay đổi input (gõ hoặc xóa)
     *
     * Mục đích: - Chuẩn hóa input - So sánh với target - Render highlight
     * đúng/sai - Cập nhật progress - Kiểm tra hoàn thành
     *
     * Tối ưu: - Nếu user chỉ thêm ký tự → chỉ render 1 ký tự (nhanh hơn) - Nếu
     * user xóa / sửa → render lại toàn bộ (an toàn hơn)
     */
    private void updateTypingState() {

        // Nếu engine chưa được khởi tạo → không xử lý
        if (typingEngine == null) {
            return;
        }

        // Chuẩn hóa input (loại bỏ CRLF, tab,...)
        final String input = TextUtils.normalizeText(inputTextArea.getText());

        // Lấy target từ engine (null-safe)
        final String target = safeString(typingEngine.getTarget());

        // ================= OPTIMIZATION =================
        // Nếu độ dài tăng → user đang gõ thêm
        if (input.length() > lastInputLength) {
            // Chỉ update ký tự cuối → tăng performance
            renderSingleChar(input.length() - 1, input, target);
        } else {
            // Nếu xóa / sửa → render lại toàn bộ để đảm bảo đúng
            renderFullText(input, target);
        }

        // Cập nhật lại độ dài input trước đó
        lastInputLength = input.length();

        // Cập nhật progress
        updateProgress(input, target);

        // Kiểm tra hoàn thành
        checkCompletion(input, target);
    }

// ================= RENDER =================
    /**
     * Render lại toàn bộ text với style (safe nhưng tốn tài nguyên hơn)
     *
     * @param input nội dung user đã nhập
     * @param target nội dung chuẩn cần gõ
     *
     * Cách hoạt động: - Clear toàn bộ text hiện tại - Insert lại từng ký tự với
     * style tương ứng (đúng/sai/chưa gõ)
     *
     * Ưu điểm: - Chính xác tuyệt đối
     *
     * Nhược điểm: - Chậm hơn khi text dài
     */
    private void renderFullText(final String input, final String target) {
        // Xóa toàn bộ nội dung cũ
        targetTextPane.setText("");

        final StyledDocument doc = targetTextPane.getStyledDocument();

        // Duyệt từng ký tự trong target
        for (int i = 0; i < target.length(); i++) {
            try {
                doc.insertString(
                        i,
                        String.valueOf(target.charAt(i)),
                        buildStyle(i, input, target) // style theo trạng thái
                );
            } catch (BadLocationException ex) {
                // Không nuốt lỗi → log để debug
                System.err.println("Render error: " + ex.getMessage());
            }
        }
    }

    /**
     * Render 1 ký tự duy nhất (tối ưu hiệu năng)
     *
     * @param index vị trí ký tự cần update
     * @param input nội dung user
     * @param target nội dung chuẩn
     *
     * Cách hoạt động: - Xóa ký tự tại index - Insert lại với style mới
     *
     * Dùng khi: - User chỉ gõ thêm 1 ký tự
     */
    private void renderSingleChar(final int index, final String input, final String target) {
        // Validate index tránh lỗi
        if (index < 0 || index >= target.length()) {
            return;
        }

        final StyledDocument doc = targetTextPane.getStyledDocument();

        try {
            // Xóa ký tự cũ
            doc.remove(index, 1);

            // Insert ký tự mới với style
            doc.insertString(
                    index,
                    String.valueOf(target.charAt(index)),
                    buildStyle(index, input, target)
            );

        } catch (BadLocationException ex) {
            System.err.println("Render char error: " + ex.getMessage());
        }
    }

    /**
     * Xây dựng style cho từng ký tự dựa trên trạng thái typing
     *
     * Quy tắc: - Đúng → màu xanh - Sai → màu đỏ - Chưa gõ → màu xám
     *
     * @param index vị trí ký tự
     * @param input nội dung user
     * @param target nội dung chuẩn
     * @return style tương ứng
     */
    private SimpleAttributeSet buildStyle(final int index, final String input, final String target) {
        final SimpleAttributeSet style = new SimpleAttributeSet();

        if (index < input.length()) {
            // User đã gõ đến vị trí này → kiểm tra đúng/sai

            if (input.charAt(index) == target.charAt(index)) {
                // Gõ đúng → xanh
                StyleConstants.setForeground(style, new Color(0, 180, 0));
            } else {
                // Gõ sai → đỏ
                StyleConstants.setForeground(style, Color.RED);
            }

        } else {
            // Chưa gõ → màu xám
            StyleConstants.setForeground(style, Color.GRAY);
        }

        return style;
    }

    // ================= PROGRESS =================
    /**
     * Cập nhật thanh tiến độ (% hoàn thành)
     *
     * Công thức: progress = (độ dài input / độ dài target) * 100
     *
     * @param input nội dung user
     * @param target nội dung chuẩn
     */
    private void updateProgress(final String input, final String target) {
        // Tránh chia cho 0
        if (target.isEmpty()) {
            progressBar.setValue(0);
            return;
        }

        final int percent = (int) ((input.length() * 100.0) / target.length());

        // Đảm bảo không vượt quá 100%
        progressBar.setValue(Math.min(percent, 100));
    }

    // ================= COMPLETION =================
    /**
     * Kiểm tra user đã hoàn thành bài typing chưa
     *
     * Điều kiện: - Input phải giống hoàn toàn target
     *
     * Hành động khi hoàn thành: - Hiển thị trạng thái - Enable nút Next - Hiển
     * thị popup thông báo
     */
    private void checkCompletion(final String input, final String target) {
        if (input.equals(target)) {
            statusLabel.setText("Completed!");

            // Cho phép chuyển bài tiếp theo
            nextButton.setEnabled(true);

            // Hiển thị popup (blocking UI)
            JOptionPane.showMessageDialog(mainFrame, "Completed!");
        }
    }

    // ================= NEXT =================
    /**
     * Chuyển sang bài typing tiếp theo
     *
     * Luồng xử lý: 1. Tăng index 2. Nếu hết danh sách: - Disable input - Hiển
     * thị trạng thái hoàn thành 3. Nếu còn: - Load bài mới - Disable nút Next
     * (chờ hoàn thành)
     */
    private void goToNextTest() {
        currentIndex++;

        // Nếu đã hoàn thành tất cả bài test
        if (currentIndex >= typingCases.size()) {
            statusLabel.setText("Completed all tests!");

            // Không cho nhập nữa
            inputTextArea.setEditable(false);
            return;
        }

        // Load bài tiếp theo
        loadCurrentTest();

        // Disable nút Next cho đến khi hoàn thành
        nextButton.setEnabled(false);
    }

    // ================= HELPER =================
    /**
     * Chuyển giá trị null → chuỗi rỗng ("")
     *
     * Mục đích: - Tránh NullPointerException - Đảm bảo UI luôn nhận String hợp
     * lệ
     *
     * @param value giá trị đầu vào (có thể null)
     * @return value nếu != null, ngược lại trả về ""
     */
    private String safeString(final String value) {
        return value != null ? value : "";
    }
}
