package phongtt.type2code;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.List;

/**
 * TypingCaseLoader chịu trách nhiệm tải danh sách bài typing từ URL (JSON API).
 *
 * Chức năng:
 * - Gửi HTTP request đến server
 * - Nhận dữ liệu JSON
 * - Parse JSON thành danh sách TypingCase
 *
 * Lưu ý:
 * - Không xử lý UI
 * - Không xử lý logic typing
 * - Chỉ làm nhiệm vụ "data loading"
 */
public class TypingCaseLoader {

    /**
     * Wrapper dùng để map JSON dạng:
     *
     * {
     *   "tests": [ ... ]
     * }
     */
    static class Wrapper {
        List<TypingCase> tests;
    }

    /**
     * Tải dữ liệu từ URL và parse thành danh sách TypingCase
     *
     * @param url đường dẫn JSON (http/https)
     * @return danh sách bài typing
     *
     * @throws RuntimeException nếu có lỗi network hoặc parse
     */
    public static List<TypingCase> loadFromUrl(final String url) {

        try {
            // =========================
            // Tạo HTTP client (có timeout)
            // =========================
            final HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            // =========================
            // Tạo request GET
            // =========================
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // =========================
            // Gửi request và nhận response
            // =========================
            final HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // =========================
            // Kiểm tra HTTP status
            // =========================
            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP error: " + response.statusCode());
            }

            // =========================
            // Parse JSON → object
            // =========================
            final Gson gson = new Gson();
            final Wrapper wrapper = gson.fromJson(response.body(), Wrapper.class);

            // =========================
            // Trả về danh sách (tránh null)
            // =========================
            return (wrapper != null && wrapper.tests != null)
                    ? wrapper.tests
                    : List.of();

        } catch (IOException e) {
            throw new RuntimeException("Lỗi IO khi gọi API", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // giữ trạng thái interrupt
            throw new RuntimeException("Request bị gián đoạn", e);

        } catch (Exception e) {
            throw new RuntimeException("Không thể load dữ liệu từ URL", e);
        }
    }
}