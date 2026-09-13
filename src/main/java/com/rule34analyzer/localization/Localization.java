package com.rule34analyzer.localization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Localization {
    private final Map<String, String> strings;

    public Localization(String language) {
        String path = "/lang/lang." + language + ".json";
        try (InputStream in = Localization.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalArgumentException("Missing language: " + language);
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                strings = new Gson().fromJson(reader, new TypeToken<Map<String,String>>(){}.getType());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key) {
        return strings.getOrDefault(key, key);
    }
}
