package cloud.sect.alm.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.*;

public class ForgeStandaloneConfig {
    private static final File CONFIG_FILE = new File("config/alm-standalone.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject configData = new JsonObject();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            configData = GSON.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(configData, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ระบบจัดการข้อมูลแบบเบ็ดเสร็จในตัวเดียว
    public static void set(String key, String value) {
        configData.addProperty(key, value);
        save();
    }

    public static String get(String key, String def) {
        return configData.has(key) ? configData.get(key).getAsString() : def;
    }
}
