package com.rule34analyzer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DIRECTORY = Paths.get(System.getenv().getOrDefault("APPDATA", System.getProperty("user.home")), ".r34ta");
    private static final Path CONFIG_FILE = DIRECTORY.resolve("config.json");

    public static class Config{
        public String language = "ru";
        public String api_key = "";
    }

    public static Path getConfigPath(){
        return CONFIG_FILE;
    }

    public static boolean exists() {
        return Files.isRegularFile(CONFIG_FILE);
    }

    public static Config load() throws IOException {
        if(!exists()) return new Config();

        String json = Files.readString(CONFIG_FILE, StandardCharsets.UTF_8);

        Config config = GSON.fromJson(json, Config.class);

        if(config == null) throw new IOException("Invalid config.json");

        if(!"ru".equals(config.language) && !"en".equals(config.language)) config.language = "ru";

        if(config.api_key == null) config.api_key = "";

        return config;
    }

    public static void save(Config config) throws IOException{
        Files.createDirectories(DIRECTORY);
        Files.writeString(CONFIG_FILE, GSON.toJson(config), StandardCharsets.UTF_8);
    }
}
