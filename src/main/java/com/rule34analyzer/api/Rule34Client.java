package com.rule34analyzer.api;

import com.google.gson.*;
import com.rule34analyzer.model.Rule34Post;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Rule34Client {

    private static final String API = "https://api.rule34.xxx/index.php?page=dapi&s=post&q=index&json=1";
    private static final String API_KEY_RESOURCE = "/please_use_your_own_api_key.json";

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    /**
     * Loads api key from json
     */
    private String loadApiKey() throws IOException {

        try (InputStream input = Rule34Client.class.getResourceAsStream(API_KEY_RESOURCE)) {
            if (input == null) {
                throw new IOException("Не найден файл " + API_KEY_RESOURCE + ". Поместите please_use_your_own_api_key.json " + "в src/main/resources.");
            }

            JsonElement root;

            try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                root = JsonParser.parseReader(reader);
            } catch (Exception e) {
                throw new IOException("Не удалось прочитать please_use_your_own_api_key.json: " + e.getMessage(), e);
            }

            if (!root.isJsonObject()) {
                throw new IOException("please_use_your_own_api_key.json должен содержать JSON-объект.");
            }

            JsonObject json = root.getAsJsonObject();

            if (!json.has("api_key")) {
                throw new IOException("В please_use_your_own_api_key.json отсутствует поле \"api_key\".");
            }

            String apiKey = json.get("api_key").getAsString();

            if (apiKey == null || apiKey.isBlank()) {
                throw new IOException("Поле \"api_key\" в please_use_your_own_api_key.json пустое.");
            }

            return apiKey;
        }
    }

    /**
     * Get posts.
     *
     * @param tag tag
     * @param pid page number
     */
    public List<Rule34Post> getPosts(String tag, int pid)
            throws IOException, InterruptedException {

        String encodedTag = URLEncoder.encode(tag, StandardCharsets.UTF_8);

        String apiKey = loadApiKey();

        String url = API
                + apiKey
                + "&limit=1000"
                + "&pid=" + pid
                + "&tags=" + encodedTag
                + "&sort=id:asc";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("User-Agent", "Rule34TagAnalyzer/1.0")
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 429) {
            TimeUnit.SECONDS.sleep(3);
            throw new IOException("HTTP 429: слишком много запросов. " + "Попробуйте повторить позже.");
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP " + response.statusCode());
        }

        return parse(response.body());
    }

    /**
     * Parses JSON and turns into list
     */
    private List<Rule34Post> parse(String body) {

        List<Rule34Post> result = new ArrayList<>();

        JsonElement root;

        try {
            root = JsonParser.parseString(body);
        } catch (Exception e) {
            return result;
        }

        if (!root.isJsonArray()) {
            return result;
        }

        for (JsonElement element : root.getAsJsonArray()) {
            if (!element.isJsonObject()) continue;
            JsonObject object = element.getAsJsonObject();
            if (!object.has("id")) continue;
            long id;
            try {
                id = object.get("id").getAsLong();
            } catch (Exception e) {continue;}

            LocalDate date = parseDate(object);
            if (date == null) continue;

            Set<String> tags = new HashSet<>();
            if (object.has("tags") && !object.get("tags").isJsonNull()) {
                String tagsString = object.get("tags").getAsString();

                for (String tag : tagsString.split("\\s+")) {
                    if (!tag.isBlank()) tags.add(tag);
                }
            }
            result.add(new Rule34Post(id, date, tags));
        }
        return result;
    }

    /**
     * Extracts Data
     */
    private LocalDate parseDate(JsonObject object) {

        String[] fields = {
                "created_at",
                "change",
                "updated_at"
        };

        for (String field : fields) {
            if (!object.has(field) || object.get(field).isJsonNull()) continue;

            String value;

            try {
                value = object.get(field).getAsString();
            } catch (Exception e) {continue;}

            try {
                return Instant.ofEpochSecond(Long.parseLong(value)).atZone(ZoneOffset.UTC).toLocalDate();
            } catch (Exception ignored) {}

            try {
                return OffsetDateTime.parse(value).toLocalDate();
            } catch (Exception ignored) {}

            try {
                if (value.length() >= 10) return LocalDate.parse(value.substring(0, 10));
            } catch (Exception ignored) {}
        }

        return null;
    }
}