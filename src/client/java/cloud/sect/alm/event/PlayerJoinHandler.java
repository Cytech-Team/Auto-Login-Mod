package cloud.sect.alm.event;

import cloud.sect.alm.config.ServerConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class PlayerJoinHandler {
    
    public static void register() {
        // Show status when joining a server
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            client.execute(() -> {
                if (client.player == null) return;
                
                String serverIP = getCurrentServerIP(client);
                ServerConfig.ServerEntry entry = ServerConfig.getServer(serverIP);
                
                // Check status
                if (entry != null) {
                    // Per-server config found
                    String type = entry.isRegisterCommand ? "/register" : "/login";
                    client.player.sendSystemMessage(
                        Component.literal("[Auto-Login] Server configured: " + type + " (Per-Server)")
                    );
                } else if (ServerConfig.hasGlobalPassword()) {
                    // Global config found
                    String type = ServerConfig.isGlobalRegisterMode() ? "/register" : "/login";
                    client.player.sendSystemMessage(
                        Component.literal("[Auto-Login] Server configured: " + type + " (Global)")
                    );
                } else {
                    // No config
                    client.player.sendSystemMessage(
                        Component.literal("[Auto-Login] NOT configured for this server! Use /alm set <password>")
                    );
                    return; // Don't auto-type if not configured
                }
                
                // Auto-login if enabled
                if (!ServerConfig.isAutoTypeEnabled()) {
                    client.player.sendSystemMessage(
                        Component.literal("[Auto-Login] Auto-type is OFF - Press F9 to login manually")
                    );
                    return;
                }
                
                // Get password and type
                String password;
                boolean isRegisterCommand;
                
                if (entry != null) {
                    password = ServerConfig.getDecryptedCommand(entry);
                    isRegisterCommand = entry.isRegisterCommand;
                } else {
                    password = ServerConfig.getGlobalPassword();
                    isRegisterCommand = ServerConfig.isGlobalRegisterMode();
                }
                
                if (password.isEmpty()) return;
                
                // Send command after delay
                client.player.sendSystemMessage(
                    Component.literal("[Auto-Login] Auto-typing in 1 second...")
                );
                
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        client.execute(() -> {
                            if (client.player != null) {
                                String cmd = isRegisterCommand ? "register " + password : "login " + password;
                                client.player.connection.sendCommand(cmd);
                                client.player.sendSystemMessage(
                                    Component.literal("[Auto-Login] Auto-sent /" + (isRegisterCommand ? "register" : "login") + " command")
                                );
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        });
    }
    
    private static String getCurrentServerIP(Minecraft client) {
        if (client.getCurrentServer() != null) {
            return client.getCurrentServer().ip;
        }
        return "singleplayer";
    }
}
