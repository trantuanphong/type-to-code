package phongtt.type2code;

/**
 * TypingEngine chịu trách nhiệm xử lý logic liên quan đến nội dung cần gõ.
 *
 * Vai trò chính:
 * - Chuẩn hóa (normalize) dữ liệu đầu vào
 * - Cung cấp nội dung chuẩn để so sánh với input của người dùng
 *
 * Lưu ý:
 * - Không xử lý UI
 * - Không xử lý highlight
 * - Chỉ tập trung vào dữ liệu (single responsibility)
 */
public class TypingEngine {

    /**
     * Nội dung chuẩn (sau khi đã normalize)
     * Đây là chuỗi dùng để so sánh với input người dùng
     */
    private final String target;

    /**
     * Constructor
     *
     * @param rawText nội dung gốc (có thể chứa \r\n, tab, format khác nhau)
     */
    public TypingEngine(final String rawText) {
        this.target = normalize(rawText);
    }

    /**
     * Trả về nội dung đã được chuẩn hóa
     */
    public String getTarget() {
        return target;
    }

    /**
     * Chuẩn hóa chuỗi đầu vào để đảm bảo việc so sánh chính xác
     *
     * Các xử lý:
     * - Đồng nhất xuống dòng: \r\n → \n
     * - Thay tab bằng 4 spaces (tránh lệch format khi gõ)
     *
     * @param s chuỗi đầu vào
     * @return chuỗi đã chuẩn hóa
     */
    private static String normalize(final String s) {
        if (s == null) return "";

        return s
                .replace("\r\n", "\n")   // Windows → Unix newline
                .replace("\t", "    "); // tab → 4 spaces
    }
}
