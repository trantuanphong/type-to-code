package phongtt.type2code;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

/**
 * Lớp tiện ích (utility class) cung cấp các hàm hỗ trợ xử lý chuỗi
 * và thao tác với UI component.
 * 
 * Lưu ý:
 * - Đây là class stateless (không có trạng thái)
 * - Không nên khởi tạo instance → sử dụng static methods
 */
public final class TextUtils {

    /**
     * Chuẩn hóa chuỗi đầu vào:
     * - Chuyển CRLF (\r\n) → LF (\n)
     * - Thay tab (\t) → 4 dấu space
     * 
     * @param input Chuỗi đầu vào (có thể null)
     * @return Chuỗi đã chuẩn hóa, không bao giờ null
     */
    public static String normalizeText(String input) {
        // Xử lý null an toàn
        if (input == null) {
            return "";
        }

        return input
                .replace("\r\n", "\n")  // Chuẩn hóa xuống dòng
                .replace("\t", "    "); // Thay tab bằng 4 spaces
    }

    /**
     * Vô hiệu hóa các thao tác copy/paste/cut trên JTextArea
     * 
     * @param textArea JTextArea cần disable (có thể null)
     */
    public static void disableCopyPaste(JTextArea textArea) {
        // Null check để tránh NullPointerException
        if (textArea == null) {
            return;
        }

        // Lấy InputMap và ActionMap của component
        InputMap inputMap = textArea.getInputMap();
        ActionMap actionMap = textArea.getActionMap();

        // Tên action dùng chung để disable
        final String DISABLED_ACTION_KEY = "disabled-action";

        // Map các phím tắt Ctrl+C, Ctrl+V, Ctrl+X → action rỗng
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), DISABLED_ACTION_KEY);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), DISABLED_ACTION_KEY);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), DISABLED_ACTION_KEY);

        // Gán action rỗng (không làm gì)
        actionMap.put(DISABLED_ACTION_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // Intentionally empty: chặn hành động
            }
        });

        // Disable paste từ drag & drop hoặc clipboard
        textArea.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return false; // Không cho import dữ liệu
            }
        });

        // Disable menu chuột phải (context menu)
        textArea.setComponentPopupMenu(null);
    }
}
