package phongtt.type2code;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 *
 * @author Phong
 */
public class TypingCaseLoader {

    static class Wrapper {
        List<TypingCase> tests;
    }

    public static List<TypingCase> loadFromUrl(String url) {

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP error: " + response.statusCode());
            }

            Gson gson = new Gson();
            Wrapper wrapper = gson.fromJson(response.body(), Wrapper.class);

            return wrapper.tests != null ? wrapper.tests : List.of();

        } catch (IOException | InterruptedException | RuntimeException e) {
            throw new RuntimeException("Failed to load tests from URL", e);
        }
    }
}
