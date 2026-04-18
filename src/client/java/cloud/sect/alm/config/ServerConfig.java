package cloud.sect.alm.config;

import cloud.sect.alm.util.EncryptionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
    private static final File CONFIG_FILE = new File("config/alm-servers.json");
    private static final String MASTER_KEY = "AutoLoginModSecretKey2026";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // Map<ServerIP, ServerEntry>
    private static Map<String, ServerEntry> servers = new HashMap<>();
    private static String globalSalt = "";
    
    // Global settings (apply to all servers)
    private static String globalPassword = "";          // password for all servers
    private static boolean globalRegisterMode = false;  // use /register for all
    private static String globalPasswordSalt = "";       // salt for global password
    
    public static class ServerEntry {
        public String ip;
        public String encryptedCommand;
        public boolean isRegisterCommand; // true = /register, false = /login
        public boolean autoRegister;      // auto register on first join
        public String salt;
        
        public ServerEntry(String ip, String encryptedCommand, boolean isRegisterCommand, boolean autoRegister, String salt) {
            this.ip = ip;
            this.encryptedCommand = encryptedCommand;
            this.isRegisterCommand = isRegisterCommand;
            this.autoRegister = autoRegister;
            this.salt = salt;
        }
    }
    
    public static void load() {
        try {
            if (!CONFIG_FILE.exists()) {
                createDefaultConfig();
                return;
            }
            
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                if (root.has("salt")) {
                    globalSalt = root.get("salt").getAsString();
                }
                if (root.has("servers")) {
                    servers = GSON.fromJson(root.get("servers"), new TypeToken<Map<String, ServerEntry>>(){}.getType());
                }
                // Load global settings
                if (root.has("globalPassword")) {
                    globalPassword = root.get("globalPassword").getAsString();
                }
                if (root.has("globalRegisterMode")) {
                    globalRegisterMode = root.get("globalRegisterMode").getAsBoolean();
                }
                if (root.has("globalPasswordSalt")) {
                    globalPasswordSalt = root.get("globalPasswordSalt").getAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void save() {
        try {
            if (globalSalt.isEmpty()) {
                globalSalt = EncryptionUtil.generateSalt();
            }
            
            JsonObject root = new JsonObject();
            root.addProperty("salt", globalSalt);
            root.add("servers", GSON.toJsonTree(servers));
            // Save global settings
            root.addProperty("globalPassword", globalPassword);
            root.addProperty("globalRegisterMode", globalRegisterMode);
            root.addProperty("globalPasswordSalt", globalPasswordSalt);
            
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(root, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void createDefaultConfig() throws Exception {
        globalSalt = EncryptionUtil.generateSalt();
        servers = new HashMap<>();
        save();
    }
    
    // Add or update server
    public static void setServer(String ip, String password, boolean isRegisterCommand, boolean autoRegister) {
        try {
            String salt = EncryptionUtil.generateSalt();
            String encrypted = EncryptionUtil.encrypt(password, MASTER_KEY, salt);
            servers.put(ip, new ServerEntry(ip, encrypted, isRegisterCommand, autoRegister, salt));
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Get server config
    public static ServerEntry getServer(String ip) {
        return servers.get(ip);
    }
    
    // Get decrypted command
    public static String getDecryptedCommand(ServerEntry entry) {
        if (entry == null) return "";
        try {
            return EncryptionUtil.decrypt(entry.encryptedCommand, MASTER_KEY, entry.salt);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    // Remove server
    public static void removeServer(String ip) {
        servers.remove(ip);
        save();
    }
    
    // Get all servers
    public static Map<String, ServerEntry> getAllServers() {
        return new HashMap<>(servers);
    }
    
    // Check if has auto-register enabled
    public static boolean hasAutoRegister(String ip) {
        ServerEntry entry = servers.get(ip);
        return entry != null && entry.autoRegister;
    }
    
    // Set global register mode (apply to all servers)
    public static void setGlobalRegisterMode(boolean enabled) {
        globalRegisterMode = enabled;
        save();
    }
    
    // Get global register mode
    public static boolean isGlobalRegisterMode() {
        return globalRegisterMode;
    }
    
    // Set global password (apply to all servers)
    public static void setGlobalPassword(String password) {
        try {
            if (globalPasswordSalt.isEmpty()) {
                globalPasswordSalt = EncryptionUtil.generateSalt();
            }
            globalPassword = EncryptionUtil.encrypt(password, MASTER_KEY, globalPasswordSalt);
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Get decrypted global password
    public static String getGlobalPassword() {
        if (globalPassword.isEmpty()) return "";
        try {
            return EncryptionUtil.decrypt(globalPassword, MASTER_KEY, globalPasswordSalt);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    // Check if has global password set
    public static boolean hasGlobalPassword() {
        return !globalPassword.isEmpty();
    }
}
