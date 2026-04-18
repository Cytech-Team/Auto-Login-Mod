package cloud.sect.alm.event;

import cloud.sect.alm.config.ServerConfig;
import cloud.sect.alm.keybinding.LoginKeybinding;
import net.minecraft.network.chat.Component;

public class ClientTickEvents {
    public static void register() {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (LoginKeybinding.get().consumeClick()) {
                if (client.player != null) {
                    // Get current server IP
                    String serverIP = getCurrentServerIP(client);
                    
                    // Check for per-server config first (override)
                    ServerConfig.ServerEntry entry = ServerConfig.getServer(serverIP);
                    String password;
                    boolean isRegisterCommand;
                    
                    if (entry != null) {
                        // Use per-server setting
                        password = ServerConfig.getDecryptedCommand(entry);
                        isRegisterCommand = entry.isRegisterCommand;
                    } else if (ServerConfig.hasGlobalPassword()) {
                        // Use global setting
                        password = ServerConfig.getGlobalPassword();
                        isRegisterCommand = ServerConfig.isGlobalRegisterMode();
                    } else {
                        client.player.sendSystemMessage(Component.literal("[Auto-Login] No password configured. Use /alm set <password> or /alm add <ip> <password>"));
                        continue;
                    }
                    
                    if (password.isEmpty()) {
                        client.player.sendSystemMessage(Component.literal("[Auto-Login] No password configured. Use /alm set <password> or /alm add <ip> <password>"));
                        continue;
                    }
                    
                    // Send appropriate command
                    String cmd = isRegisterCommand ? "register " + password : "login " + password;
                    client.player.connection.sendCommand(cmd);
                    client.player.sendSystemMessage(Component.literal("[Auto-Login] Sent /" + (isRegisterCommand ? "register" : "login") + " command"));
                }
            }
        });
    }
    
    private static String getCurrentServerIP(net.minecraft.client.Minecraft client) {
        if (client.getCurrentServer() != null) {
            return client.getCurrentServer().ip;
        }
        return "singleplayer";
    }
}
