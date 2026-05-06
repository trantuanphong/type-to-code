package phongtt.type2code;

/**
 * Class TypingCase đại diện cho một đối tượng chứa thông tin văn bản,
 * bao gồm tiêu đề (title) và nội dung (content).
 * 
 * Mục đích:
 * - Lưu trữ dữ liệu đơn giản
 * - Có thể mở rộng thêm các thuộc tính khác trong tương lai
 */
public class TypingCase {

    /**
     * Tiêu đề
     */
    private String title;

    /**
     * Nội dung chi tiết
     */
    private String content;

    /**
     * Lấy tiêu đề
     * 
     * @return tiêu đề
     */
    public String getTitle() {
        return title;
    }

    /**
     * Cập nhật tiêu đề
     * 
     * @param title tiêu đề mới (có thể null)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Lấy nội dung
     * 
     * @return nội dung
     */
    public String getContent() {
        return content;
    }

    /**
     * Cập nhật nội dung
     * 
     * @param content nội dung mới (có thể null)
     */
    public void setContent(String content) {
        this.content = content;
    }
}